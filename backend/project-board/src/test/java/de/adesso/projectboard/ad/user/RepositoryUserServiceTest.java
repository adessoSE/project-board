package de.adesso.projectboard.ad.user;

import de.adesso.projectboard.base.exceptions.HierarchyNotFoundException;
import de.adesso.projectboard.base.exceptions.UserDataNotFoundException;
import de.adesso.projectboard.base.exceptions.UserNotFoundException;
import de.adesso.projectboard.base.user.persistence.User;
import de.adesso.projectboard.base.user.persistence.UserRepository;
import de.adesso.projectboard.base.user.persistence.data.UserData;
import de.adesso.projectboard.base.user.persistence.data.UserDataRepository;
import de.adesso.projectboard.base.user.persistence.hierarchy.HierarchyTreeNode;
import de.adesso.projectboard.base.user.persistence.hierarchy.HierarchyTreeNodeRepository;
import org.assertj.core.data.MapEntry;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class RepositoryUserServiceTest {

    private final String USER_ID = "user";

    @Mock
    private UserRepository userRepoMock;

    @Mock
    private UserDataRepository userDataRepoMock;

    @Mock
    private HierarchyTreeNodeRepository hierarchyTreeNodeRepoMock;

    @Mock
    private User userMock;

    @Mock
    private User otherUserMock;

    @Mock
    private HierarchyTreeNode hierarchyTreeNodeMock;

    @Mock
    private HierarchyTreeNode otherHierarchyTreeNodeMock;

    @Mock
    private UserData userDataMock;

    private RepositoryUserService repoUserService;

    @Before
    public void setUp() {
        this.repoUserService = new RepositoryUserService(userRepoMock, userDataRepoMock, hierarchyTreeNodeRepoMock);

        given(userMock.getId()).willReturn(USER_ID);
    }

    @Test
    public void getUserByIdReturnsUserWhenUserIsPresent() {
        // given
        given(userRepoMock.findById("user")).willReturn(Optional.of(userMock));

        // when
        var actualUser = repoUserService.getUserById(USER_ID);

        // then
        assertThat(actualUser).isEqualTo(userMock);
    }

    @Test
    public void getUserByIdThrowsExceptionWhenUserNotPresent() {
        // given
        var expectedMessage = String.format("User with ID '%s' not found!", USER_ID);

        // when / then
        assertThatThrownBy(() -> repoUserService.getUserById(USER_ID))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage(expectedMessage);
    }

    @Test
    public void userExistsReturnsTrueWhenUserPresent() {
        // given
        given(userRepoMock.existsById(USER_ID)).willReturn(true);

        // when / then
        compareUserExistsWithExpectedExists(USER_ID, true);
    }

    @Test
    public void userExistsReturnsFalseWhenUserNotPresent() {
        // given
        given(userRepoMock.existsById(USER_ID)).willReturn(false);

        // when / then
        compareUserExistsWithExpectedExists(USER_ID, false);
    }

    @Test
    public void userIsManagerReturnsTrueWhenHierarchyPresentAndManagingUserTrue() {
        // given
        given(hierarchyTreeNodeRepoMock.existsByUserAndManagingUserTrue(userMock)).willReturn(true);

        // when / then
        compareUserIsManagerWithExpectedIsManager(userMock, true);
    }

    @Test
    public void userIsManagerReturnsFalseWhenHierarchyPresentAndManagingUserFalse() {
        // given
        given(hierarchyTreeNodeRepoMock.existsByUserAndManagingUserTrue(userMock)).willReturn(false);

        // when / then
        compareUserIsManagerWithExpectedIsManager(userMock, false);
    }

    @Test
    public void userIsManagerReturnsFalseWhenHierarchyNotPresent() {
        // given
        given(hierarchyTreeNodeRepoMock.existsByUserAndManagingUserTrue(userMock)).willReturn(false);

        // when / then
        compareUserIsManagerWithExpectedIsManager(userMock, false);
    }

    @Test
    public void getHierarchyForUserReturnsHierarchyWhenPresent() {
        // given
        given(hierarchyTreeNodeRepoMock.findByUser(userMock)).willReturn(Optional.of(hierarchyTreeNodeMock));

        // when
        var actualHierarchy = repoUserService.getHierarchyForUser(userMock);

        // then
        assertThat(actualHierarchy).isEqualTo(hierarchyTreeNodeMock);
    }

    @Test
    public void getHierarchyForUserThrowsExceptionWhenHierarchyNotPresent() {
        // given
        var expectedMessage = String.format("Hierarchy for User with ID '%s' not found!", USER_ID);

        given(hierarchyTreeNodeRepoMock.findByUser(userMock)).willReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> repoUserService.getHierarchyForUser(userMock))
                .isInstanceOf(HierarchyNotFoundException.class)
                .hasMessage(expectedMessage);
    }

    @Test
    public void getUserDataReturnsDataWhenPresent() {
        // given
        given(userDataRepoMock.findByUser(userMock)).willReturn(Optional.of(userDataMock));

        // when
        var actualData = repoUserService.getUserData(userMock);

        // then
        assertThat(actualData).isEqualTo(userDataMock);
    }

    @Test
    public void getUserDataThrowsExceptionWhenDataNotPresent() {
        // given
        var expectedMessage = String.format("Data for User with ID '%s' not found!", USER_ID);

        given(userDataRepoMock.findByUser(userMock)).willReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> repoUserService.getUserData(userMock))
                .isInstanceOf(UserDataNotFoundException.class)
                .hasMessage(expectedMessage);
    }

    @Test
    public void userHasStaffMemberReturnsTrueWhenHierarchyPresentAndStaffContainsUser() {
        // given
        given(hierarchyTreeNodeRepoMock.findByUser(userMock)).willReturn(Optional.of(hierarchyTreeNodeMock));
        given(hierarchyTreeNodeRepoMock.existsByUserAndStaffContaining(userMock, hierarchyTreeNodeMock)).willReturn(true);

        // when / then
        compareUserHasStaffMemberWithExpectedHasStaffMember(userMock, userMock, true);
    }

    @Test
    public void userHasStaffMemberReturnsFalseWhenHierarchyPresentAndStaffNotContainsUser() {
        // given
        given(hierarchyTreeNodeRepoMock.findByUser(userMock)).willReturn(Optional.of(hierarchyTreeNodeMock));
        given(hierarchyTreeNodeRepoMock.existsByUserAndStaffContaining(userMock, hierarchyTreeNodeMock)).willReturn(true);

        // when / then
        compareUserHasStaffMemberWithExpectedHasStaffMember(userMock, userMock, true);
    }

    @Test
    public void userHasStaffMemberThrowsExceptionWhenHierarchyNotPresent() {
        // given
        var expectedMessage = String.format("Hierarchy for User with ID '%s' not found!", USER_ID);

        given(hierarchyTreeNodeRepoMock.findByUser(userMock)).willReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> repoUserService.userHasStaffMember(userMock, userMock))
                .isInstanceOf(HierarchyNotFoundException.class)
                .hasMessage(expectedMessage);
    }

    @Test
    public void getManagerOfUserReturnsManagerWhenHierarchyPresent() {
        // given
        given(hierarchyTreeNodeRepoMock.findByUser(userMock)).willReturn(Optional.of(hierarchyTreeNodeMock));

        given(hierarchyTreeNodeMock.getManager()).willReturn(otherHierarchyTreeNodeMock);
        given(otherHierarchyTreeNodeMock.getUser()).willReturn(otherUserMock);

        // when
        var actualManager = repoUserService.getManagerOfUser(userMock);

        // then
        assertThat(actualManager).isEqualTo(otherUserMock);
    }

    @Test
    public void getManagerOfUserReturnsHierarchyUserWhenManagerIsNull() {
        // given
        given(hierarchyTreeNodeRepoMock.findByUser(userMock)).willReturn(Optional.of(hierarchyTreeNodeMock));

        given(hierarchyTreeNodeMock.isRoot()).willReturn(true);
        given(hierarchyTreeNodeMock.getUser()).willReturn(userMock);

        // when
        var actualManager = repoUserService.getManagerOfUser(userMock);

        // then
        assertThat(actualManager).isEqualTo(userMock);
    }

    @Test
    public void getManagerOfUserThrowsExceptionWhenHierarchyNotPresent() {
        // given
        var expectedMessage = String.format("Hierarchy for User with ID '%s' not found!", USER_ID);

        given(hierarchyTreeNodeRepoMock.findByUser(userMock)).willReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> repoUserService.getManagerOfUser(userMock))
                .isInstanceOf(HierarchyNotFoundException.class)
                .hasMessage(expectedMessage);
    }

    @Test
    public void saveReturnsSavedUser() {
        // given
        given(userRepoMock.save(userMock)).willReturn(otherUserMock);

        // when
        var actualUser = repoUserService.save(userMock);

        // then
        assertThat(actualUser).isEqualTo(otherUserMock);

        verify(userRepoMock).save(userMock);
    }

    @Test
    public void usersAreManagers() {
        // given
        var users = Set.of(userMock, otherUserMock);
        var hierarchies = List.of(hierarchyTreeNodeMock, otherHierarchyTreeNodeMock);

        given(hierarchyTreeNodeMock.getUser()).willReturn(userMock);
        given(hierarchyTreeNodeMock.isManagingUser()).willReturn(true);

        given(otherHierarchyTreeNodeMock.getUser()).willReturn(otherUserMock);
        given(otherHierarchyTreeNodeMock.isManagingUser()).willReturn(false);

        given(hierarchyTreeNodeRepoMock.findByUserIn(users)).willReturn(hierarchies);

        // when
        var actualMap = repoUserService.usersAreManagers(users);

        // then
        assertThat(actualMap)
                .containsOnly(MapEntry.entry(userMock, true), MapEntry.entry(otherUserMock, false));
    }

    @Test
    public void usersAreManagersThrowsExceptionWhenNoHierarchyPresentForSomeUser() {
        // given
        var expectedMessage = "Hierarchy for some users not present!";
        var users = Set.of(userMock, otherUserMock);
        var hierarchies = List.of(hierarchyTreeNodeMock);

        given(hierarchyTreeNodeMock.getUser()).willReturn(userMock);
        given(hierarchyTreeNodeMock.isManagingUser()).willReturn(true);

        given(hierarchyTreeNodeRepoMock.findByUserIn(users)).willReturn(hierarchies);

        // when / then
        assertThatThrownBy(() -> repoUserService.usersAreManagers(users))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(expectedMessage);
    }

    @Test
    public void getOrCreateUserByIdReturnsUserWhenPresent() {
        // given
        given(userRepoMock.findById(USER_ID)).willReturn(Optional.of(userMock));

        // when
        var actualUser = repoUserService.getOrCreateUserById(USER_ID);

        // then
        assertThat(actualUser).isEqualTo(userMock);
    }

    @Test
    public void getOrCreateUserByIdCreatesNewUserWhenNotPresent() {
        // given
        var expectedUser = new User(USER_ID);

        given(userRepoMock.findById(USER_ID)).willReturn(Optional.empty());
        given(userRepoMock.save(expectedUser)).willReturn(expectedUser);

        // when
        var actualUser = repoUserService.getOrCreateUserById(USER_ID);

        // then
        assertThat(actualUser).isEqualTo(expectedUser);
        verify(userRepoMock).save(actualUser);
    }

    private void compareUserExistsWithExpectedExists(String userId, boolean expected) {
        // when
        var actualExists = repoUserService.userExists(userId);

        // then
        assertThat(actualExists).isEqualTo(expected);
    }

    private void compareUserIsManagerWithExpectedIsManager(User user, boolean expected) {
        // when
        var actualIsManager = repoUserService.userIsManager(user);

        // then
        assertThat(actualIsManager).isEqualTo(expected);
    }

    private void compareUserHasStaffMemberWithExpectedHasStaffMember(User user, User staffMember, boolean expected) {
        // when
        var actualHasStaffMember = repoUserService.userHasStaffMember(user, staffMember);

        // then
        assertThat(actualHasStaffMember).isEqualTo(expected);
    }

}