package edu.cgdi.labeler.service;

import edu.cgdi.labeler.mapper.ExportMapper;
import edu.cgdi.labeler.mapper.OntologyMapper;
import edu.cgdi.labeler.model.AssignableUser;
import edu.cgdi.labeler.model.AttributeDef;
import edu.cgdi.labeler.model.DimensionDef;
import edu.cgdi.labeler.model.ExportAnnotationRow;
import edu.cgdi.labeler.model.ExportAttributeRow;
import edu.cgdi.labeler.model.ExportDimensionRow;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Service
public class ExportService {

    private static final Set<String> VALID_STAGES = Set.of(
        "CALIBRATION", "BLIND_PILOT", "FORMAL_VALIDATION", "OTHER", "ALL"
    );
    private static final Set<String> VALID_STATUSES = Set.of("SUBMITTED", "MODIFIED", "ALL");

    private final ExportMapper exportMapper;
    private final OntologyMapper ontologyMapper;

    public ExportService(ExportMapper exportMapper, OntologyMapper ontologyMapper) {
        this.exportMapper = exportMapper;
        this.ontologyMapper = ontologyMapper;
    }

    public List<AssignableUser> labelers() {
        return exportMapper.findExportLabelers();
    }

    public ExportResult exportCsv(String requestedStage, Long labelerId, String requestedStatus) {
        String stage = normalizeStage(requestedStage);
        String status = normalizeStatus(requestedStatus);
        validateLabeler(labelerId);

        List<ExportAnnotationRow> baseRows = exportMapper.findAnnotationRows(stage, labelerId, status);
        List<ExportDimensionRow> dimensionRows = exportMapper.findDimensionRows(stage, labelerId, status);
        List<ExportAttributeRow> attributeRows = exportMapper.findAttributeRows(stage, labelerId, status);
        List<DimensionDef> dimensions = ontologyMapper.findDimensions();
        List<AttributeDef> attributes = ontologyMapper.findAttributes();

        Map<Long, Map<String, ExportDimensionRow>> dimensionsByAnnotation = new LinkedHashMap<>();
        for (ExportDimensionRow row : dimensionRows) {
            dimensionsByAnnotation
                .computeIfAbsent(row.getAnnotationId(), ignored -> new LinkedHashMap<>())
                .put(row.getDimensionCode(), row);
        }

        Map<Long, Map<String, ExportAttributeRow>> attributesByAnnotation = new LinkedHashMap<>();
        for (ExportAttributeRow row : attributeRows) {
            attributesByAnnotation
                .computeIfAbsent(row.getAnnotationId(), ignored -> new LinkedHashMap<>())
                .put(row.getAttributeCode(), row);
        }

        List<String> headers = buildHeaders(dimensions, attributes);
        byte[] bytes = renderCsv(headers, baseRows, dimensions, attributes,
            dimensionsByAnnotation, attributesByAnnotation);

        String labelerPart = labelerId == null ? "ALL_LABELERS" : usernameFor(labelerId);
        String filename = "cgdi_" + stage + "_" + labelerPart + "_" + status + "_"
            + LocalDate.now() + ".csv";

        return new ExportResult(filename, bytes, baseRows.size());
    }

    private List<String> buildHeaders(List<DimensionDef> dimensions, List<AttributeDef> attributes) {
        List<String> headers = new ArrayList<>(List.of(
            "dataset_stage",
            "batch_code",
            "filing_id",
            "filename",
            "master_cik",
            "company",
            "fiscal_year",
            "paragraph_db_id",
            "paragraph_id",
            "section_hint",
            "heading_context",
            "sort_order",
            "text",
            "labeler_id",
            "labeler",
            "annotation_id",
            "annotation_status",
            "submitted_at",
            "gate1_cyber_nexus",
            "gate2_registrant_attribution",
            "gate3_governance_process",
            "eligible",
            "ontology_gap",
            "gap_note",
            "notes"
        ));

        for (DimensionDef dimension : dimensions) {
            String code = dimension.getCode();
            headers.add(code);
            headers.add(code + "_evidence_text");
            headers.add(code + "_confidence");
            headers.add(code + "_ambiguity_note");
        }

        for (AttributeDef attribute : attributes) {
            headers.add(attribute.getCode());
            headers.add(attribute.getCode() + "_value");
        }
        return headers;
    }

    private byte[] renderCsv(
        List<String> headers,
        List<ExportAnnotationRow> baseRows,
        List<DimensionDef> dimensions,
        List<AttributeDef> attributes,
        Map<Long, Map<String, ExportDimensionRow>> dimensionsByAnnotation,
        Map<Long, Map<String, ExportAttributeRow>> attributesByAnnotation
    ) {
        try {
            StringWriter writer = new StringWriter();
            CSVFormat format = CSVFormat.DEFAULT.builder()
                .setHeader(headers.toArray(String[]::new))
                .build();

            try (CSVPrinter printer = new CSVPrinter(writer, format)) {
                for (ExportAnnotationRow row : baseRows) {
                    List<Object> values = new ArrayList<>();
                    values.add(blank(row.getDatasetStage()));
                    values.add(blank(row.getBatchCode()));
                    values.add(row.getFilingId());
                    values.add(blank(row.getFilename()));
                    values.add(blank(row.getMasterCik()));
                    values.add(blank(row.getCompany()));
                    values.add(row.getFiscalYear());
                    values.add(row.getParagraphDbId());
                    values.add(blank(row.getParagraphId()));
                    values.add(blank(row.getSectionHint()));
                    values.add(blank(row.getHeadingContext()));
                    values.add(row.getSortOrder());
                    values.add(blank(row.getText()));
                    values.add(row.getUserId());
                    values.add(blank(row.getUsername()));
                    values.add(row.getAnnotationId());
                    values.add(blank(row.getStatus()));
                    values.add(row.getSubmittedAt() == null ? "" : row.getSubmittedAt().toString());
                    values.add(blank(row.getCyberNexus()));
                    values.add(blank(row.getRegistrantAttribution()));
                    values.add(blank(row.getGovernanceProcess()));
                    values.add(binary(row.getEligible()));
                    values.add(binary(row.getOntologyGap()));
                    values.add(blank(row.getGapNote()));
                    values.add(blank(row.getNotes()));

                    Map<String, ExportDimensionRow> dimMap = dimensionsByAnnotation
                        .getOrDefault(row.getAnnotationId(), Map.of());
                    for (DimensionDef dimension : dimensions) {
                        ExportDimensionRow dim = dimMap.get(dimension.getCode());
                        values.add(dim == null ? 0 : 1);
                        values.add(dim == null ? "" : blank(dim.getEvidenceText()));
                        values.add(dim == null ? "" : blank(dim.getConfidence()));
                        values.add(dim == null ? "" : blank(dim.getAmbiguityNote()));
                    }

                    Map<String, ExportAttributeRow> attrMap = attributesByAnnotation
                        .getOrDefault(row.getAnnotationId(), Map.of());
                    for (AttributeDef attribute : attributes) {
                        ExportAttributeRow attr = attrMap.get(attribute.getCode());
                        values.add(attr != null && Boolean.TRUE.equals(attr.getPresent()) ? 1 : 0);
                        values.add(attr == null ? "" : blank(attr.getValueText()));
                    }

                    printer.printRecord(values);
                }
            }

            // UTF-8 BOM makes the file open cleanly in Excel while preserving ordinary CSV semantics.
            return ("\uFEFF" + writer).getBytes(StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to generate CSV export", e);
        }
    }

    private String normalizeStage(String requestedStage) {
        String stage = requestedStage == null || requestedStage.isBlank()
            ? "CALIBRATION"
            : requestedStage.trim().toUpperCase();
        if (!VALID_STAGES.contains(stage)) {
            throw new IllegalArgumentException("Unknown export stage: " + requestedStage);
        }
        return stage;
    }

    private String normalizeStatus(String requestedStatus) {
        String status = requestedStatus == null || requestedStatus.isBlank()
            ? "SUBMITTED"
            : requestedStatus.trim().toUpperCase();
        if (!VALID_STATUSES.contains(status)) {
            throw new IllegalArgumentException("Unknown annotation status: " + requestedStatus);
        }
        return status;
    }

    private void validateLabeler(Long labelerId) {
        if (labelerId == null) return;
        boolean valid = labelers().stream().anyMatch(user -> Objects.equals(user.getId(), labelerId));
        if (!valid) {
            throw new IllegalArgumentException("Selected user is not an active assigned LABELER.");
        }
    }

    private String usernameFor(Long labelerId) {
        return labelers().stream()
            .filter(user -> Objects.equals(user.getId(), labelerId))
            .map(AssignableUser::getUsername)
            .findFirst()
            .orElse("LABELER_" + labelerId);
    }

    private Object binary(Boolean value) {
        if (value == null) return "";
        return value ? 1 : 0;
    }

    private String blank(String value) {
        return value == null ? "" : value;
    }

    public record ExportResult(String filename, byte[] bytes, int rowCount) {}
}
