--requires Users.sql to be executed first

--Create a new AccessTemplateMessage for User1:
INSERT INTO TEMPLATE_MESSAGE (DTYPE, ID, REFERENCED_USER_ID, ADDRESSEE_ID, SUBJECT, TEXT) VALUES
('AccessTemplateMessage', 1, 'User1', 'SuperUser2', 'Subject', 'Text');
