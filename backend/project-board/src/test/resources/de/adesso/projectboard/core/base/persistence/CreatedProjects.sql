--Requires Users.sql and Projects.sql to be executed first

--Add Project to created Projects of SuperUser1:
INSERT INTO USER_CREATED_PROJECTS (USER_ID, CREATED_PROJECTS_ID) VALUES
('SuperUser1', 'STF-1'),
('SuperUser1', 'STF-2');