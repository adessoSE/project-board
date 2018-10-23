--Requires Projects.sql script to be executed before

--Create new Users:
INSERT INTO USER (DTYPE, ID, FIRST_NAME, LAST_NAME, EMAIL, LOB, BOSS_ID) VALUES
('SuperUser', 'SuperUser1', 'First Test', 'Super User', 'firsttestsuperuser@user.com', 'LOB Test', 'SuperUser1'),
('SuperUser', 'SuperUser2', 'Second Test', 'Super User', 'secondtestsuperuser@user.com', 'LOB Test', 'SuperUser1'),
('User', 'User1', 'First Test', 'User', 'firsttestuser@user.com', 'LOB Test', 'SuperUser2'),
('User', 'User2', 'Second Test', 'User', 'secondtestuser@user.com', 'LOB Test', 'SuperUser2');

--Create new Bookmark for SuperUser2 and User1:
INSERT INTO USER_BOOKMARKS (USER_ID, BOOKMARKS_ID) VALUES
('SuperUser2', 'STF-1'),
('User1', 'STF-1');

--Add Project to created Projects of SuperUser1:
INSERT INTO USER_CREATED_PROJECTS (USER_ID, CREATED_PROJECTS_ID) VALUES
('SuperUser1', 'STF-1'),
('SuperUser1', 'STF-2');