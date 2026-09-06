# CGDI Labeler v0.5.6

## Changes

- Bootstrap users are now: `admin` (ADMIN), `johndoor` (LABELER), `marykey` (LABELER), `dummy` (LABELER), and `donglai` (SUPERVISOR).
- Dev default passwords are: `Temp12345!`, `johndoor`, `marykey`, `dummy`, and `donglai` respectively. Environment variables still override the four configured password properties.
- `/review/filing/{id}` now lets ADMIN/SUPERVISOR select any two assigned LABELER accounts and compare exactly that pair.
- Pair comparison is based on user IDs, not usernames. Renaming labelers does not change the comparison logic.
- Agreement/disagreement/incomplete counts are recalculated for the selected pair only.
- Original human annotations remain read-only on the comparison page.
- No database migration is required for v0.5.6.
- Carries forward the non-sticky `/label`/table-header CSS fixes made after v0.5.5.

## Intended calibration use

Assign `johndoor`, `marykey`, and `dummy` to the calibration filings. The supervisor (`donglai`) can then compare:

- johndoor vs marykey
- johndoor vs dummy
- marykey vs dummy

For blind pilot/formal validation, assign only the two intended independent labelers if that is the study design.
