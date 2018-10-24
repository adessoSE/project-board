--Requires Users.sql and Projects.sql to be executed first

--Create new project applications for User1 and User2
INSERT INTO PROJECT_APPLICATION (ID, PROJECT_ID, USER_ID, COMMENT, APPLICATION_DATE) VALUES
(1, 'STF-3', 'User1', 'First application', '2018-01-1 13:37:00'),
(2, 'STF-4', 'User1', 'Second application', '2018-01-2 13:37:00');