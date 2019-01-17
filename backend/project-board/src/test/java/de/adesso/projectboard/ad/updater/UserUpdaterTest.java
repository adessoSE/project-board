package de.adesso.projectboard.ad.updater;

import de.adesso.projectboard.ad.configuration.LdapConfigurationProperties;
import de.adesso.projectboard.ad.service.LdapService;
import de.adesso.projectboard.ad.service.node.LdapUserNode;
import de.adesso.projectboard.ad.user.RepositoryUserService;
import de.adesso.projectboard.base.user.persistence.data.UserDataRepository;
import de.adesso.projectboard.base.user.persistence.hierarchy.HierarchyTreeNodeRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class UserUpdaterTest {

    @Mock
    private HierarchyTreeNodeRepository hierarchyTreeNodeRepoMock;

    @Mock
    private LdapService ldapServiceMock;

    @Mock
    private LdapConfigurationProperties configPropertiesMock;

    @Mock
    private RepositoryUserService repoUserServiceMock;

    @Mock
    private UserDataRepository userDataRepo;

    private UserUpdater userUpdater;

    @Before
    public void setUp() throws Exception {
        given(configPropertiesMock.getLdapBase()).willReturn("base");

        this.userUpdater = new UserUpdater(hierarchyTreeNodeRepoMock, repoUserServiceMock, userDataRepo, ldapServiceMock);
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

}