package de.adesso.projectboard.ldap.user;

import de.adesso.projectboard.base.exceptions.UserNotFoundException;
import de.adesso.projectboard.base.security.AuthenticationInfoRetriever;
import de.adesso.projectboard.base.user.persistence.User;
import de.adesso.projectboard.base.user.persistence.UserRepository;
import de.adesso.projectboard.base.user.persistence.data.UserData;
import de.adesso.projectboard.base.user.persistence.data.UserDataRepository;
import de.adesso.projectboard.base.user.persistence.structure.OrganizationStructure;
import de.adesso.projectboard.base.user.persistence.structure.OrganizationStructureRepository;
import de.adesso.projectboard.ldap.service.LdapService;
import de.adesso.projectboard.ldap.service.util.data.StringStructure;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Sort;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class LdapUserServiceTest {

    private final String USER_ID = "user";

    @Mock
    private UserRepository userRepoMock;

    @Mock
    private UserDataRepository userDataRepoMock;

    @Mock
    private OrganizationStructureRepository structureRepoMock;

    @Mock
    private LdapService ldapServiceMock;

    @Mock
    private AuthenticationInfoRetriever authInfoRetrieverMock;

    @Captor
    private ArgumentCaptor<List<User>> userListCaptor;

    private LdapUserService ldapUserService;

    @Before
    public void setUp() {
        this.ldapUserService = new LdapUserService(userRepoMock, userDataRepoMock, ldapServiceMock, structureRepoMock, authInfoRetrieverMock);
    }

    @Test
    public void getAuthenticatedUserReturnsUserWhenUserExists() {
        // given
        User expectedUser = new User(USER_ID);

        given(authInfoRetrieverMock.getUserId()).willReturn(USER_ID);
        given(userRepoMock.findById(USER_ID)).willReturn(Optional.of(expectedUser));

        // when
        User actualUser = ldapUserService.getAuthenticatedUser();

        // then
        assertThat(actualUser).isEqualTo(expectedUser);
    }

    @Test
    public void getAuthenticatedUserThrowsExceptionWhenUserDoesNotExist() {
        // given
        given(authInfoRetrieverMock.getUserId()).willReturn(USER_ID);
        given(userRepoMock.findById(USER_ID)).willReturn(Optional.empty());

        // when
        assertThatThrownBy(() -> ldapUserService.getAuthenticatedUser())
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    public void getAuthenticatedUserIdReturnsExpectedId() {
        // given
        given(authInfoRetrieverMock.getUserId()).willReturn(USER_ID);

        // when
        String actualUserId = ldapUserService.getAuthenticatedUserId();

        // then
        assertThat(actualUserId).isEqualTo(USER_ID);
    }

    @Test
    public void authenticatedUserIsAdminReturnsTrueWhenUserHasAdminRole() {
        // given
        given(authInfoRetrieverMock.hasAdminRole()).willReturn(true);

        // when
        boolean actualAuthenticatedUserIsAdmin = ldapUserService.authenticatedUserIsAdmin();

        // then
        assertThat(actualAuthenticatedUserIsAdmin).isTrue();
    }

    @Test
    public void authenticatedUserIsAdminReturnsFalseWhenUserHasNoAdminRole() {
        // given
        given(authInfoRetrieverMock.hasAdminRole()).willReturn(false);

        // when
        boolean actualAuthenticatedUserIsAdmin = ldapUserService.authenticatedUserIsAdmin();

        // then
        assertThat(actualAuthenticatedUserIsAdmin).isFalse();
    }

    @Test
    public void userExistsReturnsTrueWhenUserExistsInRepo() {
        // given
        given(userRepoMock.existsById(USER_ID)).willReturn(true);

        // when
        boolean actualExists = ldapUserService.userExists(USER_ID);

        // then
        assertThat(actualExists).isTrue();
    }

    @Test
    public void userExistsReturnsFalseWhenUserDoesNotExistInRepo() {
        // given
        given(userRepoMock.existsById(USER_ID)).willReturn(false);

        // when
        boolean actualExists = ldapUserService.userExists(USER_ID);

        // then
        assertThat(actualExists).isFalse();
    }

    @Test
    public void userIsManagerReturnsTrueWhenManagerFieldOfExistingStructureForUserIsTrue() {
        // given
        User user = new User(USER_ID);

        given(structureRepoMock.existsByUser(user)).willReturn(true);
        given(structureRepoMock.existsByUserAndManagingUser(user, true)).willReturn(true);

        // when
        boolean actualIsManager = ldapUserService.userIsManager(user);

        // then
        assertThat(actualIsManager).isTrue();
    }

    @Test
    public void userIsManagerReturnsFalseWhenManagerFieldOfExistingStructureForUserIsFalse() {
        // given
        User user = new User(USER_ID);

        given(structureRepoMock.existsByUser(user)).willReturn(true);
        given(structureRepoMock.existsByUserAndManagingUser(user, true)).willReturn(false);

        // when
        boolean actualIsManager = ldapUserService.userIsManager(user);

        // then
        assertThat(actualIsManager).isFalse();
    }

    @Test
    public void userIsManagerReturnsTrueWhenLdapServiceReturnsTrue() {
        // given
        User user = new User(USER_ID);

        given(structureRepoMock.existsByUser(user)).willReturn(false);
        given(ldapServiceMock.isManager(USER_ID)).willReturn(true);

        // when
        boolean actualIsManager = ldapUserService.userIsManager(user);

        // then
        assertThat(actualIsManager).isTrue();
    }

    @Test
    public void userIsManagerReturnsFalseWhenLdapServiceReturnsFalse() {
        // given
        User user = new User(USER_ID);

        given(structureRepoMock.existsByUser(user)).willReturn(false);
        given(ldapServiceMock.isManager(USER_ID)).willReturn(false);

        // when
        boolean actualIsManager = ldapUserService.userIsManager(user);

        // then
        assertThat(actualIsManager).isFalse();
    }

    @Test
    public void getStructureForUserReturnsPersistedInstanceWhenPresent() {
        // given
        User user = new User(USER_ID);
        User manager = new User("manager");

        OrganizationStructure expectedStructure = new OrganizationStructure(user, manager, Collections.emptySet(), false);

        given(structureRepoMock.findByUser(user)).willReturn(Optional.of(expectedStructure));

        // when
        OrganizationStructure actualStructure = ldapUserService.getStructureForUser(user);

        // then
        assertThat(actualStructure).isEqualTo(expectedStructure);
    }

    @Test
    public void getStructureForUserCreatesNewStructureWhenNoneIsPresent() {
        // given
        String managerId = "manager";
        String staffMemberId = "staff";

        User user = new User(USER_ID);
        User managerUser = new User(managerId);
        User staffUser = new User(staffMemberId);

        OrganizationStructure expectedStructure = new OrganizationStructure(user, managerUser, Collections.singleton(staffUser), true);

        StringStructure idStructure = new StringStructure(user, USER_ID, managerId, Collections.singleton(staffMemberId));

        given(structureRepoMock.findByUser(user)).willReturn(Optional.empty());
        given(ldapServiceMock.userExists(USER_ID)).willReturn(true);
        given(ldapServiceMock.getIdStructure(user)).willReturn(idStructure);

        given(userRepoMock.findById(managerId)).willReturn(Optional.of(managerUser));
        given(userRepoMock.findById(staffMemberId)).willReturn(Optional.of(staffUser));

        given(structureRepoMock.save(expectedStructure)).willReturn(expectedStructure);

        // when
        OrganizationStructure actualStructure = ldapUserService.getStructureForUser(user);

        // then
        assertThat(actualStructure).isEqualTo(expectedStructure);

        verify(structureRepoMock).save(actualStructure);
    }

    @Test
    public void getStructureForUserCreatesNewStructureAndNewUsersWhenNoneIsPresent() {
        // given
        String managerId = "manager";
        String staffMemberId = "staff";

        User user = new User(USER_ID);
        User manager = new User(managerId);
        User staffMember = new User(staffMemberId);

        OrganizationStructure expectedStructure = new OrganizationStructure(user, manager, Collections.singleton(staffMember), true);

        StringStructure idStructure = new StringStructure(user, USER_ID, managerId, Collections.singleton(staffMemberId));

        given(ldapServiceMock.userExists(USER_ID)).willReturn(true);
        given(ldapServiceMock.getIdStructure(user)).willReturn(idStructure);

        given(userRepoMock.findById(managerId)).willReturn(Optional.empty());
        given(userRepoMock.findById(staffMemberId)).willReturn(Optional.empty());

        given(userRepoMock.save(manager)).willReturn(manager);
        given(userRepoMock.save(staffMember)).willReturn(staffMember);

        given(structureRepoMock.findByUser(user)).willReturn(Optional.empty());
        given(structureRepoMock.save(expectedStructure)).willReturn(expectedStructure);

        // when
        OrganizationStructure actualStructure = ldapUserService.getStructureForUser(user);

        // then
        assertThat(actualStructure).isEqualTo(expectedStructure);

        verify(structureRepoMock).save(actualStructure);
        verify(userRepoMock).save(manager);
        verify(userRepoMock).save(staffMember);
    }

    @Test
    public void getStructureForUserCorrectlySetsIsManagerWhenCreatingNewInstance() {
        // given
        String managerId = "manager";

        User user = new User(USER_ID);
        User manager = new User(managerId);

        OrganizationStructure expectedStructure = new OrganizationStructure(user, manager, Collections.emptySet(), false);

        StringStructure idStructure = new StringStructure(user, USER_ID, managerId, Collections.emptySet());

        given(ldapServiceMock.userExists(USER_ID)).willReturn(true);
        given(ldapServiceMock.getIdStructure(user)).willReturn(idStructure);

        given(userRepoMock.findById(managerId)).willReturn(Optional.empty());

        given(userRepoMock.save(manager)).willReturn(manager);

        given(structureRepoMock.findByUser(user)).willReturn(Optional.empty());
        given(structureRepoMock.save(expectedStructure)).willReturn(expectedStructure);

        // when
        OrganizationStructure actualStructure = ldapUserService.getStructureForUser(user);

        // then
        assertThat(actualStructure).isEqualTo(expectedStructure);

        verify(structureRepoMock).save(actualStructure);
        verify(userRepoMock).save(manager);
    }

    @Test
    public void getStructureForUserThrowsExceptionWhenUserDoesNotExistInAD() {
        // given
        User user = new User(USER_ID);

        given(structureRepoMock.findByUser(user)).willReturn(Optional.empty());
        given(ldapServiceMock.userExists(USER_ID)).willReturn(false);

        // when
        assertThatThrownBy(() -> ldapUserService.getStructureForUser(user))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("No user with the given ID exists in the AD!");
    }

    @Test
    public void getUserDataCreatesNewInstanceWhenNoneIsPresent() {
        // given
        User user = new User(USER_ID);
        UserData expectedUserData = new UserData(user, "First", "Last", "mail", "lob");

        given(ldapServiceMock.getUserData(Collections.singletonList(user))).willReturn(Collections.singletonList(expectedUserData));
        given(ldapServiceMock.userExists(USER_ID)).willReturn(true);

        given(userDataRepoMock.findByUser(user)).willReturn(Optional.empty());
        given(userDataRepoMock.save(expectedUserData)).willReturn(expectedUserData);

        // when
        UserData actualUserData = ldapUserService.getUserData(user);

        // then
        assertThat(actualUserData).isEqualTo(expectedUserData);

        verify(userDataRepoMock).save(expectedUserData);
    }

    @Test
    public void getUserDataReturnsPersistedInstanceWhenPresent() {
        // given
        User user = new User(USER_ID);
        UserData expectedUserData = new UserData(user, "First", "Last", "mail", "lob");

        given(userDataRepoMock.findByUser(user)).willReturn(Optional.of(expectedUserData));

        // when
        UserData actualUserData = ldapUserService.getUserData(user);

        // then
        assertThat(actualUserData).isEqualTo(expectedUserData);
    }

    @Test
    public void getUserDataThrowsExceptionWhenUserDoesNotExistInAD() {
        // given
        User user = new User(USER_ID);

        given(userDataRepoMock.findByUser(user)).willReturn(Optional.empty());
        given(ldapServiceMock.userExists(USER_ID)).willReturn(false);

        // when
        assertThatThrownBy(() -> ldapUserService.getUserData(user))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("No user with the given ID exists in the AD!");
    }

    @Test
    public void getUserByIdReturnsUserWhenUserExists() {
        // given
        User user = new User(USER_ID);

        given(userRepoMock.findById(USER_ID)).willReturn(Optional.of(user));

        // when
        User actualUser = ldapUserService.getUserById(USER_ID);

        // then
        assertThat(actualUser).isEqualTo(user);
    }

    @Test
    public void getUserByIdCreatesNewInstanceWhenNoneIsPresentAndUserExistsInAD() {
        // given
        User expectedUser = new User(USER_ID);

        given(userRepoMock.findById(USER_ID)).willReturn(Optional.empty());
        given(ldapServiceMock.userExists(USER_ID)).willReturn(true);

        given(userRepoMock.save(expectedUser)).willReturn(expectedUser);

        // when
        User actualUser = ldapUserService.getUserById(USER_ID);

        // then
        assertThat(actualUser).isEqualTo(expectedUser);
    }

    @Test
    public void getUserByIdThrowsExceptionWhenNoneIsPresentAndUserDoesNotExistInAD() {
        // given
        given(userRepoMock.findById(USER_ID)).willReturn(Optional.empty());
        given(ldapServiceMock.userExists(USER_ID)).willReturn(false);

        // when
        assertThatThrownBy(() -> ldapUserService.getUserById(USER_ID))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    public void userHasStaffMemberReturnsTrueWhenExistsByUserAndStaffMemberContaining() {
        // given
        String managerId = "manager";

        User user = new User(USER_ID);
        User manager = new User(managerId);

        given(structureRepoMock.existsByUserAndStaffMembersContaining(user, manager)).willReturn(true);

        // when
        boolean actualUserHasStaffMember = ldapUserService.userHasStaffMember(user, manager);

        // then
        assertThat(actualUserHasStaffMember).isTrue();
    }

    @Test
    public void userHasStaffMemberReturnsFalseWhenNotExistsByUserAndStaffMemberContaining() {
        // given
        String managerId = "manager";

        User user = new User(USER_ID);
        User manager = new User(managerId);

        given(structureRepoMock.existsByUserAndStaffMembersContaining(user, manager)).willReturn(false);

        // when
        boolean actualUserHasStaffMember = ldapUserService.userHasStaffMember(user, manager);

        // then
        assertThat(actualUserHasStaffMember).isFalse();
    }

    @Test
    public void getManagerOfUserReturnsManagerOfPersistedInstanceWhenPresent() {
        // given
        String managerId = "manager";

        User user = new User(USER_ID);
        User expectedManager = new User(managerId);

        OrganizationStructure structure = new OrganizationStructure(user, expectedManager, Collections.emptySet(), false);

        given(structureRepoMock.findByUser(user)).willReturn(Optional.of(structure));

        // when
        User actualManager = ldapUserService.getManagerOfUser(user);

        // then
        assertThat(actualManager).isEqualTo(expectedManager);
    }

    @Test
    public void getManagerOfUserReturnsManagerWhenNoneIsPresent() {
        // given
        String managerId = "manager";
        User user = new User(USER_ID);
        User expectedManager = new User(managerId);

        given(structureRepoMock.findByUser(user)).willReturn(Optional.empty());

        given(ldapServiceMock.getManagerId(user)).willReturn(managerId);
        given(ldapServiceMock.userExists(USER_ID)).willReturn(true);

        given(userRepoMock.findById(managerId)).willReturn(Optional.of(expectedManager));

        // when
        User actualManager = ldapUserService.getManagerOfUser(user);

        // then
        assertThat(actualManager).isEqualTo(expectedManager);
    }

    @Test
    public void getStaffMemberDataOfUser() {
        // given
        Sort sort = Sort.unsorted();

        String managerId = "manager";
        String staffMemberWithDataId = "staff-1";
        String staffMemberWithoutDataId = "staff-2";

        User user = new User(USER_ID);
        User manager = new User(managerId);
        User staffMemberWithData = new User(staffMemberWithDataId);
        User staffMemberWithoutData = new User(staffMemberWithoutDataId);

        Set<User> staffMembers = new HashSet<>(Arrays.asList(staffMemberWithData, staffMemberWithoutData));
        OrganizationStructure structure = new OrganizationStructure(user, manager, staffMembers, true);

        UserData staffMemberWithDataData = new UserData(staffMemberWithData, "First", "Last", "email", "lob");
        UserData staffMemberWithoutDataData = new UserData(staffMemberWithoutData, "First", "Last", "email", "lob");

        given(structureRepoMock.findByUser(user)).willReturn(Optional.of(structure));

        given(userDataRepoMock.existsByUser(staffMemberWithData)).willReturn(true);
        given(userDataRepoMock.existsByUser(staffMemberWithoutData)).willReturn(false);

        given(userDataRepoMock.findByUserIn(staffMembers, sort))
                .willReturn(Arrays.asList(staffMemberWithDataData, staffMemberWithoutDataData));

        given(ldapServiceMock.getUserData(Collections.singletonList(staffMemberWithoutData)))
                .willReturn(Collections.singletonList(staffMemberWithoutDataData));

        // when
        List<UserData> actualStaffData = ldapUserService.getStaffMemberUserDataOfUser(user, sort);

        // then
        assertThat(actualStaffData).containsExactlyInAnyOrder(staffMemberWithDataData, staffMemberWithoutDataData);

        verify(ldapServiceMock).getUserData(userListCaptor.capture());
        assertThat(userListCaptor.getValue()).containsExactlyInAnyOrder(staffMemberWithoutData);

        verify(userDataRepoMock).saveAll(Collections.singletonList(staffMemberWithoutDataData));
    }

    @Test
    public void save() {
        // given
        User user = new User(USER_ID);

        given(userRepoMock.save(user)).willReturn(user);

        // when
        User actualUser = ldapUserService.save(user);

        // then
        assertThat(actualUser).isEqualTo(user);

        verify(userRepoMock).save(user);
    }

    @Test
    public void usersAreManagers() {
        // given
        String managerId = "manager";
        String userWithExistingStructId = "user-1";
        String userWithoutExistingStructId = "user-2";

        User manager = new User(managerId);
        User userWithExistingStruct = new User(userWithExistingStructId);
        User userWithoutExistingStruct = new User(userWithoutExistingStructId);

        OrganizationStructure structureForUserWithExistingStruct
                = new OrganizationStructure(userWithExistingStruct, manager, Collections.emptySet(), true);

        Set<User> users = new HashSet<>(Arrays.asList(userWithExistingStruct, userWithoutExistingStruct));

        given(structureRepoMock.findAllByUserIn(users)).willReturn(Collections.singletonList(structureForUserWithExistingStruct));

        given(ldapServiceMock.isManager(userWithoutExistingStructId)).willReturn(false);

        // when
        Map<User, Boolean> actualUserManagerMap = ldapUserService.usersAreManagers(users);

        // then
        assertThat(actualUserManagerMap).containsOnly(entry(userWithExistingStruct, true), entry(userWithoutExistingStruct, false));

        verify(ldapServiceMock).isManager(userWithoutExistingStructId);
    }

    @Test
    public void validateExistenceReturnsUserWhenUserExistsInAD() {
        // given
        User expectedUser = new User(USER_ID);

        given(ldapServiceMock.userExists(USER_ID)).willReturn(true);

        // when
        User actualUser = ldapUserService.validateExistence(expectedUser);

        // then
        assertThat(actualUser).isEqualTo(expectedUser);
    }

    @Test
    public void validateExistenceThrowsExceptionWhenUserDoesNotExistInAD() {
        // given
        User user = new User(USER_ID);

        given(ldapServiceMock.userExists(USER_ID)).willReturn(false);

        // when
        assertThatThrownBy(() -> ldapUserService.validateExistence(user))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("No user with the given ID exists in the AD!");
    }

}