--Requires Users.sql and Applications.sql to be executed first

--Create a new ApplicationTemplateMessage for User1:
INSERT INTO TEMPLATE_MESSAGE (DTYPE, ID, REFERENCED_USER_ID, ADDRESSEE_ID, APPLICATION_ID, SUBJECT, TEXT) VALUES
('ApplicationTemplateMessage', 2, 'User1', 'SuperUser2', 1, 'Subject', 'Text');