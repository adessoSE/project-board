--Requires Users.sql script to be executed before

--Create new AccessInfo for SuperUser1 and SuperUser2:
INSERT INTO ACCESS_INFO (ID, ACCESS_END, ACCESS_START, USER_ID) VALUES
(1, '2018-01-1 13:37:00', '2018-01-2 13:37:00', 'SuperUser1'),
(2, '2017-01-1 13:37:00', '2017-01-2 13:37:00', 'SuperUser1'),
(3, '2018-02-1 13:37:00', '2018-02-2 13:37:00', 'SuperUser2');