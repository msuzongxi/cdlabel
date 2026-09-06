package edu.cgdi.labeler.service;

import edu.cgdi.labeler.mapper.ImportMapper;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;

@Service
public class FilingFolderImportService {

    private static final Logger log = LoggerFactory.getLogger(FilingFolderImportService.class);

    private final ImportMapper importMapper;

    @Value("${cgdi.filing-import.folder:}")
    private String folder;

    @Value("${cgdi.filing-import.recursive:false}")
    private boolean recursive;

    private volatile ImportReport lastReport = ImportReport.empty("");

    public FilingFolderImportService(ImportMapper importMapper) {
        this.importMapper = importMapper;
    }

    public synchronized ImportReport importNow() {
        String configured = folder == null ? "" : folder.trim();
        if (configured.isBlank()) {
            lastReport = new ImportReport("", 0, 0, 0,
                List.of("No filing folder is configured. Set CGDI_FILING_FOLDER or cgdi.filing-import.folder."));
            return lastReport;
        }

        Path root = Path.of(configured).toAbsolutePath().normalize();
        if (!Files.isDirectory(root)) {
            lastReport = new ImportReport(root.toString(), 0, 0, 0,
                List.of("Configured filing folder does not exist or is not a directory."));
            return lastReport;
        }

        List<String> errors = new ArrayList<>();
        int filesRead = 0;
        int paragraphs = 0;
        Map<String, Long> filingIds = new LinkedHashMap<>();
        Set<String> clearedFilenames = new HashSet<>();

        try (Stream<Path> stream = recursive ? Files.walk(root) : Files.list(root)) {
            List<Path> files = stream
                .filter(Files::isRegularFile)
                .filter(isCsv())
                .sorted()
                .toList();

            for (Path file : files) {
                try {
                    ImportCounts counts = importCsv(file, filingIds, clearedFilenames);
                    filesRead++;
                    paragraphs += counts.paragraphs();
                } catch (Exception e) {
                    String message = file.getFileName() + ": " + e.getMessage();
                    errors.add(message);
                    log.warn("CGDI filing import failed for {}", file, e);
                }
            }
        } catch (IOException e) {
            errors.add(e.getMessage());
        }

        lastReport = new ImportReport(
            root.toString(), filesRead, filingIds.size(), paragraphs, List.copyOf(errors)
        );
        return lastReport;
    }

    public ImportReport getStatus() {
        String configured = folder == null ? "" : folder.trim();
        if (lastReport.folder().isBlank() && !configured.isBlank()) {
            return new ImportReport(
                Path.of(configured).toAbsolutePath().normalize().toString(),
                lastReport.filesRead(), lastReport.filings(), lastReport.paragraphs(), lastReport.errors()
            );
        }
        return lastReport;
    }

    private ImportCounts importCsv(
        Path path,
        Map<String, Long> filingIds,
        Set<String> clearedFilenames
    ) throws IOException {
        int paragraphs = 0;

        try (InputStream raw = Files.newInputStream(path);
             InputStream input = path.getFileName().toString().toLowerCase(Locale.ROOT).endsWith(".gz")
                 ? new GZIPInputStream(raw) : raw;
             BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8));
             CSVParser parser = CSVFormat.DEFAULT.builder()
                 .setHeader()
                 .setSkipHeaderRecord(true)
                 .setIgnoreEmptyLines(true)
                 .build()
                 .parse(reader)) {

            int rowNumber = 0;
            for (CSVRecord record : parser) {
                rowNumber++;
                Map<String, String> row = normalize(record.toMap());

                String filename = required(row, "filename", path, rowNumber);
                String company = required(row, "company", path, rowNumber);
                String fiscalYearText = required(row, "fiscal_year", path, rowNumber);
                String paragraphId = required(row, "paragraph_id", path, rowNumber);
                String text = required(row, "text", path, rowNumber);

                Integer fiscalYear = parseInteger(fiscalYearText, "fiscal_year", path, rowNumber);
                String masterCik = blankToNull(row.get("master_cik"));
                String sectionHint = blankToNull(row.get("section_hint"));
                String headingContext = blankToNull(row.get("heading_context"));
                Integer wordCount = parseOptionalInteger(row.get("word_count"));
                Boolean tableLike = parseOptionalBoolean(row.get("table_like_flag"));

                Long filingId = filingIds.get(filename);
                if (filingId == null) {
                    filingId = importMapper.upsertFiling(
                        filename,
                        masterCik,
                        company,
                        fiscalYear,
                        path.toAbsolutePath().normalize().toString()
                    );
                    filingIds.put(filename, filingId);
                }

                if (clearedFilenames.add(filename)) {
                    importMapper.clearSelectedFlag(filingId);
                }

                importMapper.upsertParagraph(
                    filingId,
                    paragraphId,
                    text,
                    sectionHint,
                    headingContext,
                    paragraphSortOrder(paragraphId, rowNumber),
                    wordCount,
                    tableLike
                );
                paragraphs++;
            }
        }

        return new ImportCounts(paragraphs);
    }

    private Predicate<Path> isCsv() {
        return p -> {
            String n = p.getFileName().toString().toLowerCase(Locale.ROOT);
            return n.endsWith(".csv") || n.endsWith(".csv.gz");
        };
    }

    private Map<String, String> normalize(Map<String, String> source) {
        Map<String, String> out = new HashMap<>();
        source.forEach((key, value) -> out.put(normalizeKey(key), value));
        return out;
    }

    private String normalizeKey(String key) {
        if (key == null) return "";
        return key.replace("\uFEFF", "")
            .trim()
            .toLowerCase(Locale.ROOT)
            .replace(' ', '_')
            .replace('-', '_');
    }

    private String required(Map<String, String> row, String key, Path path, int rowNumber) {
        String value = row.get(key);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                "Missing required column/value '" + key + "' at row " + rowNumber + " in " + path.getFileName()
            );
        }
        return value.trim();
    }

    private Integer parseInteger(String value, String key, Path path, int rowNumber) {
        try {
            return Integer.valueOf(value.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                "Invalid integer '" + key + "' at row " + rowNumber + " in " + path.getFileName()
            );
        }
    }

    private Integer parseOptionalInteger(String value) {
        if (value == null || value.isBlank()) return null;
        try {
            return Integer.valueOf(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Boolean parseOptionalBoolean(String value) {
        if (value == null || value.isBlank()) return null;
        String v = value.trim().toLowerCase(Locale.ROOT);
        return v.equals("1") || v.equals("true") || v.equals("yes") || v.equals("y");
    }

    private int paragraphSortOrder(String paragraphId, int fallback) {
        String digits = paragraphId.replaceAll("\\D+", "");
        if (!digits.isBlank()) {
            try {
                return Integer.parseInt(digits);
            } catch (NumberFormatException ignored) {
                // fall through
            }
        }
        return fallback;
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private record ImportCounts(int paragraphs) {}

    public record ImportReport(
        String folder,
        int filesRead,
        int filings,
        int paragraphs,
        List<String> errors
    ) {
        static ImportReport empty(String folder) {
            return new ImportReport(folder, 0, 0, 0, List.of());
        }
    }
}
