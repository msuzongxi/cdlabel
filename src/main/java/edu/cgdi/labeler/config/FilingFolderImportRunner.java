package edu.cgdi.labeler.config;

import edu.cgdi.labeler.service.FilingFolderImportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(20)
@ConditionalOnProperty(name = "cgdi.filing-import.enabled", havingValue = "true", matchIfMissing = true)
public class FilingFolderImportRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(FilingFolderImportRunner.class);
    private final FilingFolderImportService importService;

    public FilingFolderImportRunner(FilingFolderImportService importService) {
        this.importService = importService;
    }

    @Override
    public void run(String... args) {
        var report = importService.importNow();
        if (!report.folder().isBlank()) {
            log.info(
                "CGDI folder import: folder={}, files={}, filings={}, paragraphs={}, errors={}",
                report.folder(), report.filesRead(), report.filings(), report.paragraphs(), report.errors().size()
            );
        }
    }
}
