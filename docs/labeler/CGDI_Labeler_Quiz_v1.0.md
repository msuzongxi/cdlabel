# CGDI Human Labeler Qualification Quiz v1.0

## Instructions

Complete this quiz **independently** after reading the CGDI Human Labeling Manual v1.0. Do not consult the answer key or another labeler.

For each case, choose one gate decision:

- **E** = Eligible: passes all three gates
- **X1** = Exclude: Gate 1 fails (no cyber nexus)
- **X2** = Exclude: Gate 2 fails (not the registrant’s practice)
- **X3** = Exclude: Gate 3 fails (no governance/process evidence)

If the case is eligible:

1. select every supported dimension: A1, A2, A3, B1, B2, C1;
2. list every supported attribute for each selected dimension;
3. do not infer unstated attributes.

The examples are synthetic training passages designed to test the coding rules.

---

## Q1 — Generic ERM

> “Our enterprise risk management program identifies and evaluates strategic, financial, operational, and compliance risks and reports the highest-ranked risks to senior management.”

Gate decision: ______

Dimensions / attributes: ______________________________________________

---

## Q2 — Customer-facing cybersecurity service

> “Our managed-security service provides customers with 24x7 threat monitoring, endpoint detection, incident response, and monthly security reports.”

Gate decision: ______

Dimensions / attributes: ______________________________________________

---

## Q3 — Threat exposure only

> “Cyberattacks could interrupt our operations, expose confidential information, increase costs, and damage our reputation.”

Gate decision: ______

Dimensions / attributes: ______________________________________________

---

## Q4 — Comprehensive internal cyber-risk process

> “We perform annual cybersecurity risk assessments and vulnerability reviews. We use multi-factor authentication, encryption, and network segmentation as protective safeguards. Our security operations center continuously monitors alerts for suspicious activity. We maintain an incident-response plan, provide annual phishing-awareness training, maintain cyber recovery and system-restoration procedures, and conduct penetration tests and tabletop exercises each year.”

Gate decision: ______

Dimensions / attributes: ______________________________________________

---

## Q5 — ERM integration

> “Cybersecurity risk is incorporated into our enterprise risk management framework. The Enterprise Risk Committee evaluates cyber risks together with other principal enterprise risks, ranks them in the company-wide risk register, and uses the resulting assessment in strategic planning and capital-allocation decisions.”

Gate decision: ______

Dimensions / attributes: ______________________________________________

---

## Q6 — Third-party cyber governance

> “Before onboarding, vendors are classified into cyber-risk tiers. High-risk vendors complete security questionnaires and technical due-diligence reviews and must accept contractual security, breach-notification, and audit-right provisions. Critical vendors are reassessed annually, and unresolved security findings are escalated to management for remediation or termination decisions.”

Gate decision: ______

Dimensions / attributes: ______________________________________________

---

## Q7 — Board cyber oversight

> “The Board has delegated cybersecurity oversight to the Audit Committee. The Committee is responsible for overseeing cybersecurity risk and reviews the security program, significant incidents, testing results, and remediation progress on a quarterly basis.”

Gate decision: ______

Dimensions / attributes: ______________________________________________

---

## Q8 — Management cyber governance

> “The Chief Information Security Officer leads the cybersecurity program and is responsible for security policy, risk assessment, and incident response. The CISO reports to the Chief Risk Officer and coordinates cyber matters with Legal, Compliance, IT, and Operations. The CISO has 18 years of information-security experience and holds CISSP and CISM certifications.”

Gate decision: ______

Dimensions / attributes: ______________________________________________

---

## Q9 — Routine governance information flow

> “The CISO provides the Audit Committee with quarterly cybersecurity reports covering major threats, significant incidents, security metrics, risk-assessment results, and remediation status.”

Gate decision: ______

Dimensions / attributes: ______________________________________________

---

## Q10 — Event-triggered escalation

> “When a security incident is detected, the incident-response team evaluates its severity. Incidents above the defined severity threshold are escalated to the Disclosure Committee, and incidents that could be material are further escalated to the Chief Executive Officer and the Audit Committee.”

Gate decision: ______

Dimensions / attributes: ______________________________________________

---

## Q11 — Multi-label: internal process + management + information flow

> “The CISO, who reports to the Chief Operating Officer, directs the company’s incident-response program. The incident-response team monitors security alerts and investigates suspected incidents. The CISO briefs the Risk Committee monthly on incident trends and remediation progress.”

Gate decision: ______

Dimensions / attributes: ______________________________________________

---

## Q12 — A1 or A2?

> “The security team assesses cyber threats quarterly, ranks vulnerabilities by likelihood and impact, and prioritizes remediation based on those rankings.”

Gate decision: ______

Dimensions / attributes: ______________________________________________

Does this support A2? Why or why not? __________________________________

---

## Q13 — B1 and/or C1?

> “The Audit Committee oversees cybersecurity. It receives a quarterly report from the CISO describing significant threats, incidents, and remediation activities.”

Gate decision: ______

Dimensions / attributes: ______________________________________________

---

## Q14 — Third-party exposure without governance

> “We depend on cloud, payroll, and payment-processing vendors. A cyber incident affecting any of these providers could disrupt our operations or expose confidential information.”

Gate decision: ______

Dimensions / attributes: ______________________________________________

---

## Q15 — Management expertise

> “Our CISO is responsible for enterprise information security and has more than 25 years of cybersecurity experience, including prior leadership roles in security engineering and incident response.”

Gate decision: ______

Dimensions / attributes: ______________________________________________

---

## Q16 — Protective controls only

> “We require multi-factor authentication for privileged accounts and encrypt sensitive information in transit and at rest.”

Gate decision: ______

Dimensions / attributes: ______________________________________________

---

## Q17 — Routine reporting without board oversight mandate

> “The Information Security Director provides the Chief Legal Officer with a monthly report on security incidents, threat trends, and outstanding remediation items.”

Gate decision: ______

Dimensions / attributes: ______________________________________________

---

## Q18 — Contract terms without ongoing vendor monitoring

> “Our vendor-security standard requires critical suppliers to sign contractual provisions covering minimum security controls and prompt notification of cyber incidents.”

Gate decision: ______

Dimensions / attributes: ______________________________________________

---

# Attribute Coverage Check

Before submitting the quiz, confirm that across your answers you have considered every attribute below.

### A1
- risk_assessment
- protective_controls
- monitoring_detection
- incident_response
- training_awareness
- recovery_resilience
- testing_assurance

### A2
- explicit_erm_link
- erm_actor
- integration_prioritization
- decision_strategy_link

### A3
- vendor_assessment
- risk_scope_tiering
- contractual_requirements
- monitoring_reassessment
- remediation_escalation

### B1
- responsible_body_delegation
- oversight_mandate
- review_scope
- review_frequency

### B2
- responsible_role
- responsibility_scope
- reporting_line
- cross_functional_coordination
- expertise_qualification

### C1
- routine_reporter
- routine_recipient
- routine_frequency
- routine_content
- event_trigger
- severity_materiality_threshold
- escalation_path
