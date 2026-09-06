package edu.cgdi.labeler.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Development-only convenience reset.
 *
 * Flyway migrations and ontology definition tables are intentionally preserved.
 * Mutable study/application data are cleared before bootstrap users and CSV data
 * are recreated by the later startup runners.
 */
@Component
@Order(5)
@ConditionalOnProperty(name = "cgdi.dev.reset-on-startup", havingValue = "true")
public class DevelopmentDatabaseResetRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DevelopmentDatabaseResetRunner.class);
    private final JdbcTemplate jdbcTemplate;

    public DevelopmentDatabaseResetRunner(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) {
        log.warn("CGDI DEV RESET is enabled: clearing users, filings, paragraphs, assignments, and annotations.");
        jdbcTemplate.execute("""
            TRUNCATE TABLE
                annotation_attribute,
                annotation_dimension,
                annotation,
                assignment,
                paragraph,
                filing,
                app_user
            RESTART IDENTITY CASCADE
            """);
        log.warn("CGDI DEV RESET complete. Flyway history and ontology definitions were preserved.");
    }
}
