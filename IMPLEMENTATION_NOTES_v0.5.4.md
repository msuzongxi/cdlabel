# CGDI Labeler v0.5.4

## Assignment table visibility fix

- The assignment table header is no longer sticky. In v0.5.3, when the page was scrolled, the sticky header could visually cover the first filing row, so the status correctly said 10 visible while only 9 rows appeared on screen.
- Added a visible row number column so Calibration clearly shows rows 1–10 and Blind pilot rows 1–20.
- CSV parsing/import logic is unchanged; the missing-on-screen row was not caused by the CSV header.

## Existing database note

If the page says 31 total filings, this is an old/stale database row from a previous development import. The current bundled dataset contains exactly 30 filings (10 calibration + 20 blind pilot). During testing, run once with `CGDI_RESET_ON_STARTUP=true`, then remove/set it back to false. Normal restarts must use false so real annotations are preserved.
