# CGDI Human Labeling Manual v1.0

## Purpose

This manual trains human labelers to code **Cyber Governance Disclosure Index (CGDI)** evidence from selected 10-K paragraphs. The goal is to identify observable disclosure facts consistently, not to judge whether a company is actually secure or well governed.

The labeler’s job is to answer three questions in order:

1. Does the paragraph qualify as cyber-governance disclosure?
2. If yes, which CGDI dimension(s) does it support?
3. For each selected dimension, which **attributes** are explicitly disclosed?

The attributes are the most important measurement layer. A dimension tells us **what kind of governance mechanism** is being disclosed; its attributes tell us **how much of that mechanism is observable**. Labelers should therefore treat attribute coding as the core factual task, not as optional detail.

---

# 1. What CGDI measures

**CGDI (Cyber Governance Disclosure Index)** measures the extent to which a registrant’s annual report makes observable the organizational structures, processes, responsibilities, and information flows through which cybersecurity risks are assessed, managed, monitored, and overseen.

CGDI measures **disclosure transparency / disclosure quality**. It does **not** measure the true quality or effectiveness of the firm’s cybersecurity program.

A firm can have strong cyber governance but disclose little, or weak cyber governance but disclose a great deal. CGDI measures the disclosure that is observable to an outside reader.

## 1.1 Three higher-order aspects

CGDI covers three broad aspects:

1. **Risk-processing transparency** — how cybersecurity risk is managed.
2. **Governance-structure transparency** — who is responsible for oversight and management.
3. **Governance-information-flow transparency** — how cybersecurity information moves through governance channels.

## 1.2 Six operational dimensions

| Aspect | Code | Dimension | Core question |
|---|---|---|---|
| Risk-processing transparency | **A1** | Internal cyber risk management | What internal processes does the firm use to assess, prevent, detect, respond to, recover from, and test cyber risk? |
| Risk-processing transparency | **A2** | ERM integration | How is cybersecurity integrated into broader enterprise risk management and business decision processes? |
| Risk-processing transparency | **A3** | Third-party cyber governance | How does the firm govern cybersecurity risk from vendors, suppliers, and service providers? |
| Governance-structure transparency | **B1** | Board cyber oversight | How is board-level responsibility for cybersecurity organized and exercised? |
| Governance-structure transparency | **B2** | Management cyber governance | Which management actors are responsible for cybersecurity, what do they do, how are they organized, and what relevant expertise is disclosed? |
| Governance-information-flow transparency | **C1** | Governance information flow | How does cyber information move through governance channels routinely and when significant events occur? |

---

# 2. What CGDI does not count

Do not code a paragraph as CGDI evidence merely because it contains words such as “cybersecurity,” “data,” “risk,” “security,” “technology,” or “privacy.”

The following are normally **not** CGDI evidence by themselves:

- descriptions of cyber threats, attacks, vulnerabilities, or possible losses;
- descriptions of a past incident without a governance/process mechanism;
- generic enterprise risk management language with no cyber nexus;
- generic internal-control or IT-operating language with no cyber nexus;
- descriptions of cybersecurity products or services sold to customers;
- descriptions of what regulators require without saying that the registrant actually performs the practice;
- statements that third-party providers create risk, without explaining how the registrant governs that risk.

---

# 3. The three-gate rationale

A paragraph is CGDI-eligible only when it passes **all three gates**. The gates are applied sequentially.

## Gate 1 — Cyber nexus

**Question:** Is the passage substantively about cybersecurity, information security, cyber risk, security of information systems, or closely related technological-security risk?

**Pass** when the governance/process being described is clearly cyber/security related.

**Fail** when the paragraph is about generic business risk, generic ERM, ordinary IT operations, privacy, compliance, or internal control without a cyber/security connection.

Positive example:

> “We conduct annual cybersecurity risk assessments of our information systems.”

Negative example:

> “We conduct annual enterprise risk assessments covering strategic, financial, and operational risks.”

The second example may be useful ERM disclosure, but it has no explicit cyber nexus.

## Gate 2 — Registrant attribution

**Question:** Is the disclosed practice the registrant’s own governance/process, or a process for which the registrant is responsible?

**Pass** when the paragraph says what the registrant, its board, management, employees, or retained service providers do on the registrant’s behalf.

**Fail** when the passage describes:

- a product/service sold to customers;
- a customer’s security program;
- industry practices generally;
- unrelated third parties;
- a regulatory requirement not stated as implemented by the registrant.

Positive example:

> “Our security operations center monitors our systems continuously for suspicious activity.”

Negative example:

> “Our managed-security service provides customers with 24x7 monitoring and detection.”

## Gate 3 — Governance/process evidence

**Question:** Does the passage disclose a structure, process, responsibility, control, assurance activity, or information flow?

**Pass** when it tells the reader something the firm **does**, **who is responsible**, **how oversight occurs**, or **how information moves**.

**Fail** when it only describes threat exposure, possible consequences, or the existence of risk.

Positive example:

> “The incident-response team investigates security alerts and escalates severe incidents to the General Counsel.”

Negative example:

> “Cyber incidents could disrupt operations and harm our reputation.”

## 3.1 Fast decision rule used in the website

The website records the **first failed gate**. This preserves the logic of the three gates while reducing clicks.

The four possible submission decisions are:

- **Eligible — passes all 3 gates**
- **Exclude — Gate 1 fails: no cyber nexus**
- **Exclude — Gate 2 fails: not the registrant’s practice**
- **Exclude — Gate 3 fails: no governance/process evidence**

A labeler therefore needs only **one gate-decision click per paragraph**.

If uncertain, do not use an “unclear” gate value. Leave the paragraph unsubmitted, save a draft, and explain the uncertainty in the memo. The uncertainty can then be discussed during calibration or adjudication.

---

# 4. Unit of coding and labeling workflow

The unit of human coding is a **selected paragraph**.

For each paragraph:

1. Make the one-click gate decision.
2. If excluded, submit and move to the next paragraph.
3. If eligible, select every supported CGDI dimension.
4. For each selected dimension, code every explicitly supported attribute.
5. Do not infer an attribute that is not stated.
6. Use the optional evidence/notes area only when it helps explain the decision.
7. Submit and move to the next paragraph.

**Multi-labeling is allowed and expected.** One paragraph may support several dimensions.

At paragraph level, do not code “0” for all unselected dimensions. Filing-level zeros are calculated later when no qualifying evidence for a dimension appears anywhere in the filing.

---

# 5. Why attributes matter

Attributes are the **measurement atoms** of CGDI.

For example, two paragraphs may both support B1 Board cyber oversight:

- “The Audit Committee oversees cybersecurity.”
- “The Audit Committee reviews cybersecurity risk quarterly, including incidents, risk assessments, and remediation progress.”

Both are B1, but the second makes much more of the oversight mechanism observable. That difference is captured through attributes such as responsible body, oversight mandate, review scope, and review frequency.

Therefore:

- select a dimension only when the mechanism is explicitly supported;
- once a dimension is selected, inspect **every attribute** for that dimension;
- mark an attribute present only when the text explicitly supports it;
- do not use an overall impression such as “this is detailed” instead of attribute coding;
- do not count the same fact twice merely because it is phrased twice.

For a submitted positive dimension, the website requires **at least one explicitly supported attribute**. If you believe a dimension is substantively supported but none of its listed attributes fits, do not force an attribute. Save the paragraph as a draft and flag the issue in the memo for calibration/adjudication; this may indicate an attribute-definition gap.

The final CGDI score will be computed later from the coded evidence. Human labelers should not assign a CGDI score.

---

# 6. Dimension A1 — Internal cyber risk management

## Definition

A1 captures internal processes used to identify, assess, prevent, detect, respond to, recover from, remediate, or test cybersecurity risk.

A1 concerns the firm’s **internal cyber risk-management machinery**.

## Attributes

| Attribute | Mark present when the paragraph explicitly discloses… | Do not mark for… |
|---|---|---|
| `risk_assessment` | cyber-risk identification, assessment, evaluation, prioritization, vulnerability assessment, threat analysis | threat exposure alone |
| `protective_controls` | preventive/protective safeguards, access controls, encryption, security controls, protective architecture | generic statement that systems are “secure” |
| `monitoring_detection` | monitoring, detection, security alerts, threat detection, SOC activity | generic awareness of threats |
| `incident_response` | incident-response plan/process, investigation, containment, response procedure | statement that incidents may occur |
| `training_awareness` | employee/executive cyber training, awareness exercises, phishing training | generic employee training unrelated to cyber |
| `recovery_resilience` | recovery, business continuity for cyber events, restoration, remediation, resilience | ordinary disaster recovery with no cyber nexus |
| `testing_assurance` | penetration tests, tabletop exercises, audits, red-team exercises, control testing, cyber program assurance | operation of a control without testing it |

### Positive example

> “We conduct periodic cyber-risk assessments, monitor network activity for anomalous behavior, maintain an incident-response plan, train employees annually, and conduct tabletop and penetration-testing exercises.”

Supported A1 attributes: `risk_assessment`, `monitoring_detection`, `incident_response`, `training_awareness`, `testing_assurance`.

### Negative example

> “Cyberattacks are becoming more frequent and sophisticated and could materially harm our business.”

This fails Gate 3. It describes exposure, not a risk-management process.

### Boundary notes

- **A1 vs A2:** A1 is the cyber process itself; A2 requires explicit integration into broader ERM or enterprise decision processes.
- **A1 testing_assurance:** testing is included in A1 in the six-dimension framework. Do not create a separate testing dimension.

---

# 7. Dimension A2 — ERM integration

## Definition

A2 captures explicit integration of cybersecurity into the firm’s broader enterprise risk management, enterprise prioritization, or strategic/business decision processes.

A cyber-risk process is **not automatically A2** merely because the company also has an ERM program.

## Attributes

| Attribute | Mark present when the paragraph explicitly discloses… | Do not mark for… |
|---|---|---|
| `explicit_erm_link` | a direct statement that cybersecurity is integrated into, part of, or incorporated within ERM | separate cyber and ERM statements with no link |
| `erm_actor` | ERM function, enterprise risk committee, risk council, or other ERM actor handling cyber risk | a cyber team with no ERM role |
| `integration_prioritization` | how cyber risk enters enterprise risk ranking/prioritization or is evaluated alongside other enterprise risks | cyber prioritization only within the security team |
| `decision_strategy_link` | cyber risk informing strategy, capital allocation, business decisions, enterprise objectives, or major decision processes | generic statement that cyber risk “may affect strategy” |

### Positive example

> “Cybersecurity risks are incorporated into our enterprise risk management framework. The Enterprise Risk Committee evaluates cyber risk alongside other principal risks and uses that assessment in strategic planning and capital-allocation decisions.”

Supported A2 attributes: all four.

### Negative example

> “Management performs cybersecurity risk assessments, and the Company maintains an enterprise risk management program.”

This supports A1 but not necessarily A2 because no integration is stated.

---

# 8. Dimension A3 — Third-party cyber governance

## Definition

A3 captures how the registrant governs cybersecurity risk arising from vendors, suppliers, contractors, cloud providers, and other third-party service providers.

Third-party dependence or exposure alone is not enough.

## Attributes

| Attribute | Mark present when the paragraph explicitly discloses… | Do not mark for… |
|---|---|---|
| `vendor_assessment` | cyber due diligence, questionnaires, audits, security review, vendor assessment before/on onboarding | merely using third parties |
| `risk_scope_tiering` | risk-based scope, tiering, classification, critical-vendor focus | identical review of all vendors without risk logic |
| `contractual_requirements` | security clauses, cybersecurity requirements, breach-notification obligations, audit rights in contracts | generic procurement terms |
| `monitoring_reassessment` | ongoing monitoring, periodic reassessment, continuous rating, recurring vendor review | one-time onboarding only |
| `remediation_escalation` | requiring remediation, escalation of vendor findings, termination/suspension or management escalation for unresolved cyber issues | noting that vendors may create risk |

### Positive example

> “Vendors are risk-tiered before onboarding. High-risk suppliers complete security assessments and must accept contractual security and incident-notification requirements. We reassess critical vendors annually and escalate unresolved findings for remediation.”

Supported A3 attributes: all five.

### Negative example

> “We rely on third-party cloud providers, and a breach at a provider could adversely affect us.”

This is third-party exposure, not third-party cyber governance; Gate 3 fails.

---

# 9. Dimension B1 — Board cyber oversight

## Definition

B1 captures the board-level body responsible for cybersecurity oversight and how that oversight responsibility is organized and exercised.

## Attributes

| Attribute | Mark present when the paragraph explicitly discloses… | Do not mark for… |
|---|---|---|
| `responsible_body_delegation` | the Board or a named board committee has responsibility; delegation from board to committee | management committee only |
| `oversight_mandate` | explicit cybersecurity oversight responsibility/mandate | a board merely receiving one incident notice |
| `review_scope` | what the board/committee reviews: cyber risk, incidents, program effectiveness, preparedness, controls, remediation, etc. | vague “cybersecurity matters” with no scope detail |
| `review_frequency` | quarterly, annual, periodic, at each meeting, or another explicit cadence | “from time to time” unless the manual/supervisor treats it as a meaningful cadence |

### Positive example

> “The Board delegates cybersecurity oversight to the Audit Committee. The Committee reviews the cyber-risk program, significant incidents, testing results, and remediation progress quarterly.”

Supported B1 attributes: all four.

### Negative example

> “The CISO manages the cybersecurity program.”

This may support B2, but it contains no board-level oversight evidence.

---

# 10. Dimension B2 — Management cyber governance

## Definition

B2 captures responsible management roles, their cyber responsibilities, reporting relationships, cross-functional coordination, and relevant expertise or qualifications.

## Attributes

| Attribute | Mark present when the paragraph explicitly discloses… | Do not mark for… |
|---|---|---|
| `responsible_role` | CISO, CIO, CTO, risk officer, security officer, executive committee, or other management role responsible for cyber | an unnamed “management” reference with no identifiable role, unless responsibility is still clear enough under the coding rule |
| `responsibility_scope` | what the role is responsible for: program management, risk assessment, incident response, policy, controls, etc. | title alone with no cyber responsibility if the cyber nexus is not otherwise explicit |
| `reporting_line` | who the responsible cyber role reports to | board reporting, which may instead support C1 depending on context |
| `cross_functional_coordination` | coordination among legal, IT, risk, compliance, finance, operations, disclosure committee, etc. | several functions named separately without coordination |
| `expertise_qualification` | relevant years of experience, prior roles, certifications, education, or cyber/security qualifications | generic statement that a person is “experienced” with no relevant detail |

### Positive example

> “The CISO leads the cybersecurity program, reports to the Chief Risk Officer, and coordinates incident response with Legal, Compliance, and IT. The CISO has 20 years of information-security experience and holds a CISSP certification.”

Supported B2 attributes: all five.

### Negative example

> “Our executives recognize cybersecurity as an important risk.”

This does not make a management governance structure observable.

---

# 11. Dimension C1 — Governance information flow

## Definition

C1 captures how cybersecurity information moves through governance channels. It includes both **routine reporting** and **event-triggered escalation**.

## Attributes

| Attribute | Mark present when the paragraph explicitly discloses… | Do not mark for… |
|---|---|---|
| `routine_reporter` | who regularly provides cyber information: CISO, CIO, management team, risk committee, etc. | a role responsible for cyber but no reporting activity |
| `routine_recipient` | who regularly receives the information: Board, Audit Committee, CEO, risk committee, etc. | a body with oversight but no information-flow statement |
| `routine_frequency` | quarterly, monthly, annually, at each meeting, periodic schedule | event-triggered reporting only |
| `routine_content` | what routine reports cover: threats, incidents, metrics, risk assessments, remediation, program status | generic “updates” with no content detail |
| `event_trigger` | the condition that initiates escalation: security event, incident, breach, alert, threshold breach | routine scheduled reporting |
| `severity_materiality_threshold` | severity, significance, materiality, potential impact, or another threshold used to determine escalation | escalation with no threshold criteria |
| `escalation_path` | who sends/receives escalation, sequence of escalation, disclosure committee, executives, board/committee decision body | incident-response activity that does not describe governance information flow |

### Positive routine-reporting example

> “The CISO reports quarterly to the Audit Committee on cyber threats, significant incidents, remediation status, and program metrics.”

Supported C1 attributes: `routine_reporter`, `routine_recipient`, `routine_frequency`, `routine_content`.

### Positive event-escalation example

> “Security incidents that exceed defined severity thresholds are escalated by the incident-response team to the Disclosure Committee and, when potentially material, to the Audit Committee.”

Supported C1 attributes: `event_trigger`, `severity_materiality_threshold`, `escalation_path`.

### Negative example

> “The Audit Committee oversees cybersecurity.”

This supports B1, but no information flow is described.

---

# 12. Important dimension boundaries

## A1 vs A2

- **A1:** the firm assesses/manages cyber risk.
- **A2:** the cyber risk process is explicitly connected to enterprise-wide ERM, prioritization, strategy, or business decisions.

## A1 vs A3

- **A1:** internal cyber processes.
- **A3:** vendor/third-party cyber governance.

A paragraph can support both if it separately describes internal processes and vendor processes.

## B1 vs B2

- **B1:** board/board-committee oversight.
- **B2:** management responsibility and organization.

## B1 vs C1

- **B1:** who oversees and what the board reviews.
- **C1:** how information reaches the board/committee or moves through other governance channels.

Example:

> “The Audit Committee oversees cybersecurity and receives quarterly reports from the CISO.”

This supports both B1 and C1.

## B2 vs C1

- **B2:** management role/responsibility/reporting structure.
- **C1:** recurring information transfer or event escalation.

---

# 13. Evidence span policy

Exact evidence spans are **useful but not required** for ordinary human coding in this pilot.

Why evidence can be useful:

- helps the supervisor understand disagreements;
- creates auditable training examples;
- may later support human-vs-LLM comparison.

Why it is not required:

- the full selected paragraph is already stored;
- forcing a separate span for every dimension creates extra clicks and can shift reliability from substantive coding to text-selection behavior;
- the main gold-standard targets are the gate decision, dimension labels, and attributes.

The website therefore treats evidence capture as **optional**. Use it when the paragraph is long, supports several dimensions, or the exact phrase will help adjudication.

---

# 14. Ontology gap

Use **Ontology gap** only when:

1. the paragraph passes all three gates;
2. it clearly discloses cyber-governance information;
3. none of A1, A2, A3, B1, B2, or C1 adequately represents the disclosure.

Do not use ontology gap merely because a paragraph is unusual or because you are uncertain which existing dimension fits. Explain the proposed missing concept in the note.

---

# 15. Labeler memo and uncertainty

Use the memo for:

- a difficult gate boundary;
- uncertainty between dimensions;
- uncertainty about an attribute;
- need for surrounding context;
- suspected ontology gap;
- UI/data problem.

During calibration, uncertain paragraphs are valuable discussion material. Do not force a guess simply to clear the queue; save the paragraph as MODIFIED and discuss it at the next calibration meeting.

---

# 16. Training, quiz, and calibration process

## Stage 1 — Manual study

Each labeler reads this manual independently and should be able to explain:

- what CGDI measures and does not measure;
- the three gates;
- the six dimensions;
- the difference between a dimension and its attributes;
- the main boundary rules.

## Stage 2 — Qualification quiz

Each labeler completes the quiz independently before seeing the answer key or another labeler’s answers.

Recommended readiness standard:

- at least **85% overall agreement with the answer key**; and
- no systematic misunderstanding of any one dimension; and
- all three gate concepts understood correctly.

The supervisor should review mistakes with the labeler before calibration begins.

## Stage 3 — Calibration: 10 filings

Both labelers code the same 10 filings.

### Filings 1–2

1. Labeler A codes independently.
2. Labeler B codes independently.
3. Neither sees the other’s labels before both finish.
4. Supervisor compares the coding.
5. A, B, and supervisor discuss disagreements.
6. Clarify the manual with examples if necessary.

### Filings 3–5

Repeat independent coding, then hold a second discussion. Focus on recurring boundary problems and attribute definitions.

### Filings 6–10

Code independently using the stabilized instructions. Discuss only after each agreed calibration block is complete.

At the end of filing 10, **freeze the coding manual** for the blind pilot.

## 16.1 How to discuss disagreements

The supervisor should classify the source of disagreement before adjudicating:

- coder error;
- gate-definition ambiguity;
- dimension-boundary ambiguity;
- attribute-definition ambiguity;
- evidence/context problem;
- ontology gap;
- UI/workflow problem;
- other.

Do not overwrite either coder’s original record. Adjudicated gold decisions should be stored separately in the later supervisor/adjudication layer.

## Stage 4 — Blind pilot: 20 filings

After the manual is frozen:

- both labelers code the same 20 filings independently;
- no discussion of specific blind-pilot cases occurs while coding is in progress;
- labelers do not see each other’s answers;
- supervisor comparison begins only after the blind coding block is complete.

The blind pilot evaluates whether the stabilized coding framework can be applied reliably without live calibration feedback.

---

# 17. Quick labeling checklist

For every paragraph:

1. **Gate decision:** eligible, Gate 1 fail, Gate 2 fail, or Gate 3 fail.
2. If excluded: submit and continue.
3. If eligible: select every supported dimension.
4. For every selected dimension: inspect **every attribute**.
5. Mark only explicitly supported attributes.
6. Use optional evidence/memo only when helpful.
7. If qualifying evidence fits no dimension, use ontology gap and explain why.
8. Submit and move to the next paragraph.

## Five rules to remember

1. **Cyber words alone are not enough.**
2. **The practice must belong to the registrant.**
3. **There must be a governance/process mechanism.**
4. **Dimensions identify the type of mechanism; attributes capture what is actually observable.**
5. **Do not infer missing details.**
