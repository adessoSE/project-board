--Requires Users.sql and Projects.sql to be executed first

--Create new Bookmark for SuperUser2 and User1:
INSERT INTO USER_BOOKMARKS (USER_ID, BOOKMARKS_ID) VALUES
('SuperUser2', 'STF-1'),
('User1', 'STF-1');