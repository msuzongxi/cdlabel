INSERT INTO dimension_def(code, aspect, name, question, display_order) VALUES
('A1', 'Risk-processing transparency',
 'Internal cyber risk management',
 'How does the firm identify, assess, control, respond to, recover from, and test cyber risk internally?', 10),

('A2', 'Risk-processing transparency',
 'ERM integration',
 'How is cybersecurity integrated into broader enterprise risk management and decision processes?', 20),

('A3', 'Risk-processing transparency',
 'Third-party cyber governance',
 'How does the firm govern cybersecurity risk from vendors and service providers?', 30),

('B1', 'Governance-structure transparency',
 'Board cyber oversight',
 'How is board-level responsibility for cybersecurity organized and exercised?', 40),

('B2', 'Governance-structure transparency',
 'Management cyber governance',
 'Which management actors are responsible for cybersecurity, what do they do, and what relevant expertise is disclosed?', 50),

('C1', 'Governance-information-flow transparency',
 'Governance information flow',
 'How does cyber information move through governance channels, routinely and when significant events occur?', 60);

INSERT INTO attribute_def(code, dimension_code, label, display_order) VALUES
('A1_risk_assessment',       'A1', 'Risk identification / assessment', 10),
('A1_protective_controls',   'A1', 'Protective / preventive controls', 20),
('A1_monitoring_detection',  'A1', 'Monitoring / detection', 30),
('A1_incident_response',     'A1', 'Incident-response process', 40),
('A1_training_awareness',    'A1', 'Training / awareness', 50),
('A1_recovery_resilience',   'A1', 'Recovery / resilience / remediation', 60),
('A1_testing_assurance',     'A1', 'Testing / assurance', 70),

('A2_explicit_erm_link',          'A2', 'Explicit ERM link', 10),
('A2_erm_actor',                  'A2', 'ERM actor / committee', 20),
('A2_integration_prioritization', 'A2', 'Integration / enterprise prioritization', 30),
('A2_decision_strategy_link',     'A2', 'Decision / strategy link', 40),

('A3_vendor_assessment',          'A3', 'Vendor cyber due diligence / assessment', 10),
('A3_risk_scope_tiering',         'A3', 'Risk-based scope / tiering', 20),
('A3_contractual_requirements',   'A3', 'Contractual security requirements', 30),
('A3_monitoring_reassessment',    'A3', 'Ongoing monitoring / reassessment', 40),
('A3_remediation_escalation',     'A3', 'Vendor remediation / escalation', 50),

('B1_responsible_body_delegation','B1', 'Responsible body / delegation', 10),
('B1_oversight_mandate',          'B1', 'Oversight mandate', 20),
('B1_review_scope',               'B1', 'Review scope', 30),
('B1_review_frequency',           'B1', 'Review frequency', 40),

('B2_responsible_role',           'B2', 'Responsible management role', 10),
('B2_responsibility_scope',       'B2', 'Responsibility scope', 20),
('B2_reporting_line',             'B2', 'Reporting line', 30),
('B2_cross_functional_coordination','B2', 'Cross-functional coordination', 40),
('B2_expertise_qualification',    'B2', 'Relevant expertise / qualification', 50),

('C1_routine_reporter',           'C1', 'Routine reporter', 10),
('C1_routine_recipient',          'C1', 'Routine recipient', 20),
('C1_routine_frequency',          'C1', 'Routine reporting frequency', 30),
('C1_routine_content',            'C1', 'Routine report content', 40),
('C1_event_trigger',              'C1', 'Event trigger', 50),
('C1_severity_materiality_threshold','C1', 'Severity / materiality threshold', 60),
('C1_escalation_path',            'C1', 'Escalation path / decision body', 70);
