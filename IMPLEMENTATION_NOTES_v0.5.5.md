# CGDI Labeler v0.5.5

## In-context dimension / attribute guidance

- Added Flyway `V4__labeler_guidance.sql`.
- Existing `dimension_def` rows gain: `definition`, `positive_example`, `negative_example`, `boundary_note`.
- Existing `attribute_def` rows gain: `meaning`, `positive_example`, `negative_example`.
- `/api/ontology` now returns those fields.
- The labeling UI shows an ⓘ helper beside every dimension and every attribute.
- Hover, keyboard focus, or click shows the meaning plus positive/negative examples; dimensions also show a boundary note.
- No scoring information or machine predictions are exposed to labelers.

## Supervisor / admin comparison

New read-only routes:

- `/review` — filing-level comparison dashboard.
- `/review/filing/{filingId}` — paragraph-level side-by-side comparison.
- `/api/review/filings`
- `/api/review/filings/{filingId}`

Comparison status:

- `AGREEMENT`: all assigned labelers have submitted and coded gates, ontology-gap state, dimensions, and attributes consistently.
- `DISAGREEMENT`: all assigned labelers have submitted but at least one of those coding elements differs.
- `INCOMPLETE`: at least one assigned labeler has not submitted the paragraph.

The filing comparison page defaults to disagreements when any exist and provides filters for agreement, incomplete, and all paragraphs. It renders the paragraph text once and a side-by-side decision matrix for the two labelers.

Original labeler annotations are never overwritten by the comparison page. Adjudicated GOLD is intentionally a separate future layer.

## Database upgrade

Do not delete existing PostgreSQL tables. Flyway runs V4 once and preserves existing annotations and assignments.
