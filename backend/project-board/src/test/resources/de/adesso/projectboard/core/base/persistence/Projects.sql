--Create Projects, some with other statuses, LoBs and no LoB (null)
INSERT INTO PROJECT (ID, STATUS, ISSUETYPE, TITLE, JOB, SKILLS, DESCRIPTION, LOB, CUSTOMER, LOCATION, OPERATION_START, OPERATION_END, EFFORT, CREATED, UPDATED, FREELANCER, ELONGATION, OTHER, ORIGIN) VALUES
('STF-1', 'eskaliert', 'Issuetype', 'Title', 'Job', 'Skills', 'Description', 'LOB Test', 'Customer', 'Location', 'OperationStart', 'OperationEnd', 'Effort', '2018-02-1 13:37:00', '2018-02-2 13:37:00', 'Freelancer', 'Elongation', 'Other', 1),

('STF-2', 'Abgeschlossen', 'Issuetype', 'Title', 'Job', 'Skills', 'Description', 'LOB Test', 'Customer', 'Location', 'OperationStart', 'OperationEnd', 'Effort', '2018-02-1 13:37:00', '2018-02-2 13:37:00', 'Freelancer', 'Elongation', 'Other', 1),

('STF-3', 'eskaliert', 'Issuetype', 'Title', 'Job', 'Skills', 'Description', 'LOB Prod', 'Customer', 'Location', 'OperationStart', 'OperationEnd', 'Effort', '2018-02-1 13:37:00', '2018-02-2 13:37:00', 'Freelancer', 'Elongation', 'Other', 1),

('STF-4', 'Offen', 'Issuetype', 'Title', 'Job', 'Skills', 'Description', NULL, 'Customer', 'Location', 'OperationStart', 'OperationEnd', 'Effort', '2018-02-1 13:37:00', '2018-02-2 13:37:00', 'Freelancer', 'Elongation', 'Other', 1),

('STF-5', 'eskaliert', 'Issuetype', 'Title', 'Job', 'Skills', 'Description', NULL, 'Customer', 'Location', 'OperationStart', 'OperationEnd', 'Effort', '2018-02-1 13:37:00', '2018-02-2 13:37:00', 'Freelancer', 'Elongation', 'Other', 1),

('STF-6', 'Abgeschlossen', 'Issuetype', 'Title', 'Job', 'Skills', 'Description', NULL, 'Customer', 'Location', 'OperationStart', 'OperationEnd', 'Effort', '2018-02-1 13:37:00', '2018-02-2 13:37:00', 'Freelancer', 'Elongation', 'Other', 1),

('STF-7', 'Something weird', 'Issuetype', 'Title', 'Job', 'Skills', 'Description', NULL, 'Customer', 'Location', 'OperationStart', 'OperationEnd', 'Effort', '2018-02-1 13:37:00', '2018-02-2 13:37:00', 'Freelancer', 'Elongation', 'Other', 1),

('STF-8', 'Offen', 'Issuetype', 'Title', 'Job', 'Skills', 'Description', 'LOB Test', 'Customer', 'Location', 'OperationStart', 'OperationEnd', 'Effort', '2018-02-1 13:37:00', '2018-02-2 13:37:00', 'Freelancer', 'Elongation', 'Other', 1),

('STF-9', 'Offen', 'Issuetype', 'Title', 'Job', 'Skills', 'Description', 'LOB Prod', 'Customer', 'Location', 'OperationStart', 'OperationEnd', 'Effort', '2018-02-1 13:37:00', '2018-02-2 13:37:00', 'Freelancer', 'Elongation', 'Other', 1);

--Create some Labels for STF-1:
INSERT INTO PROJECT_LABELS (PROJECT_ID, LABELS)
VALUES ('STF-1', 'Label 1'), ('STF-1', 'Label 2'), ('STF-1', 'Label 3');