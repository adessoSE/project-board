package de.adesso.projectboard.ldap.user;

import de.adesso.projectboard.base.exceptions.UserNotFoundException;
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

    @Captor
    private ArgumentCaptor<List<User>> userListCaptor;

    private LdapUserService ldapUserService;

    @Before
    public void setUp() {
        this.ldapUserService = new LdapUserService(userRepoMock, userDataRepoMock, ldapServiceMock, structureRepoMock);
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
        var user = new User(USER_ID);

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
        var user = new User(USER_ID);

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
        var user = new User(USER_ID);

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
        var user = new User(USER_ID);

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
        var user = new User(USER_ID);
        var manager = new User("manager");

        var expectedStructure = new OrganizationStructure(user, manager, Collections.emptySet(), false);

        given(structureRepoMock.findByUser(user)).willReturn(Optional.of(expectedStructure));

        // when
        var actualStructure = ldapUserService.getStructureForUser(user);

        // then
        assertThat(actualStructure).isEqualTo(expectedStructure);
    }

    @Test
    public void getStructureForUserCreatesNewStructureWhenNoneIsPresent() {
        // given
        var managerId = "manager";
        var staffMemberId = "staff";

        var user = new User(USER_ID);
        var managerUser = new User(managerId);
        var staffUser = new User(staffMemberId);

        var expectedStructure = new OrganizationStructure(user, managerUser, Collections.singleton(staffUser), true);

        var idStructure = new StringStructure(user, USER_ID, managerId, Collections.singleton(staffMemberId));

        given(structureRepoMock.findByUser(user)).willReturn(Optional.empty());
        given(ldapServiceMock.userExists(USER_ID)).willReturn(true);
        given(ldapServiceMock.getIdStructure(user)).willReturn(idStructure);

        given(userRepoMock.findById(managerId)).willReturn(Optional.of(managerUser));
        given(userRepoMock.findById(staffMemberId)).willReturn(Optional.of(staffUser));

        given(structureRepoMock.save(expectedStructure)).willReturn(expectedStructure);

        // when
        var actualStructure = ldapUserService.getStructureForUser(user);

        // then
        assertThat(actualStructure).isEqualTo(expectedStructure);

        verify(structureRepoMock).save(actualStructure);
    }

    @Test
    public void getStructureForUserCreatesNewStructureAndNewUsersWhenNoneIsPresent() {
        // given
        var managerId = "manager";
        var staffMemberId = "staff";

        var user = new User(USER_ID);
        var manager = new User(managerId);
        var staffMember = new User(staffMemberId);

        var expectedStructure = new OrganizationStructure(user, manager, Collections.singleton(staffMember), true);

        var idStructure = new StringStructure(user, USER_ID, managerId, Collections.singleton(staffMemberId));

        given(ldapServiceMock.userExists(USER_ID)).willReturn(true);
        given(ldapServiceMock.getIdStructure(user)).willReturn(idStructure);

        given(userRepoMock.findById(managerId)).willReturn(Optional.empty());
        given(userRepoMock.findById(staffMemberId)).willReturn(Optional.empty());

        given(userRepoMock.save(manager)).willReturn(manager);
        given(userRepoMock.save(staffMember)).willReturn(staffMember);

        given(structureRepoMock.findByUser(user)).willReturn(Optional.empty());
        given(structureRepoMock.save(expectedStructure)).willReturn(expectedStructure);

        // when
        var actualStructure = ldapUserService.getStructureForUser(user);

        // then
        assertThat(actualStructure).isEqualTo(expectedStructure);

        verify(structureRepoMock).save(actualStructure);
        verify(userRepoMock).save(manager);
        verify(userRepoMock).save(staffMember);
    }

    @Test
    public void getStructureForUserCorrectlySetsIsManagerWhenCreatingNewInstance() {
        // given
        var managerId = "manager";

        var user = new User(USER_ID);
        var manager = new User(managerId);

        var expectedStructure = new OrganizationStructure(user, manager, Collections.emptySet(), false);

        var idStructure = new StringStructure(user, USER_ID, managerId, Collections.emptySet());

        given(ldapServiceMock.userExists(USER_ID)).willReturn(true);
        given(ldapServiceMock.getIdStructure(user)).willReturn(idStructure);

        given(userRepoMock.findById(managerId)).willReturn(Optional.empty());

        given(userRepoMock.save(manager)).willReturn(manager);

        given(structureRepoMock.findByUser(user)).willReturn(Optional.empty());
        given(structureRepoMock.save(expectedStructure)).willReturn(expectedStructure);

        // when
        var actualStructure = ldapUserService.getStructureForUser(user);

        // then
        assertThat(actualStructure).isEqualTo(expectedStructure);

        verify(structureRepoMock).save(actualStructure);
        verify(userRepoMock).save(manager);
    }

    @Test
    public void getStructureForUserThrowsExceptionWhenUserDoesNotExistInAD() {
        // given
        var user = new User(USER_ID);

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
        var user = new User(USER_ID);
        var expectedUserData = new UserData(user, "First", "Last", "mail", "lob");

        given(ldapServiceMock.getUserData(Collections.singletonList(user))).willReturn(Collections.singletonList(expectedUserData));
        given(ldapServiceMock.userExists(USER_ID)).willReturn(true);

        given(userDataRepoMock.findByUser(user)).willReturn(Optional.empty());
        given(userDataRepoMock.save(expectedUserData)).willReturn(expectedUserData);

        // when
        var actualUserData = ldapUserService.getUserData(user);

        // then
        assertThat(actualUserData).isEqualTo(expectedUserData);

        verify(userDataRepoMock).save(expectedUserData);
    }

    @Test
    public void getUserDataReturnsPersistedInstanceWhenPresent() {
        // given
        var user = new User(USER_ID);
        var expectedUserData = new UserData(user, "First", "Last", "mail", "lob");

        given(userDataRepoMock.findByUser(user)).willReturn(Optional.of(expectedUserData));

        // when
        var actualUserData = ldapUserService.getUserData(user);

        // then
        assertThat(actualUserData).isEqualTo(expectedUserData);
    }

    @Test
    public void getUserDataThrowsExceptionWhenUserDoesNotExistInAD() {
        // given
        var user = new User(USER_ID);

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
        var user = new User(USER_ID);

        given(userRepoMock.findById(USER_ID)).willReturn(Optional.of(user));

        // when
        var actualUser = ldapUserService.getUserById(USER_ID);

        // then
        assertThat(actualUser).isEqualTo(user);
    }

    @Test
    public void getUserByIdCreatesNewInstanceWhenNoneIsPresentAndUserExistsInAD() {
        // given
        var expectedUser = new User(USER_ID);

        given(userRepoMock.findById(USER_ID)).willReturn(Optional.empty());
        given(ldapServiceMock.userExists(USER_ID)).willReturn(true);

        given(userRepoMock.save(expectedUser)).willReturn(expectedUser);

        // when
        var actualUser = ldapUserService.getUserById(USER_ID);

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
        var managerId = "manager";

        var user = new User(USER_ID);
        var manager = new User(managerId);

        given(structureRepoMock.existsByUserAndStaffMembersContaining(user, manager)).willReturn(true);

        // when
        boolean actualUserHasStaffMember = ldapUserService.userHasStaffMember(user, manager);

        // then
        assertThat(actualUserHasStaffMember).isTrue();
    }

    @Test
    public void userHasStaffMemberReturnsFalseWhenNotExistsByUserAndStaffMemberContaining() {
        // given
        var managerId = "manager";

        var user = new User(USER_ID);
        var manager = new User(managerId);

        given(structureRepoMock.existsByUserAndStaffMembersContaining(user, manager)).willReturn(false);

        // when
        boolean actualUserHasStaffMember = ldapUserService.userHasStaffMember(user, manager);

        // then
        assertThat(actualUserHasStaffMember).isFalse();
    }

    @Test
    public void getManagerOfUserReturnsManagerOfPersistedInstanceWhenPresent() {
        // given
        var managerId = "manager";

        var user = new User(USER_ID);
        var expectedManager = new User(managerId);

        var structure = new OrganizationStructure(user, expectedManager, Collections.emptySet(), false);

        given(structureRepoMock.findByUser(user)).willReturn(Optional.of(structure));

        // when
        var actualManager = ldapUserService.getManagerOfUser(user);

        // then
        assertThat(actualManager).isEqualTo(expectedManager);
    }

    @Test
    public void getManagerOfUserReturnsManagerWhenNoneIsPresent() {
        // given
        var managerId = "manager";
        var user = new User(USER_ID);
        var expectedManager = new User(managerId);

        given(structureRepoMock.findByUser(user)).willReturn(Optional.empty());

        given(ldapServiceMock.getManagerId(user)).willReturn(managerId);
        given(ldapServiceMock.userExists(USER_ID)).willReturn(true);

        given(userRepoMock.findById(managerId)).willReturn(Optional.of(expectedManager));

        // when
        var actualManager = ldapUserService.getManagerOfUser(user);

        // then
        assertThat(actualManager).isEqualTo(expectedManager);
    }

    @Test
    public void getStaffMemberDataOfUser() {
        // given
        var sort = Sort.unsorted();

        var managerId = "manager";
        var staffMemberWithDataId = "staff-1";
        var staffMemberWithoutDataId = "staff-2";

        var user = new User(USER_ID);
        var manager = new User(managerId);
        var staffMemberWithData = new User(staffMemberWithDataId);
        var staffMemberWithoutData = new User(staffMemberWithoutDataId);

        var staffMembers = Set.of(staffMemberWithData, staffMemberWithoutData);
        var structure = new OrganizationStructure(user, manager, staffMembers, true);

        var staffMemberWithDataData = new UserData(staffMemberWithData, "First", "Last", "email", "lob");
        var staffMemberWithoutDataData = new UserData(staffMemberWithoutData, "First", "Last", "email", "lob");

        given(structureRepoMock.findByUser(user)).willReturn(Optional.of(structure));

        given(userDataRepoMock.existsByUser(staffMemberWithData)).willReturn(true);
        given(userDataRepoMock.existsByUser(staffMemberWithoutData)).willReturn(false);

        given(userDataRepoMock.findByUserIn(staffMembers, sort))
                .willReturn(Arrays.asList(staffMemberWithDataData, staffMemberWithoutDataData));

        given(ldapServiceMock.getUserData(Collections.singletonList(staffMemberWithoutData)))
                .willReturn(Collections.singletonList(staffMemberWithoutDataData));

        // when
        var actualStaffData = ldapUserService.getStaffMemberUserDataOfUser(user, sort);

        // then
        assertThat(actualStaffData).containsExactlyInAnyOrder(staffMemberWithDataData, staffMemberWithoutDataData);

        verify(ldapServiceMock).getUserData(userListCaptor.capture());
        assertThat(userListCaptor.getValue()).containsExactlyInAnyOrder(staffMemberWithoutData);

        verify(userDataRepoMock).saveAll(Collections.singletonList(staffMemberWithoutDataData));
    }

    @Test
    public void getStaffMembersOfUserReturnsStaffMembersOfPersistedStructureInstanceWhenPresent() {
        // given
        var user = new User(USER_ID);
        var manager = new User("manager");
        var firstStaffMember = new User("staff-1");
        var secondStaffMember = new User("staff-2");
        var expectedStaffMembers = Set.of(firstStaffMember, secondStaffMember);
        var structure = new OrganizationStructure(user, manager, expectedStaffMembers, true);

        given(structureRepoMock.findByUser(user)).willReturn(Optional.of(structure));

        // when
        var actualStaffMembers = ldapUserService.getStaffMembersOfUser(user);

        // then
        assertThat(actualStaffMembers).containsExactlyElementsOf(expectedStaffMembers);
    }

    @Test
    public void getStaffMembersOfUserReturnsStaffMembersOfNewlyCreatedStructureInstanceWhenNotPresent() {
        // given
        var managerId = "manager";
        var firstStaffMemberId = "staff-1";
        var secondStaffMemberId = "staff-2";
        var staffMemberIds = Set.of(firstStaffMemberId, secondStaffMemberId);

        var manager = new User(managerId);
        var user = new User(USER_ID);
        var firstStaffMember = new User(firstStaffMemberId);
        var secondStaffMember = new User(secondStaffMemberId);

        var stringStructure = new StringStructure(user, USER_ID, managerId, staffMemberIds);

        var expectedStructure = new OrganizationStructure(user, manager, Set.of(firstStaffMember, secondStaffMember), true);

        given(userRepoMock.findById(managerId)).willReturn(Optional.of(manager));
        given(userRepoMock.findById(firstStaffMemberId)).willReturn(Optional.of(firstStaffMember));
        given(userRepoMock.findById(secondStaffMemberId)).willReturn(Optional.of(secondStaffMember));

        given(structureRepoMock.findByUser(user)).willReturn(Optional.empty());
        given(structureRepoMock.save(expectedStructure)).willReturn(expectedStructure);

        given(ldapServiceMock.userExists(USER_ID)).willReturn(true);
        given(ldapServiceMock.getIdStructure(user)).willReturn(stringStructure);

        // when
        var actualStaffMembers = ldapUserService.getStaffMembersOfUser(user);

        // then
        assertThat(actualStaffMembers).containsExactlyInAnyOrder(firstStaffMember, secondStaffMember);
    }

    @Test
    public void save() {
        // given
        var user = new User(USER_ID);

        given(userRepoMock.save(user)).willReturn(user);

        // when
        var actualUser = ldapUserService.save(user);

        // then
        assertThat(actualUser).isEqualTo(user);

        verify(userRepoMock).save(user);
    }

    @Test
    public void usersAreManagers() {
        // given
        var managerId = "manager";
        var userWithExistingStructId = "user-1";
        var userWithoutExistingStructId = "user-2";

        var manager = new User(managerId);
        var userWithExistingStruct = new User(userWithExistingStructId);
        var userWithoutExistingStruct = new User(userWithoutExistingStructId);

        var structureForUserWithExistingStruct
                = new OrganizationStructure(userWithExistingStruct, manager, Collections.emptySet(), true);

        Set<User> users = new HashSet<>(Set.of(userWithExistingStruct, userWithoutExistingStruct));

        given(structureRepoMock.findAllByUserIn(users)).willReturn(Collections.singletonList(structureForUserWithExistingStruct));

        given(ldapServiceMock.isManager(userWithoutExistingStructId)).willReturn(false);

        // when
        var actualUserManagerMap = ldapUserService.usersAreManagers(users);

        // then
        assertThat(actualUserManagerMap).containsOnly(entry(userWithExistingStruct, true), entry(userWithoutExistingStruct, false));

        verify(ldapServiceMock).isManager(userWithoutExistingStructId);
    }

    @Test
    public void validateExistenceReturnsUserWhenUserExistsInAD() {
        // given
        var expectedUser = new User(USER_ID);

        given(ldapServiceMock.userExists(USER_ID)).willReturn(true);

        // when
        var actualUser = ldapUserService.validateExistence(expectedUser);

        // then
        assertThat(actualUser).isEqualTo(expectedUser);
    }

    @Test
    public void validateExistenceThrowsExceptionWhenUserDoesNotExistInAD() {
        // given
        var user = new User(USER_ID);

        given(ldapServiceMock.userExists(USER_ID)).willReturn(false);

        // when
        assertThatThrownBy(() -> ldapUserService.validateExistence(user))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("No user with the given ID exists in the AD!");
    }

}