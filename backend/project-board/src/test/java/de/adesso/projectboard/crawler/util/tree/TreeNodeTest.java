package de.adesso.projectboard.crawler.util.tree;

import org.junit.Test;

import static org.junit.Assert.*;

public class TreeNodeTest {

    @Test
    public void testAddChild() {
        TreeNode<Long> root = new TreeNode<>(1L);
        TreeNode<Long> child = new TreeNode<>(2L);
        root.addChild(child);

        assertEquals(1L, root.getChildren().size());
        assertEquals(root, child.getParent());
    }

    @Test
    public void testSetParent() {
        TreeNode<Long> root = new TreeNode<>(1L);
        TreeNode<Long> child = new TreeNode<>(2L);
        child.setParent(root);

        assertEquals(1L, root.getChildren().size());
        assertEquals(root, child.getParent());
    }

    @Test
    public void testIsRoot() {
        TreeNode<Long> root = new TreeNode<>(1L);
        TreeNode<Long> child = new TreeNode<>(2L);

        assertTrue(child.isRoot());

        child.setParent(root);

        assertFalse(child.isRoot());
    }

    @Test
    public void testIsLeaf() {
        TreeNode<Long> root = new TreeNode<>(1L);
        TreeNode<Long> child = new TreeNode<>(2L);

        assertTrue(root.isLeaf());

        root.getChildren().add(child);

        assertFalse(root.isLeaf());
    }

}