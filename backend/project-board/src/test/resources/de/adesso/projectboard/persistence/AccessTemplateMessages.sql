--requires Users.sql to be executed first

--Create a new UserAccessEventMessage for User1:
INSERT INTO TEMPLATE_MESSAGE (DTYPE, ID, REFERENCED_USER_ID, ADDRESSEE_ID, SUBJECT, TEXT) VALUES
('UserAccessEventMessage', 1, 'User1', 'SuperUser2', 'Subject', 'Text');
