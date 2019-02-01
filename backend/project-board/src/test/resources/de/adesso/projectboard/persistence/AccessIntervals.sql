--Requires Users.sql script to be executed before

--Create new access intervals for User1 and User2:
INSERT INTO ACCESS_INTERVAL (ID, USER_ID, START_TIME, END_TIME) VALUES
(1, 'User1', '2018-01-1 13:37:00', '2018-01-2 13:37:00'),
(2, 'User2', '2017-02-1 13:37:00', '2017-02-2 13:37:00'),
(3, 'User2', '2018-03-1 13:37:00', '2018-03-2 13:37:00');
