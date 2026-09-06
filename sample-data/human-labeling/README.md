# CGDI human-labeling data root

This directory is the long-term import structure for the CGDI labeling application.
The application scans it recursively.

```text
human-labeling/
├── calibration/
│   └── CGDI_Calibration_Batch01.csv          # 10 filings / 231 paragraphs
├── blind_pilot/
│   └── CGDI_Blind_Pilot_Batch01.csv          # 20 filings / 457 paragraphs
└── formal_validation/
    └── CGDI_Formal_Validation_Batch01.csv     # header only; 70 filings to be selected later
```

All three files use the same standardized columns:

```text
company
fiscal_year
master_cik
filename
paragraph_id
section_hint
heading_context
word_count
table_like_flag
text
label_queue
batch_order
batch
```

The importer requires only `filename`, `company`, `fiscal_year`, `paragraph_id`, and `text`.
The remaining columns are retained for reproducibility and batch management.

The formal-validation file intentionally contains only the header until the 70-filings sample is selected.
