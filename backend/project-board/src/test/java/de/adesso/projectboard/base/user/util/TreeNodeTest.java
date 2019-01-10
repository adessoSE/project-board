package de.adesso.projectboard.base.user.util;

import de.adesso.projectboard.base.user.persistence.hierarchy.tree.TreeNode;
import org.junit.Test;

import java.util.Arrays;

public class TreeNodeTest {

    @Test
    public void mapNodeAndChildren() {
        // given
        var rootNode = new TreeNode<String>("1");
        var firstLevelNode1 = new TreeNode<String>("2");
        var firstLevelNode2 = new TreeNode<String>("3");
        var secondLevelNode1 = new TreeNode<String>("4");

        rootNode.children.addAll(Arrays.asList(firstLevelNode1, firstLevelNode2));
        firstLevelNode1.children.add(secondLevelNode1);

        // when
        var mappedNode = rootNode.mapNodeAndChildren(Integer::parseInt);

        // then
        System.out.println("Test");
    }

}