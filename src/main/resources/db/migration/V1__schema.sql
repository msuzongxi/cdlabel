CREATE TABLE app_user (
    id              BIGSERIAL PRIMARY KEY,
    username        VARCHAR(100) NOT NULL UNIQUE,
    password_hash   VARCHAR(255) NOT NULL,
    role            VARCHAR(32) NOT NULL,
    active          BOOLEAN NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CHECK (role IN ('ADMIN', 'LABELER', 'ADJUDICATOR'))
);

CREATE TABLE filing (
    id              BIGSERIAL PRIMARY KEY,
    filename        VARCHAR(500) NOT NULL UNIQUE,
    master_cik      VARCHAR(32),
    company         VARCHAR(500) NOT NULL,
    fiscal_year     INTEGER NOT NULL,
    source_path     TEXT,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_filing_fiscal_year
    ON filing(fiscal_year);

CREATE INDEX idx_filing_master_cik
    ON filing(master_cik);

CREATE TABLE paragraph (
    id                  BIGSERIAL PRIMARY KEY,
    filing_id           BIGINT NOT NULL REFERENCES filing(id) ON DELETE CASCADE,
    paragraph_id        VARCHAR(64) NOT NULL,
    text                TEXT NOT NULL,
    section_hint        VARCHAR(128),
    heading_context     TEXT,
    sort_order          INTEGER NOT NULL,
    candidate           BOOLEAN NOT NULL DEFAULT FALSE,
    word_count          INTEGER,
    source_start        INTEGER,
    source_end          INTEGER,
    cyber_hint_flag     BOOLEAN,
    context_hint_flag   BOOLEAN,
    table_like_flag     BOOLEAN,
    UNIQUE (filing_id, paragraph_id)
);

CREATE INDEX idx_paragraph_filing_order
    ON paragraph(filing_id, sort_order);

CREATE INDEX idx_paragraph_candidate
    ON paragraph(candidate)
    WHERE candidate = TRUE;

CREATE INDEX idx_paragraph_text_lower
    ON paragraph USING GIN (to_tsvector('english', text));

CREATE TABLE assignment (
    id                  BIGSERIAL PRIMARY KEY,
    filing_id           BIGINT NOT NULL REFERENCES filing(id) ON DELETE CASCADE,
    labeler_id          BIGINT NOT NULL REFERENCES app_user(id) ON DELETE CASCADE,
    batch_code          VARCHAR(100),
    assignment_order    INTEGER NOT NULL DEFAULT 0,
    status              VARCHAR(32) NOT NULL DEFAULT 'PENDING',
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (filing_id, labeler_id),
    CHECK (status IN ('PENDING', 'IN_PROGRESS', 'COMPLETED'))
);

CREATE INDEX idx_assignment_labeler_status
    ON assignment(labeler_id, status, assignment_order);

CREATE TABLE dimension_def (
    code            VARCHAR(8) PRIMARY KEY,
    aspect          VARCHAR(128) NOT NULL,
    name            VARCHAR(255) NOT NULL,
    question        TEXT NOT NULL,
    display_order   INTEGER NOT NULL,
    active          BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE attribute_def (
    code            VARCHAR(100) PRIMARY KEY,
    dimension_code  VARCHAR(8) NOT NULL REFERENCES dimension_def(code),
    label           VARCHAR(255) NOT NULL,
    display_order   INTEGER NOT NULL,
    active          BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE INDEX idx_attribute_dimension
    ON attribute_def(dimension_code, display_order);

CREATE TABLE annotation (
    id                      BIGSERIAL PRIMARY KEY,
    paragraph_id            BIGINT NOT NULL REFERENCES paragraph(id) ON DELETE CASCADE,
    labeler_id              BIGINT NOT NULL REFERENCES app_user(id) ON DELETE CASCADE,
    cyber_nexus             VARCHAR(16) NOT NULL,
    registrant_attribution  VARCHAR(16) NOT NULL,
    governance_process      VARCHAR(16) NOT NULL,
    eligible                BOOLEAN NOT NULL,
    ontology_gap            BOOLEAN NOT NULL DEFAULT FALSE,
    gap_note                TEXT,
    notes                   TEXT,
    source_mode             VARCHAR(32) NOT NULL DEFAULT 'QUEUE',
    created_at              TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (paragraph_id, labeler_id),
    CHECK (cyber_nexus IN ('YES', 'NO', 'UNCLEAR')),
    CHECK (registrant_attribution IN ('YES', 'NO', 'UNCLEAR')),
    CHECK (governance_process IN ('YES', 'NO', 'UNCLEAR')),
    CHECK (source_mode IN ('QUEUE', 'FULL_SEARCH'))
);

CREATE INDEX idx_annotation_labeler
    ON annotation(labeler_id);

CREATE INDEX idx_annotation_paragraph
    ON annotation(paragraph_id);

CREATE TABLE annotation_dimension (
    id                  BIGSERIAL PRIMARY KEY,
    annotation_id       BIGINT NOT NULL REFERENCES annotation(id) ON DELETE CASCADE,
    dimension_code      VARCHAR(8) NOT NULL REFERENCES dimension_def(code),
    evidence_text       TEXT NOT NULL,
    evidence_start      INTEGER,
    evidence_end        INTEGER,
    confidence          VARCHAR(16) NOT NULL,
    ambiguity_note      TEXT,
    UNIQUE (annotation_id, dimension_code),
    CHECK (confidence IN ('HIGH', 'MEDIUM', 'LOW'))
);

CREATE TABLE annotation_attribute (
    id                          BIGSERIAL PRIMARY KEY,
    annotation_dimension_id     BIGINT NOT NULL REFERENCES annotation_dimension(id) ON DELETE CASCADE,
    attribute_code              VARCHAR(100) NOT NULL REFERENCES attribute_def(code),
    present                     BOOLEAN NOT NULL,
    value_text                  TEXT,
    UNIQUE (annotation_dimension_id, attribute_code)
);
