# CGDI Labeler v0.5 implementation notes

## 1. Reset-on-startup development mode

Added `DevelopmentDatabaseResetRunner` with startup order 5.

When `cgdi.dev.reset-on-startup=true`, it executes a PostgreSQL `TRUNCATE ... RESTART IDENTITY CASCADE` on mutable application/study tables:

- app_user
- filing
- paragraph
- assignment
- annotation
- annotation_dimension
- annotation_attribute

It intentionally preserves Flyway migration history and the two ontology definition tables (`dimension_def`, `attribute_def`).

Startup order is now:

1. Flyway migration/check
2. `DevelopmentDatabaseResetRunner` — order 5
3. `BootstrapDataRunner` — order 10
4. `FilingFolderImportRunner` — order 20

Default DEV behavior is reset ON. PROD behavior is reset OFF.

This means the user should not manually delete PostgreSQL tables between project redeployments.

## 2. Long-term human-labeling folder structure

Replaced the old single `sample-data/calibration` folder with:

```text
sample-data/human-labeling/
├── calibration/
│   └── CGDI_Calibration_Batch01.csv
├── blind_pilot/
│   └── CGDI_Blind_Pilot_Batch01.csv
└── formal_validation/
    └── CGDI_Formal_Validation_Batch01.csv
```

Data included:

- Calibration: 10 filings / 231 paragraphs
- Blind pilot: 20 filings / 457 paragraphs
- Formal validation: header only / 0 rows until the 70 filings are selected
- Current total: 30 filings / 688 paragraphs

All batch files use the same 13-column schema. The importer ignores research-only extra columns safely.

The DEV importer now points at `./sample-data/human-labeling` with recursive scanning enabled.

## 3. No Flyway migration added

No schema change is required for these changes. V1–V3 remain unchanged.

## 4. Safety note

`CGDI_RESET_ON_STARTUP=true` is intentionally convenient for current software testing, but it must be changed to `false` before real labelers start producing research annotations.
