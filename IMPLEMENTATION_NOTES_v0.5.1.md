# CGDI Labeler v0.5.1 implementation notes

## Safe restart change

The development reset mechanism remains available, but its default changed from **ON** to **OFF**.

```properties
cgdi.dev.reset-on-startup=${CGDI_RESET_ON_STARTUP:false}
```

Normal application restarts now preserve all labeling data. No PostgreSQL tables need to be manually dropped or recreated when the project is re-imported or redeployed. Flyway continues to use `flyway_schema_history` and only applies migrations that have not already been applied.

To intentionally reset the development working data, start once with:

```text
CGDI_RESET_ON_STARTUP=true
```

Then remove/set the variable back to false before the next normal restart.

## Long-term data layout

Unchanged from v0.5:

- `sample-data/human-labeling/calibration/` — 10 filings / 231 paragraphs
- `sample-data/human-labeling/blind_pilot/` — 20 filings / 457 paragraphs
- `sample-data/human-labeling/formal_validation/` — header-only placeholder for the future 70 filings

The importer remains recursive and the future formal-validation CSV uses the same schema.
