# CGDI Labeler v0.5.2 implementation notes

## Change in this version

Successful login now uses an explicit role-aware redirect:

- `ROLE_LABELER` → `/label`
- `ROLE_ADMIN` → `/assignments`
- `ROLE_SUPERVISOR` → `/assignments`
- any future/other authenticated role → `/` and the existing controller decides the destination

This avoids Spring Security's saved-request/default redirect behavior sending a labeler somewhere other than the labeling queue.

## Data behavior

Unchanged from v0.5.1:

- normal restart preserves PostgreSQL data
- `CGDI_RESET_ON_STARTUP=false` by default
- reset occurs only when explicitly enabled
- calibration/blind/formal-validation folder structure is unchanged
