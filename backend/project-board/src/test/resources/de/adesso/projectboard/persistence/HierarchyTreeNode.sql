-- Requires Users.sql to be executed first

-- Insert a tree that looks like this:
--
--         1
--        / \
--       /   \
--      2     3
--      |
--      4

INSERT INTO HIERARCHY_TREE_NODE (ID, MANAGER_NODE_ID, USER_ID, IS_MANAGING_USER) VALUES
(1, NULL, 'User1', TRUE),
(2, 1, 'User2', TRUE),
(3, 1, 'User3', FALSE),
(4, 2, 'User4', FALSE);

INSERT INTO HIERARCHY_TREE_NODE_ALL_STAFF (MANAGER_NODE_ID, NODE_ID) VALUES
(1, 2),
(1, 3),
(1, 4),
(2, 4);
