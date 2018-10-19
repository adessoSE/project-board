--Create new Users:
INSERT INTO USER (DTYPE, ID, FIRST_NAME, LAST_NAME, EMAIL, LOB, BOSS_ID) VALUES ('SuperUser', 'SuperUser1', 'First Test', 'Super User', 'firsttestsuperuser@user.com', 'LOB Test', 'SuperUser1')
INSERT INTO USER (DTYPE, ID, FIRST_NAME, LAST_NAME, EMAIL, LOB, BOSS_ID) VALUES ('SuperUser', 'SuperUser2', 'Second Test', 'Super User', 'secondtestsuperuser@user.com', 'LOB Test', 'SuperUser1');
INSERT INTO USER (DTYPE, ID, FIRST_NAME, LAST_NAME, EMAIL, LOB, BOSS_ID) VALUES ('User', 'User1', 'First Test', 'User', 'firsttestuser@user.com', 'LOB Test', 'SuperUser2');
INSERT INTO USER (DTYPE, ID, FIRST_NAME, LAST_NAME, EMAIL, LOB, BOSS_ID) VALUES ('User', 'User2', 'Second Test', 'User', 'secondtestuser@user.com', 'LOB Test', 'SuperUser2');

--Create new AccessInfo for SuperUser1:
INSERT INTO ACCESS_INFO (ID, ACCESS_END, ACCESS_START, USER_ID) VALUES (1, '2018-01-1 13:37:00', '2018-01-2 13:37:00', 'SuperUser1');
INSERT INTO ACCESS_INFO (ID, ACCESS_END, ACCESS_START, USER_ID) VALUES (2, '2017-01-1 13:37:00', '2017-01-2 13:37:00', 'SuperUser1');

--Create new AccessInfo for SuperUser2:
INSERT INTO ACCESS_INFO (ID, ACCESS_START, ACCESS_END, USER_ID) VALUES (3, '2018-02-1 13:37:00', '2018-02-2 13:37:00', 'SuperUser2');

--Create new Project:
INSERT INTO PROJECT (ID, TITLE, ORIGIN) VALUES ('AD-1', 'Test', 1);

--Create new Bookmark for SuperUser2:
INSERT INTO USER_BOOKMARKS (USER_ID, BOOKMARKS_ID) VALUES ('SuperUser2', 'AD-1');