package de.adesso.projectboard.base.user.persistence.hierarchy;

import de.adesso.projectboard.base.user.persistence.User;
import org.assertj.core.api.SoftAssertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class HierarchyTreeNodeTest {

    @Mock
    private User userMock;

    @Mock
    private User otherUserMock;

    @Test
    public void equalsReturnsTrueWhenSameInstance() {
        // given
        var node = new HierarchyTreeNode(userMock);

        // when / then
        compareEqualsWithExpectedEquals(node, node, true);
        assertHashCodesEqual(node, node);
    }

    @Test
    public void equalsReturnsTrueWhenSameFieldValues() {
        // given
        var node = new HierarchyTreeNode(userMock);
        var otherNode = new HierarchyTreeNode(userMock);

        // when / then
        compareEqualsWithExpectedEquals(node, otherNode, true);
        assertHashCodesEqual(node, otherNode);
    }

    @Test
    public void equalsReturnsFalseWhenIdDiffers() {
        // given
        var nodeId = 1L;
        var otherNodeId = 2L;

        var node = new HierarchyTreeNode(userMock);
        var otherNode = new HierarchyTreeNode(userMock);

        node.id = nodeId;
        otherNode.id = otherNodeId;

        // when / then
        compareEqualsWithExpectedEquals(node, otherNode, false);
    }

    @Test
    public void equalsReturnsFalseWhenManagerIdDiffers() {
        // given
        var managerNodeId = 1L;
        var otherManagerNodeId = 2L;

        var managerNode = new HierarchyTreeNode(userMock);
        var otherManagerNode = new HierarchyTreeNode(userMock);

        managerNode.id = managerNodeId;
        otherManagerNode.id = otherManagerNodeId;

        var node = new HierarchyTreeNode(userMock);
        var otherNode = new HierarchyTreeNode(userMock);

        node.manager = managerNode;
        otherNode.manager = otherManagerNode;

        // when / then
        compareEqualsWithExpectedEquals(node, otherNode, false);
    }

    @Test
    public void equalsReturnsFalseWhenDirectStaffDiffers() {
        // given
        var directStaffNode = new HierarchyTreeNode(otherUserMock);

        var node = new HierarchyTreeNode(userMock);
        var otherNode = new HierarchyTreeNode(userMock);

        node.directStaff = Collections.singletonList(directStaffNode);

        // when / then
        compareEqualsWithExpectedEquals(node, otherNode, false);
    }

    @Test
    public void equalsReturnsFalseWhenStaffDiffers() {
        // given
        var staffNode = new HierarchyTreeNode(otherUserMock);

        var node = new HierarchyTreeNode(userMock);
        var otherNode = new HierarchyTreeNode(userMock);

        node.staff = Collections.singletonList(staffNode);

        // when / then
        compareEqualsWithExpectedEquals(node, otherNode, false);
    }

    @Test
    public void equalsReturnsFalseWhenUserDiffers() {
        // given
        var node = new HierarchyTreeNode(userMock);
        var otherNode = new HierarchyTreeNode(otherUserMock);

        // when / then
        compareEqualsWithExpectedEquals(node, otherNode, false);
    }

    @Test
    public void equalsReturnsFalseWhenManagingUserDiffers() {
        // given
        var node = new HierarchyTreeNode(userMock);
        var otherNode = new HierarchyTreeNode(userMock);

        node.managingUser = true;

        // when / then
        compareEqualsWithExpectedEquals(node, otherNode, false);
    }


    @Test
    public void isLeafReturnsTrueWhenDirectStaffAndStaffAreEmpty() {
        // given
        var node = new HierarchyTreeNode(userMock);

        // when / then
        assertThat(node.isLeaf()).isTrue();
    }

    @Test
    public void isLeafReturnsFalseWhenDirectStaffIsNotEmpty() {
        // given
        var node = new HierarchyTreeNode(userMock);
        var directStaffNode = new HierarchyTreeNode(otherUserMock);

        node.directStaff = Collections.singletonList(directStaffNode);

        // when / then
        assertThat(node.isLeaf()).isFalse();
    }

    @Test
    public void isLeafReturnsFalseWhenStaffIsNotEmpty() {
        // given
        var node = new HierarchyTreeNode(userMock);
        var staffNode = new HierarchyTreeNode(otherUserMock);

        node.staff = Collections.singletonList(staffNode);

        // when / then
        assertThat(node.isLeaf()).isFalse();
    }

    @Test
    public void isLeafReturnsFalseWhenDirectStaffAndStaffNotEmpty() {
        // given
        var node = new HierarchyTreeNode(userMock);
        var directStaffNode = new HierarchyTreeNode(otherUserMock);
        var staffNode = new HierarchyTreeNode(otherUserMock);

        node.directStaff = Collections.singletonList(directStaffNode);
        node.staff = Collections.singletonList(staffNode);

        // when / then
        assertThat(node.isLeaf()).isFalse();
    }

    @Test
    public void isRootReturnsTrueWhenNodeIsItsOwnManager() {
        // given
        var node = new HierarchyTreeNode(userMock);

        // when / then
        assertThat(node.isRoot()).isTrue();
    }

    @Test
    public void isRootReturnsFalseWhenOtherNodeIsManager() {
        // given
        var managerNode = new HierarchyTreeNode(otherUserMock);
        var node = new HierarchyTreeNode(managerNode, userMock);

        // when / then
        assertThat(node.isRoot()).isFalse();
    }

    @Test
    public void getRootReturnsNodeWhenNodeIsRoot() {
        // given
        var node = new HierarchyTreeNode(userMock);

        // when / then
        assertThat(node.getRoot()).isSameAs(node);
    }

    @Test
    public void getRootReturnsManagerWhenNodeIsNoRoot() {
        // given
        var managerNode = new HierarchyTreeNode(otherUserMock);
        var node = new HierarchyTreeNode(managerNode, userMock);

        // when / then
        assertThat(node.getRoot()).isSameAs(managerNode);
    }

    @Test
    public void managersEqualReturnsTrueWhenSameManagerInstance() {
        // given
        var node = new HierarchyTreeNode(userMock);

        // when / then
        assertThat(node.managersEqual(node)).isTrue();
    }

    @Test
    public void managersEqualReturnsTrueWhenSameId() {
        // given
        var managerNodeId = 1L;

        var managerNode = new HierarchyTreeNode(otherUserMock);
        var otherManagerNode = new HierarchyTreeNode();

        managerNode.id = managerNodeId;
        otherManagerNode.id = managerNodeId;

        var node = new HierarchyTreeNode(managerNode, userMock);
        var otherNode = new HierarchyTreeNode(otherManagerNode, userMock);

        // when / then
        assertThat(node.managersEqual(otherNode)).isTrue();
    }

    @Test
    public void managersEqualReturnsFalseWhenDifferentManagerId() {
        // given
        var managerNodeId = 1L;
        var otherManagerNodeId = 2L;

        var managerNode = new HierarchyTreeNode(otherUserMock);
        var otherManagerNode = new HierarchyTreeNode();

        managerNode.id = managerNodeId;
        otherManagerNode.id = otherManagerNodeId;

        var node = new HierarchyTreeNode(managerNode, userMock);
        var otherNode = new HierarchyTreeNode(otherManagerNode, userMock);

        // when / then
        assertThat(node.managersEqual(otherNode)).isFalse();
    }

    private void compareEqualsWithExpectedEquals(HierarchyTreeNode node, HierarchyTreeNode otherNode, boolean expectedEquals) {
        // when
        var actualNodeEqualsOtherNode = node.equals(otherNode);
        var actualOtherNodeEqualsNode = otherNode.equals(node);

        // then
        var softly = new SoftAssertions();

        softly.assertThat(actualNodeEqualsOtherNode).isEqualTo(expectedEquals);
        softly.assertThat(actualOtherNodeEqualsNode).isEqualTo(expectedEquals);

        softly.assertAll();
    }

    private void assertHashCodesEqual(HierarchyTreeNode node, HierarchyTreeNode otherNode) {
        // when
        var nodeHash = node.hashCode();
        var otherNodeHash = otherNode.hashCode();

        // then
        assertThat(nodeHash).isEqualTo(otherNodeHash);
    }

}