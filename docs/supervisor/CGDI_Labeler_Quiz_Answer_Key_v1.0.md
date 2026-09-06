# CGDI Human Labeler Qualification Quiz — Supervisor Answer Key v1.0

**Do not provide this file to labelers before they complete the quiz.**

The examples are synthetic and intentionally written to make the intended rule test clear.

## Q1 — Generic ERM

- Gate: **X1**
- Dimensions: none
- Attributes: none
- Rationale: enterprise risk management is described, but no cyber/security nexus is stated.

## Q2 — Customer-facing cybersecurity service

- Gate: **X2**
- Dimensions: none
- Attributes: none
- Rationale: the security process is a service delivered to customers, not the registrant’s own governance process.

## Q3 — Threat exposure only

- Gate: **X3**
- Dimensions: none
- Attributes: none
- Rationale: cyber nexus and registrant exposure are clear, but no governance/process mechanism is disclosed.

## Q4 — Comprehensive internal cyber-risk process

- Gate: **E**
- Dimension: **A1**
- Attributes: `risk_assessment`, `protective_controls`, `monitoring_detection`, `incident_response`, `training_awareness`, `recovery_resilience`, `testing_assurance`

## Q5 — ERM integration

- Gate: **E**
- Dimension: **A2**
- Attributes: `explicit_erm_link`, `erm_actor`, `integration_prioritization`, `decision_strategy_link`

## Q6 — Third-party cyber governance

- Gate: **E**
- Dimension: **A3**
- Attributes: `vendor_assessment`, `risk_scope_tiering`, `contractual_requirements`, `monitoring_reassessment`, `remediation_escalation`

## Q7 — Board cyber oversight

- Gate: **E**
- Dimension: **B1**
- Attributes: `responsible_body_delegation`, `oversight_mandate`, `review_scope`, `review_frequency`

## Q8 — Management cyber governance

- Gate: **E**
- Dimension: **B2**
- Attributes: `responsible_role`, `responsibility_scope`, `reporting_line`, `cross_functional_coordination`, `expertise_qualification`

## Q9 — Routine governance information flow

- Gate: **E**
- Dimension: **C1**
- Attributes: `routine_reporter`, `routine_recipient`, `routine_frequency`, `routine_content`

## Q10 — Event-triggered escalation

- Gate: **E**
- Dimension: **C1**
- Attributes: `event_trigger`, `severity_materiality_threshold`, `escalation_path`

## Q11 — Multi-label

- Gate: **E**
- Dimensions: **A1, B2, C1**
- A1 attributes: `monitoring_detection`, `incident_response`
- B2 attributes: `responsible_role`, `responsibility_scope`, `reporting_line`
- C1 attributes: `routine_reporter`, `routine_recipient`, `routine_frequency`, `routine_content`

## Q12 — A1 or A2?

- Gate: **E**
- Dimension: **A1**
- Attribute: `risk_assessment`
- A2: **No.** The paragraph discloses cyber assessment and prioritization within the security process, but no explicit ERM/enterprise integration is stated.

## Q13 — B1 and/or C1?

- Gate: **E**
- Dimensions: **B1, C1**
- B1 attributes: `responsible_body_delegation`, `oversight_mandate`
- C1 attributes: `routine_reporter`, `routine_recipient`, `routine_frequency`, `routine_content`

## Q14 — Third-party exposure without governance

- Gate: **X3**
- Dimensions: none
- Attributes: none
- Rationale: third-party cyber exposure is disclosed, but there is no vendor governance mechanism.

## Q15 — Management expertise

- Gate: **E**
- Dimension: **B2**
- Attributes: `responsible_role`, `responsibility_scope`, `expertise_qualification`

## Q16 — Protective controls only

- Gate: **E**
- Dimension: **A1**
- Attribute: `protective_controls`

## Q17 — Routine reporting without board oversight mandate

- Gate: **E**
- Dimension: **C1**
- Attributes: `routine_reporter`, `routine_recipient`, `routine_frequency`, `routine_content`
- Do not infer B1 because the recipient is the Chief Legal Officer, not a board body, and no board oversight mandate is disclosed.

## Q18 — Contract terms without ongoing vendor monitoring

- Gate: **E**
- Dimension: **A3**
- Attribute: `contractual_requirements`
- Do not infer `monitoring_reassessment` or `vendor_assessment`.
- For this quiz, do not count `risk_scope_tiering` from the phrase “critical suppliers” alone. If the research team later adopts a different explicit rule, update both the manual and answer key together.

---

# Coverage of all 32 attributes

The quiz directly tests every attribute at least once:

- **A1 (7):** Q4; additional boundary checks Q11, Q12, Q16.
- **A2 (4):** Q5; A1-vs-A2 boundary Q12.
- **A3 (5):** Q6; negative boundary Q14; contract-only boundary Q18.
- **B1 (4):** Q7; B1-vs-C1 boundary Q13.
- **B2 (5):** Q8; additional checks Q11 and Q15.
- **C1 (7):** Q9 and Q10; additional checks Q11, Q13, Q17.

# Recommended scoring

Use the quiz diagnostically rather than as a purely mechanical exam.

Suggested scoring:

1. **Gate decision:** 1 point per question.
2. **Dimension set:** 2 points per eligible question; full credit only when all and only supported dimensions are selected.
3. **Attributes:** 1 point for each supported attribute correctly selected; subtract or flag inferred attributes that are not supported.
4. Review explanations for Q12 and Q18 qualitatively.

Recommended readiness standard:

- at least **85% overall**;
- no recurring gate error;
- no systematic misunderstanding of any dimension;
- labeler can explain why attributes, not subjective “detail” judgments, are the factual basis of the coding.

If a labeler misses several attributes within one dimension, review that dimension before the 10-filing calibration begins.
