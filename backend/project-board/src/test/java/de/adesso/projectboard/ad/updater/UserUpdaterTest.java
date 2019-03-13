package de.adesso.projectboard.ad.updater;

import de.adesso.projectboard.ad.service.LdapAdapter;
import de.adesso.projectboard.ad.service.node.LdapUserNode;
import de.adesso.projectboard.ad.user.RepositoryUserService;
import de.adesso.projectboard.base.user.persistence.User;
import de.adesso.projectboard.base.user.persistence.data.UserData;
import de.adesso.projectboard.base.user.persistence.data.UserDataRepository;
import de.adesso.projectboard.base.user.persistence.hierarchy.HierarchyTreeNode;
import de.adesso.projectboard.base.user.persistence.hierarchy.HierarchyTreeNodeRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class UserUpdaterTest {

    @Mock
    private HierarchyTreeNodeRepository hierarchyTreeNodeRepoMock;

    @Mock
    private LdapAdapter ldapAdapterMock;

    @Mock
    private RepositoryUserService repoUserServiceMock;

    @Mock
    private UserDataRepository userDataRepoMock;

    @Captor
    private ArgumentCaptor<Collection<UserData>> userDataCaptor;

    private UserUpdater userUpdater;

    @Before
    public void setUp() {
        this.userUpdater = new UserUpdater(hierarchyTreeNodeRepoMock, repoUserServiceMock, userDataRepoMock, ldapAdapterMock);
    }

    @Test
    public void updateUserDataDeletesAllAndSavesNew() {
        // given
        var firstUserId = "first-user";
        var expectedFirstDepartment = "first-department";
        var expectedFirstDivision = "first-division";
        var expectedFirstGivenName = "first-given-name";
        var expectedFirstSurname = "first-surname";
        var expectedFirstName = "first-full-name";
        var expectedFirstMail = "first@mail.com";

        var secondUserId = "second-user";
        var expectedSecondDepartment = "second-department";
        var expectedSecondDivision = "second-division";
        var expectedSecondGivenName = "second-given-name";
        var expectedSecondSurname = "second-surname";
        var expectedSecondName = "second-full-name";
        var expectedSecondMail = "second@mail.com";

        var firstUser = new User(firstUserId);
        var secondUser = new User(secondUserId);

        var firstNode = new LdapUserNode()
                .setId(firstUserId)
                .setDepartment(expectedFirstDepartment)
                .setDivision(expectedFirstDivision)
                .setGivenName(expectedFirstGivenName)
                .setSurname(expectedFirstSurname)
                .setName(expectedFirstName)
                .setMail(expectedFirstMail);
        var secondNode = new LdapUserNode()
                .setId(secondUserId)
                .setDepartment(expectedSecondDepartment)
                .setDivision(expectedSecondDivision)
                .setGivenName(expectedSecondGivenName)
                .setSurname(expectedSecondSurname)
                .setName(expectedSecondName)
                .setMail(expectedSecondMail);

        var firstExpected = new UserData(firstUser, expectedFirstGivenName, expectedFirstSurname, expectedFirstMail, expectedFirstDivision);
        var secondExpected = new UserData(secondUser, expectedSecondGivenName, expectedSecondSurname, expectedSecondMail, expectedSecondDivision);

        given(repoUserServiceMock.getOrCreateUserById(firstUserId)).willReturn(firstUser);
        given(repoUserServiceMock.getOrCreateUserById(secondUserId)).willReturn(secondUser);

        // when
        userUpdater.updateUserData(List.of(firstNode, secondNode));

        // then
        verify(userDataRepoMock).deleteAllInBatch();
        verify(userDataRepoMock).saveAll(userDataCaptor.capture());

        assertThat(userDataCaptor.getValue()).containsExactlyInAnyOrder(firstExpected, secondExpected);
    }

    @Test
    public void getRootNodesReturnsExpectedNodes() {
        // given
        var rootNodeDn = "root-dn";
        var staffNodeDn = "user-dn";

        var rootNode = new LdapUserNode()
                .setDn(rootNodeDn)
                .setManagerDn(rootNodeDn);

        var staffNode = new LdapUserNode()
                .setDn(staffNodeDn)
                .setManagerDn(rootNodeDn);

        // when
        var actualRootNodes = userUpdater.getRootNodes(List.of(rootNode, staffNode));

        // then
        assertThat(actualRootNodes).containsExactly(rootNode);
    }

    @Test
    public void buildHierarchyNodesReturnsExpectedNodes() {
        // given
        var firstRootUserId = "first-root-user";
        var secondRootUserId = "second-root-user";
        var firstRootChildUserId = "first-root-child-user";
        var secondRootChildUserId = "second-root-child-user";

        var firstRootUser = new User(firstRootUserId);
        var secondRootUser = new User(secondRootUserId);
        var firstRootChildUser = new User(firstRootChildUserId);
        var secondRootChildUser = new User(secondRootChildUserId);

        var firstRootDn = "first-root-dn";
        var secondRootDn = "second-root-dn";
        var firstRootChildDn = "first-root-child-dn";
        var secondRootChildDn = "second-root-child-dn";

        var firstRootNode = new LdapUserNode()
                .setId(firstRootUserId)
                .setDn(firstRootDn)
                .setManagerDn(firstRootDn)
                .setDirectReportsDn(List.of(firstRootChildDn));
        var firstRootChildNode = new LdapUserNode()
                .setId(firstRootChildUserId)
                .setDn(firstRootChildDn)
                .setManagerDn(firstRootDn);

        var secondRootNode = new LdapUserNode()
                .setId(secondRootUserId)
                .setDn(secondRootDn)
                .setManagerDn(secondRootDn)
                .setDirectReportsDn(List.of(secondRootChildDn));
        var secondRootChildNode = new LdapUserNode()
                .setId(secondRootChildUserId)
                .setDn(secondRootChildDn)
                .setManagerDn(secondRootDn);

        var firstRootChildHierarchyNode = new HierarchyTreeNode(firstRootChildUser);
        var expectedFirstRootHierarchyNode = new HierarchyTreeNode(firstRootUser);
        expectedFirstRootHierarchyNode.addDirectStaffMember(firstRootChildHierarchyNode);

        var secondRootChildHierarchyNode = new HierarchyTreeNode(secondRootChildUser);
        var expectedSecondRootHierarchyNode = new HierarchyTreeNode(secondRootUser);
        expectedSecondRootHierarchyNode.addDirectStaffMember(secondRootChildHierarchyNode);

        given(repoUserServiceMock.getOrCreateUserById(firstRootUserId)).willReturn(firstRootUser);
        given(repoUserServiceMock.getOrCreateUserById(firstRootChildUserId)).willReturn(firstRootChildUser);
        given(repoUserServiceMock.getOrCreateUserById(secondRootUserId)).willReturn(secondRootUser);
        given(repoUserServiceMock.getOrCreateUserById(secondRootChildUserId)).willReturn(secondRootChildUser);

        // when
        var actualRootHierarchyNodes = userUpdater.buildHierarchyNodes(List.of(firstRootNode, secondRootNode),
                List.of(firstRootNode, secondRootNode, firstRootChildNode, secondRootChildNode));

        // then
        assertThat(actualRootHierarchyNodes).containsExactly(expectedFirstRootHierarchyNode, expectedSecondRootHierarchyNode);
    }

    @Test
    public void buildHierarchyNodesThrowsExceptionWhenNodesMissing() {
        // given
        var rootUserId = "root-user";
        var rootNodeDn = "root-dn";
        var missingNodeDn = "mssing-node-dn";

        var rootNode = new LdapUserNode()
                .setId(rootUserId)
                .setDn(rootNodeDn)
                .setManagerDn(rootNodeDn)
                .setDirectReportsDn(List.of(missingNodeDn));

        var expectedMessage = String.format("Child node with DN '%s' not found!", missingNodeDn);

        // when / then
        assertThatThrownBy(() -> userUpdater.buildHierarchyNodes(List.of(rootNode), List.of(rootNode)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(expectedMessage);
    }

    @Test
    public void buildHierarchyNodeThrowsExceptionWhenChildNodesNotInLevelOrder() {
        // given
        var rootUserId = "root-user";
        var firstLevelUserId = "first-level-user";
        var secondLevelUserId = "second-level-user";

        var rootNodeDn = "root-dn";
        var firstLevelDn = "first-level-dn";
        var secondLevelDn = "second-level-dn";

        var rootNode = new LdapUserNode()
                .setId(rootUserId)
                .setDn(rootNodeDn)
                .setManagerDn(rootNodeDn)
                .setDirectReportsDn(List.of(firstLevelDn));

        var firstLevelNode = new LdapUserNode()
                .setId(firstLevelUserId)
                .setDn(firstLevelDn)
                .setManagerDn(rootNodeDn)
                .setDirectReportsDn(List.of(secondLevelDn));

        var secondLevelNode = new LdapUserNode()
                .setId(secondLevelUserId)
                .setDn(secondLevelDn)
                .setManagerDn(firstLevelDn);

        given(repoUserServiceMock.getOrCreateUserById(rootUserId)).willReturn(new User(rootUserId));
        given(repoUserServiceMock.getOrCreateUserById(secondLevelUserId)).willReturn(new User(secondLevelUserId));

        // when / then
        assertThatThrownBy(() -> userUpdater.buildHierarchyNode(rootNode, List.of(secondLevelNode, firstLevelNode)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Child nodes not in level order!");
    }

    @Test
    public void buildHierarchyNodeReturnsExpectedHierarchyNode() {
        // given
        var rootUserId = "root-user";
        var firstLevelUserId = "first-level-user";
        var secondLevelUserId = "second-level-user";

        var rootUser = new User(rootUserId);
        var firstLevelUser = new User(firstLevelUserId);
        var secondLevelUser = new User(secondLevelUserId);

        var rootNodeDn = "root-dn";
        var firstLevelDn = "first-level-dn";
        var secondLevelDn = "second-level-dn";

        var rootNode = new LdapUserNode()
                .setId(rootUserId)
                .setDn(rootNodeDn)
                .setManagerDn(rootNodeDn)
                .setDirectReportsDn(List.of(firstLevelDn));
        var firstLevelNode = new LdapUserNode()
                .setId(firstLevelUserId)
                .setDn(firstLevelDn)
                .setManagerDn(rootNodeDn)
                .setDirectReportsDn(List.of(secondLevelDn));
        var secondLevelNode = new LdapUserNode()
                .setId(secondLevelUserId)
                .setDn(secondLevelDn)
                .setManagerDn(firstLevelDn);

        var firstLevelHierarchy = new HierarchyTreeNode(firstLevelUser);
        var secondLevelHierarchy = new HierarchyTreeNode(secondLevelUser);
        var expectedHierarchyNode = new HierarchyTreeNode(rootUser);
        expectedHierarchyNode.addDirectStaffMember(firstLevelHierarchy);
        firstLevelHierarchy.addDirectStaffMember(secondLevelHierarchy);

        given(repoUserServiceMock.getOrCreateUserById(rootUserId)).willReturn(rootUser);
        given(repoUserServiceMock.getOrCreateUserById(firstLevelUserId)).willReturn(firstLevelUser);
        given(repoUserServiceMock.getOrCreateUserById(secondLevelUserId)).willReturn(secondLevelUser);

        // when
        var actualHierarchyNode = userUpdater.buildHierarchyNode(rootNode, List.of(firstLevelNode, secondLevelNode));

        // then
        assertThat(actualHierarchyNode).isEqualTo(expectedHierarchyNode);
    }

    @Test
    public void getChildNodesInLevelOrderReturnsChildNodesInLevelOrder() {
        // given
        var firstLevelNodeDn1 = "first-level-child-1";
        var firstLevelNodeDn2 = "first-level-child-2";
        var secondLevelNodeDn = "second-level-child";

        var rootNode = new LdapUserNode()
                .setDirectReportsDn(List.of(firstLevelNodeDn1, firstLevelNodeDn2));
        var firstLevelNode1 = new LdapUserNode()
                .setDirectReportsDn(List.of(secondLevelNodeDn));
        var firstLevelNode2 = new LdapUserNode();
        var secondLevelNode = new LdapUserNode();

        var dnNodeMap = Map.of(
                firstLevelNodeDn1, firstLevelNode1,
                firstLevelNodeDn2, firstLevelNode2,
                secondLevelNodeDn, secondLevelNode);

        var expectedOrder = List.of(firstLevelNode1, firstLevelNode2, secondLevelNode);

        // when
        var actualOrder = userUpdater.getChildNodesInLevelOrder(rootNode, dnNodeMap);

        // then
        assertThat(actualOrder).containsExactlyElementsOf(expectedOrder);
    }

    @Test
    public void getChildNodesInLevelOrderThrowsExceptionWhenMapNotContainsDn() {
        // given
        var childNodeDn = "child";

        var rootNode = new LdapUserNode()
                .setDirectReportsDn(List.of(childNodeDn));

        var expectedMessage = String.format("Child node with DN '%s' not found!", childNodeDn);

        // when / then
        assertThatThrownBy(() -> userUpdater.getChildNodesInLevelOrder(rootNode, Collections.emptyMap()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(expectedMessage);
    }

    @Test
    public void filterNodesWithMissingManagerFiltersNodeWhenManagerDnNotPresent() {
        // given
        var nodeToFilter = new LdapUserNode()
                .setDn("user-dn")
                .setManagerDn("other-manager-dn");

        var nodeToKeep = new LdapUserNode()
                .setDn("root-manager-dn")
                .setManagerDn("root-manager-dn");


        // when / then
        compareActualWithExpectedFilteredNodes(List.of(nodeToFilter, nodeToKeep), List.of(nodeToKeep));
    }

    @Test
    public void filterNodesWithMissingManagerFiltersNodeWhenManagerDnOfManagerNotPresent() {
        // given
        var managerDn = "manager-dn";
        var staffDn = "staff-dn";

        var managerNodeToFilter = new LdapUserNode()
                .setDn(managerDn)
                .setManagerDn("other-manager-dn")
                .setDirectReportsDn(List.of(staffDn));

        var staffNodeToFilter = new LdapUserNode()
                .setDn(staffDn)
                .setManagerDn(managerDn);

        // when / then
        compareActualWithExpectedFilteredNodes(List.of(managerNodeToFilter, staffNodeToFilter), Collections.emptyList());
    }

    @Test
    public void cleanDirectReportsRemovesNodesOwnDn() {
        // given
        var staffDn = "staff-dn";
        var managerDn = "manager-dn";

        var managerNode = new LdapUserNode()
                .setDn(managerDn)
                .setDirectReportsDn(List.of(staffDn, managerDn));
        var staffNode = new LdapUserNode()
                .setDn(staffDn);

        var expectedManagerNode = new LdapUserNode()
                .setDn(managerDn)
                .setDirectReportsDn(List.of(staffDn));
        var expectedStaffNode = new LdapUserNode()
                .setDn(staffDn);

        // when / then
        compareActualWithExpectedCleanedNodes(List.of(managerNode, staffNode), List.of(expectedManagerNode, expectedStaffNode));
    }

    @Test
    public void cleanDirectReportsRemovesDnReferringToNotExistingNode() {
        // given
        var managerDn = "manager-dn";
        var staffDn = "user-dn";

        var managerNode = new LdapUserNode()
                .setDn(managerDn)
                .setDirectReportsDn(List.of(staffDn, "other-dn"));
        var staffNode = new LdapUserNode()
                .setDn(staffDn);

        var expectedManagerNode = new LdapUserNode()
                .setDn(managerDn)
                .setDirectReportsDn(List.of(staffDn));
        var expectedStaffNode = new LdapUserNode()
                .setDn(staffDn);

        // when / then
        compareActualWithExpectedCleanedNodes(List.of(managerNode, staffNode), List.of(expectedManagerNode, expectedStaffNode));
    }

    private void compareActualWithExpectedFilteredNodes(Collection<LdapUserNode> unfiltered, Collection<LdapUserNode> expectedFiltered) {
        // when
        var actualFiltered = userUpdater.filterNodesWithMissingManager(unfiltered);

        // then
        assertThat(actualFiltered).containsExactlyInAnyOrderElementsOf(expectedFiltered);
    }

    private void compareActualWithExpectedCleanedNodes(Collection<LdapUserNode> nodes, Collection<LdapUserNode> expectedNodes) {
        // when
        var actualNodes = userUpdater.cleanDirectReports(nodes);

        // then
        assertThat(actualNodes).containsExactlyInAnyOrderElementsOf(expectedNodes);
    }

}
