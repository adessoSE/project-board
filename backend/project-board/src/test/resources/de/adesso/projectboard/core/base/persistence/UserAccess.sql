--Requires Users.sql script to be executed before

--Create new AccessInfo for SuperUser1, SuperUser2 and User2:
INSERT INTO ACCESS_INFO (ID, ACCESS_START, ACCESS_END, USER_ID) VALUES
(1, '2018-01-1 13:37:00', '2018-01-2 13:37:00', 'SuperUser1'),
(2, '2017-02-1 13:37:00', '2017-02-2 13:37:00', 'SuperUser2'),
(3, '2018-03-1 13:37:00', '2018-03-2 13:37:00', 'User2');