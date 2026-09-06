# CGDI Labeler v0.5.3 implementation notes

- Assignment page now exposes the research dataset stage for every filing.
- Stage is derived from the existing `filing.source_path`; no Flyway migration is required.
- Stage filter defaults to **Calibration**, so the first admin view shows only the 10 calibration filings.
- Filters: Calibration / Blind pilot / Formal validation / All.
- Changing the stage filter also proposes the matching batch code (`CALIBRATION_01`, `BLIND_PILOT_01`, `FORMAL_VALIDATION_01`).
- The page shows stage counts, e.g. Calibration 10 · Blind pilot 20 · Formal validation 0.
- `Select all visible` therefore safely selects only the current stage.
- Labelers still see only filings assigned to them, not all imported filings.
- No database reset behavior changed; reset remains opt-in and false by default.
