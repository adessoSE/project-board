--Requires Users.sql and Projects.sql to be executed first

--Create new Bookmark for User1 and User2:
INSERT INTO PB_USER_BOOKMARKS (USER_ID, PROJECT_ID) VALUES
('User1', 'STF-1'),
('User2', 'STF-1');