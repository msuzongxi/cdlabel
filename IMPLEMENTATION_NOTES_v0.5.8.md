# CGDI Labeler v0.5.8

## Admin-only CSV export

New routes:

- `GET /exports` — admin-only export page.
- `GET /exports/csv?stage=CALIBRATION&labelerId=&status=SUBMITTED` — downloads UTF-8 CSV.

Export filters:

- Stage: CALIBRATION / BLIND_PILOT / FORMAL_VALIDATION / OTHER / ALL
- Labeler: one active assigned LABELER or all labelers
- Annotation status: SUBMITTED / MODIFIED / ALL

Each row is one paragraph × labeler annotation. It includes source metadata, gates, eligibility, ontology gap, all six dimensions, all 32 attributes, optional attribute values and dimension evidence metadata, notes, status, and submitted timestamp.

Security: `/exports` and `/exports/**` require ROLE_ADMIN. Supervisor access is denied.

No Flyway migration is required.
