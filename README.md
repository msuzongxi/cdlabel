# CGDI Labeler v0.5.2 — role-aware login redirect + safe restart + long-term data batches

## Login redirect behavior

After a successful login, the application now redirects by role:

- `LABELER` → `/label`
- `ADMIN` → `/assignments`
- `SUPERVISOR` → `/assignments`

This redirect is explicit and does not depend on the URL that happened to trigger the login form.

Human-labeling application for CGDI calibration, blind-pilot, and formal-validation work.

## Stack

- Java 25
- Spring Boot 4.1.1
- Spring MVC + Spring Security
- MyBatis 4.0.0 using Java annotations only
- PostgreSQL + Flyway
- Thymeleaf + jQuery 3.7.1
- Apache Commons CSV

No JPA, Hibernate, H2, or MyBatis mapper XML.

## Important: database behavior in the default DEV profile

The project defaults to the `dev` Spring profile. Starting with v0.5.1, the safe default is:

```properties
cgdi.dev.reset-on-startup=${CGDI_RESET_ON_STARTUP:false}
```

Therefore a normal Eclipse/application restart **preserves** users, assignments, annotations, and labeling progress. The folder importer may re-read the bundled CSV files, but it upserts filings/paragraphs rather than deleting human labels.

```text
normal start / restart
    ↓
Flyway checks V1/V2/V3 (already-applied migrations are not rerun)
    ↓
bootstrap ensures development users exist
    ↓
folder importer upserts bundled labeling data
    ↓
existing assignments + annotations remain
```

So **do not manually delete PostgreSQL tables when re-importing/redeploying this project**. Flyway already tracks applied migrations in `flyway_schema_history`.

### Deliberate full development reset

Only when you intentionally want a completely clean working database, start the application once with:

```text
CGDI_RESET_ON_STARTUP=true
```

That one start clears the mutable application/study tables, resets identities, recreates the development users, and reimports the labeling files. It preserves:

```text
flyway_schema_history
dimension_def
attribute_def
```

After the clean start, immediately remove the environment variable or set:

```text
CGDI_RESET_ON_STARTUP=false
```

Otherwise leaving `true` in an Eclipse Run Configuration would intentionally reset again on the next restart.

The `prod` profile always has reset disabled.

## Long-term labeling data structure

The bundled DEV import root is:

```text
sample-data/human-labeling/
├── calibration/
│   └── CGDI_Calibration_Batch01.csv
│       10 filings / 231 paragraphs
│
├── blind_pilot/
│   └── CGDI_Blind_Pilot_Batch01.csv
│       20 filings / 457 paragraphs
│
└── formal_validation/
    └── CGDI_Formal_Validation_Batch01.csv
        header only for now; 70 filings will be added later
```

Total currently loaded on a fresh DEV start:

```text
30 filings
688 selected paragraphs
```

The importer scans the structure recursively. The future 70-filings file uses the same schema, so adding it later requires no Java or database-schema change.

For an external research folder, set for example:

```text
CGDI_FILING_FOLDER=C:\research\filing\cgdi_human_labeling
CGDI_FILING_IMPORT_RECURSIVE=true
```

A recommended external structure is the same:

```text
C:\research\filing\cgdi_human_labeling\
├── calibration\
├── blind_pilot\
└── formal_validation\
```

## Standard labeling CSV schema

All batch files now use the same columns:

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

Importer-required columns are only:

```text
filename
company
fiscal_year
paragraph_id
text
```

The rest are retained for research reproducibility and batch organization.

## Batch assignment

Importing a filing does not put it into a labeler's queue. Use:

```text
http://localhost:8080/assignments
```

Suggested batch codes:

```text
CALIBRATION_01
BLIND_PILOT_01
FORMAL_VALIDATION_01
```

You can therefore keep all 30 current filings loaded while assigning only the 10 calibration filings first.

## Labeling workflow

Paragraph states:

```text
NEW       = no annotation row
MODIFIED  = saved draft
SUBMITTED = submitted coding
```

Eligibility is one click:

```text
1  Eligible — passes all three gates
2  Exclude — Gate 1 fails: no cyber nexus
3  Exclude — Gate 2 fails: not registrant practice
4  Exclude — Gate 3 fails: no governance/process evidence
```

Eligible paragraphs proceed to dimension and attribute coding. At least one explicit attribute is required for each submitted dimension. Evidence-span capture is optional.

Keyboard shortcuts:

```text
1–4               eligibility decision
Ctrl/Cmd + S      Save
Ctrl/Cmd + Enter  Submit & Next
```

## Development accounts

```text
admin      / Temp12345!
johndoor   / johndoor
marykey    / marykey
dummy      / dummy
donglai    / donglai
```

## PostgreSQL

Start the included database if using Docker:

```bash
docker compose up -d
```

Defaults:

```text
database: cgdi
user: cgdi
password: cgdi
port: 5432
```

Flyway creates/updates tables inside the existing `cgdi` database; Flyway does not create the PostgreSQL database itself.

## Testing labels

v0.4's ADMIN bulk cleanup is retained on `/assignments` if you want to clear only selected test labels without restarting the application.

## Training documents

```text
docs/labeler/
  CGDI_Labeler_Manual_v1.0.docx
  CGDI_Labeler_Manual_v1.0.md
  CGDI_Labeler_Quiz_v1.0.docx
  CGDI_Labeler_Quiz_v1.0.md

docs/supervisor/
  CGDI_Labeler_Quiz_Answer_Key_v1.0.docx
  CGDI_Labeler_Quiz_Answer_Key_v1.0.md
```

## Not yet implemented

Adjudicated-gold storage and final labeled-dataset/document export remain the next workflow layer.

## Dataset stages on the assignment page

The imported human-labeling corpus is separated into three research stages: `CALIBRATION`, `BLIND_PILOT`, and `FORMAL_VALIDATION`. The admin assignment page defaults to **Calibration**, shows the stage count, and lets you filter the filing table by stage before using **Select all visible**. This prevents accidental assignment of the blind-pilot filings during calibration.



## v0.5.4 assignment-table fix
The assignment table uses a non-sticky header and row numbers to prevent the first filing from being visually hidden while scrolling. The bundled dataset is 10 calibration + 20 blind pilot + an empty formal-validation placeholder.

## v0.5.5: in-context coding help + label comparison

Every dimension and attribute on the labeling page now has an ⓘ helper. Hover/focus/click shows the definition/meaning and positive/negative examples. Dimension helpers also include a boundary note.

ADMIN and SUPERVISOR can open `/review` to compare labelers at filing and paragraph level. Paragraphs are classified as agreement, disagreement, or incomplete. The review layer is read-only and does not modify either labeler's record.

Flyway V4 only adds guidance fields to the ontology tables; normal upgrades preserve all existing labeling data.


## v0.5.6: selectable comparison pair

On `/review/filing/{id}`, ADMIN/SUPERVISOR can select any two LABELER accounts assigned to that filing and compare exactly those two. This supports a calibration coding identity such as `dummy` without assuming that only two LABELER accounts exist. Agreement/disagreement/incomplete counts are recalculated for the selected pair.

Default development users are `admin`, `johndoor`, `marykey`, `dummy`, and `donglai`.

## v0.5.7 paragraph comparison queue

Supervisor/admin can use `/review/paragraphs` to compare any two assigned LABELER accounts across filings, with status-first filtering for disagreements, agreements, and incomplete paragraphs. The original `/review` page remains the filing-progress view.
