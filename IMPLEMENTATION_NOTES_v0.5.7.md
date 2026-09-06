# CGDI Labeler v0.5.7

## Cross-filing paragraph comparison queue

Adds a new supervisor/admin page:

- `/review/paragraphs`

This view is status-oriented rather than filing-oriented. A supervisor/admin selects two LABELER accounts and a research stage, then reviews paragraphs across every filing assigned to both selected labelers.

Default workflow:

- Stage: `CALIBRATION`
- Status: `DISAGREEMENT`
- Pair: first two assigned LABELER accounts by user id (normally `johndoor` and `marykey` after a clean reset)

Filters:

- status: disagreement / agreement / incomplete / all
- difference: eligibility, ontology gap, A1, A2, A3, B1, B2, C1, attributes
- free-text search over company, CIK, filing, paragraph metadata and paragraph text

The queue shows counts for agreements, disagreements, incomplete paragraphs, common filings, and both-submitted paragraphs. Agreement percentage is operational only; it is not presented as the study's final reliability statistic.

Clicking a queue item opens the existing side-by-side gate/dimension/attribute comparison without requiring a filing-level navigation step. Previous/Next buttons step through the currently filtered queue. `Open filing view` remains available when filing context is needed.

The existing `/review` filing-progress view remains available as `Filing view`.

## Pair selection

The queue works with any two LABELER accounts that have assignments. This supports calibration comparisons such as:

- `johndoor` vs `marykey`
- `johndoor` vs `dummy`
- `marykey` vs `dummy`

Only filings assigned to both selected users are included.

## Bootstrap users

The development bootstrap remains:

- `admin` / ADMIN
- `johndoor` / LABELER
- `marykey` / LABELER
- `dummy` / LABELER
- `donglai` / SUPERVISOR

`dummy` now uses the configurable property:

`cgdi.bootstrap.labeler3-password=${CGDI_LABELER3_PASSWORD:dummy}`

The safe development reset default remains `false`.

## Database

No Flyway migration is required for v0.5.7. The new review queue reads the existing filing, assignment, annotation, dimension, and attribute tables.
