-- Requires Users.sql to be executed first

INSERT INTO PB_USER_DATA (ID, USER_ID, FIRST_NAME, LAST_NAME, EMAIL, LOB, PICTURE, IS_PICTURE_INITIALIZED) VALUES
(1, 'User1', 'First', 'User', 'first.user@test.com', 'LOB Test', NULL, TRUE),
(2, 'User2', 'Second', 'User', 'second.user@test.com', 'LOB Test', NULL, TRUE),
(3, 'User3', 'Third', 'User', 'third.user@test.com', 'LOB Prod', NULL, TRUE);