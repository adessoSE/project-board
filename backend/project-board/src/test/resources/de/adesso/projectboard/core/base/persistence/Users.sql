--Create new Users:
INSERT INTO USER (DTYPE, ID, FIRST_NAME, LAST_NAME, EMAIL, LOB, BOSS_ID) VALUES
('SuperUser', 'SuperUser1', 'First Test', 'Super User', 'firsttestsuperuser@user.com', 'LOB Test', 'SuperUser1'),
('SuperUser', 'SuperUser2', 'Second Test', 'Super User', 'secondtestsuperuser@user.com', 'LOB Test', 'SuperUser1'),
('User', 'User1', 'First Test', 'User', 'firsttestuser@user.com', 'LOB Test', 'SuperUser2'),
('User', 'User2', 'Second Test', 'User', 'secondtestuser@user.com', 'LOB Test', 'SuperUser2');
