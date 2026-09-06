-- Paragraph-level workflow: NEW is derived from the absence of an annotation row.
-- Stored annotation states are MODIFIED and SUBMITTED.

ALTER TABLE app_user
    DROP CONSTRAINT IF EXISTS app_user_role_check;

ALTER TABLE app_user
    ADD CONSTRAINT app_user_role_check
    CHECK (role IN ('ADMIN', 'LABELER', 'SUPERVISOR', 'ADJUDICATOR'));

ALTER TABLE assignment
    ADD COLUMN IF NOT EXISTS assignment_type VARCHAR(16);

UPDATE assignment a
SET assignment_type = CASE
    WHEN u.role = 'LABELER' THEN 'LABELER'
    ELSE 'SUPERVISOR'
END
FROM app_user u
WHERE a.labeler_id = u.id
  AND a.assignment_type IS NULL;

ALTER TABLE assignment
    ALTER COLUMN assignment_type SET NOT NULL;

ALTER TABLE assignment
    DROP CONSTRAINT IF EXISTS assignment_assignment_type_check;

ALTER TABLE assignment
    ADD CONSTRAINT assignment_assignment_type_check
    CHECK (assignment_type IN ('LABELER', 'SUPERVISOR'));

DROP INDEX IF EXISTS idx_assignment_labeler_status;
ALTER TABLE assignment DROP CONSTRAINT IF EXISTS assignment_status_check;
ALTER TABLE assignment DROP COLUMN IF EXISTS status;

CREATE INDEX IF NOT EXISTS idx_assignment_labeler_order
    ON assignment(labeler_id, assignment_type, assignment_order);

ALTER TABLE annotation
    ALTER COLUMN cyber_nexus DROP NOT NULL,
    ALTER COLUMN registrant_attribution DROP NOT NULL,
    ALTER COLUMN governance_process DROP NOT NULL,
    ALTER COLUMN eligible DROP NOT NULL;

ALTER TABLE annotation
    ADD COLUMN IF NOT EXISTS status VARCHAR(16) NOT NULL DEFAULT 'MODIFIED',
    ADD COLUMN IF NOT EXISTS submitted_at TIMESTAMPTZ;

ALTER TABLE annotation
    DROP CONSTRAINT IF EXISTS annotation_status_check;

ALTER TABLE annotation
    ADD CONSTRAINT annotation_status_check
    CHECK (status IN ('MODIFIED', 'SUBMITTED'));

CREATE INDEX IF NOT EXISTS idx_annotation_labeler_status
    ON annotation(labeler_id, status);
