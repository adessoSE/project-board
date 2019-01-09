--Requires Users.sql and Projects.sql to be executed first

--Add Project to created Projects of SuperUser1:
INSERT INTO PB_USER_OWNED_PROJECTS (USER_ID, PROJECT_ID) VALUES
('User1', 'STF-1'),
('User2', 'STF-2');