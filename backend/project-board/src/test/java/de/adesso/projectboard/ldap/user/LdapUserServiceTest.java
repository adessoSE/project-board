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
    UserRepository userRepo;

    @Mock
    UserDataRepository userDataRepo;

    @Mock
    OrganizationStructureRepository structureRepo;

    @Mock
    LdapService ldapService;

    @Mock
    AuthenticationInfoRetriever authInfoRetriever;

    @Captor
    ArgumentCaptor<List<User>> userListCaptor;

    LdapUserService ldapUserService;

    @Before
    public void setUp() {
        this.ldapUserService = new LdapUserService(userRepo, userDataRepo, ldapService, structureRepo, authInfoRetriever);
    }

    @Test
    public void getAuthenticatedUserReturnsUserWhenUserExists() {
        // given
        User expectedUser = new User(USER_ID);

        given(authInfoRetriever.getUserId()).willReturn(USER_ID);
        given(userRepo.findById(USER_ID)).willReturn(Optional.of(expectedUser));

        // when
        User actualUser = ldapUserService.getAuthenticatedUser();

        // then
        assertThat(actualUser).isEqualTo(expectedUser);
    }

    @Test
    public void getAuthenticatedUserThrowsExceptionWhenUserDoesNotExist() {
        // given
        given(authInfoRetriever.getUserId()).willReturn(USER_ID);
        given(userRepo.findById(USER_ID)).willReturn(Optional.empty());

        // when
        assertThatThrownBy(() -> ldapUserService.getAuthenticatedUser())
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    public void getAuthenticatedUserIdReturnsExpectedId() {
        // given
        given(authInfoRetriever.getUserId()).willReturn(USER_ID);

        // when
        String actualUserId = ldapUserService.getAuthenticatedUserId();

        // then
        assertThat(actualUserId).isEqualTo(USER_ID);
    }

    @Test
    public void userExistsReturnsTrueWhenUserExistsInRepo() {
        // given
        given(userRepo.existsById(USER_ID)).willReturn(true);

        // when
        boolean actualExists = ldapUserService.userExists(USER_ID);

        // then
        assertThat(actualExists).isTrue();
    }

    @Test
    public void userExistsReturnsFalseWhenUserDoesNotExistInRepo() {
        // given
        given(userRepo.existsById(USER_ID)).willReturn(false);

        // when
        boolean actualExists = ldapUserService.userExists(USER_ID);

        // then
        assertThat(actualExists).isFalse();
    }

    @Test
    public void userIsManagerReturnsTrueWhenManagerFieldOfExistingStructureForUserIsTrue() {
        // given
        User user = new User(USER_ID);

        given(structureRepo.existsByUser(user)).willReturn(true);
        given(structureRepo.existsByUserAndUserIsManager(user, true)).willReturn(true);

        // when
        boolean actualIsManager = ldapUserService.userIsManager(user);

        // then
        assertThat(actualIsManager).isTrue();
    }

    @Test
    public void userIsManagerReturnsFalseWhenManagerFieldOfExistingStructureForUserIsFalse() {
        // given
        User user = new User(USER_ID);

        given(structureRepo.existsByUser(user)).willReturn(true);
        given(structureRepo.existsByUserAndUserIsManager(user, true)).willReturn(false);

        // when
        boolean actualIsManager = ldapUserService.userIsManager(user);

        // then
        assertThat(actualIsManager).isFalse();
    }

    @Test
    public void userIsManagerReturnsTrueWhenLdapServiceReturnsTrue() {
        // given
        User user = new User(USER_ID);

        given(structureRepo.existsByUser(user)).willReturn(false);
        given(ldapService.isManager(USER_ID)).willReturn(true);

        // when
        boolean actualIsManager = ldapUserService.userIsManager(user);

        // then
        assertThat(actualIsManager).isTrue();
    }

    @Test
    public void userIsManagerReturnsFalseWhenLdapServiceReturnsFalse() {
        // given
        User user = new User(USER_ID);

        given(structureRepo.existsByUser(user)).willReturn(false);
        given(ldapService.isManager(USER_ID)).willReturn(false);

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

        given(structureRepo.findByUser(user)).willReturn(Optional.of(expectedStructure));

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

        given(structureRepo.findByUser(user)).willReturn(Optional.empty());
        given(ldapService.userExists(USER_ID)).willReturn(true);
        given(ldapService.getIdStructure(user)).willReturn(idStructure);

        given(userRepo.findById(managerId)).willReturn(Optional.of(managerUser));
        given(userRepo.findById(staffMemberId)).willReturn(Optional.of(staffUser));

        given(structureRepo.save(expectedStructure)).willReturn(expectedStructure);

        // when
        OrganizationStructure actualStructure = ldapUserService.getStructureForUser(user);

        // then
        assertThat(actualStructure).isEqualTo(expectedStructure);

        verify(structureRepo).save(actualStructure);
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

        given(ldapService.userExists(USER_ID)).willReturn(true);
        given(ldapService.getIdStructure(user)).willReturn(idStructure);

        given(userRepo.findById(managerId)).willReturn(Optional.empty());
        given(userRepo.findById(staffMemberId)).willReturn(Optional.empty());

        given(userRepo.save(manager)).willReturn(manager);
        given(userRepo.save(staffMember)).willReturn(staffMember);

        given(structureRepo.findByUser(user)).willReturn(Optional.empty());
        given(structureRepo.save(expectedStructure)).willReturn(expectedStructure);

        // when
        OrganizationStructure actualStructure = ldapUserService.getStructureForUser(user);

        // then
        assertThat(actualStructure).isEqualTo(expectedStructure);

        verify(structureRepo).save(actualStructure);
        verify(userRepo).save(manager);
        verify(userRepo).save(staffMember);
    }

    @Test
    public void getStructureForUserCorrectlySetsIsManagerWhenCreatingNewInstance() {
        // given
        String managerId = "manager";

        User user = new User(USER_ID);
        User manager = new User(managerId);

        OrganizationStructure expectedStructure = new OrganizationStructure(user, manager, Collections.emptySet(), false);

        StringStructure idStructure = new StringStructure(user, USER_ID, managerId, Collections.emptySet());

        given(ldapService.userExists(USER_ID)).willReturn(true);
        given(ldapService.getIdStructure(user)).willReturn(idStructure);

        given(userRepo.findById(managerId)).willReturn(Optional.empty());

        given(userRepo.save(manager)).willReturn(manager);

        given(structureRepo.findByUser(user)).willReturn(Optional.empty());
        given(structureRepo.save(expectedStructure)).willReturn(expectedStructure);

        // when
        OrganizationStructure actualStructure = ldapUserService.getStructureForUser(user);

        // then
        assertThat(actualStructure).isEqualTo(expectedStructure);

        verify(structureRepo).save(actualStructure);
        verify(userRepo).save(manager);
    }

    @Test
    public void getStructureForUserThrowsExceptionWhenUserDoesNotExistInAD() {
        // given
        User user = new User(USER_ID);

        given(structureRepo.findByUser(user)).willReturn(Optional.empty());
        given(ldapService.userExists(USER_ID)).willReturn(false);

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

        given(ldapService.getUserData(Collections.singletonList(user))).willReturn(Collections.singletonList(expectedUserData));
        given(ldapService.userExists(USER_ID)).willReturn(true);

        given(userDataRepo.findByUser(user)).willReturn(Optional.empty());
        given(userDataRepo.save(expectedUserData)).willReturn(expectedUserData);

        // when
        UserData actualUserData = ldapUserService.getUserData(user);

        // then
        assertThat(actualUserData).isEqualTo(expectedUserData);

        verify(userDataRepo).save(expectedUserData);
    }

    @Test
    public void getUserDataReturnsPersistedInstanceWhenPresent() {
        // given
        User user = new User(USER_ID);
        UserData expectedUserData = new UserData(user, "First", "Last", "mail", "lob");

        given(userDataRepo.findByUser(user)).willReturn(Optional.of(expectedUserData));

        // when
        UserData actualUserData = ldapUserService.getUserData(user);

        // then
        assertThat(actualUserData).isEqualTo(expectedUserData);
    }

    @Test
    public void getUserDataThrowsExceptionWhenUserDoesNotExistInAD() {
        // given
        User user = new User(USER_ID);

        given(userDataRepo.findByUser(user)).willReturn(Optional.empty());
        given(ldapService.userExists(USER_ID)).willReturn(false);

        // when
        assertThatThrownBy(() -> ldapUserService.getUserData(user))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("No user with the given ID exists in the AD!");
    }

    @Test
    public void getUserByIdReturnsUserWhenUserExists() {
        // given
        User user = new User(USER_ID);

        given(userRepo.findById(USER_ID)).willReturn(Optional.of(user));

        // when
        User actualUser = ldapUserService.getUserById(USER_ID);

        // then
        assertThat(actualUser).isEqualTo(user);
    }

    @Test
    public void getUserByIdCreatesNewInstanceWhenNoneIsPresentAndUserExistsInAD() {
        // given
        User expectedUser = new User(USER_ID);

        given(userRepo.findById(USER_ID)).willReturn(Optional.empty());
        given(ldapService.userExists(USER_ID)).willReturn(true);

        given(userRepo.save(expectedUser)).willReturn(expectedUser);

        // when
        User actualUser = ldapUserService.getUserById(USER_ID);

        // then
        assertThat(actualUser).isEqualTo(expectedUser);
    }

    @Test
    public void getUserByIdThrowsExceptionWhenNoneIsPresentAndUserDoesNotExistInAD() {
        // given
        given(userRepo.findById(USER_ID)).willReturn(Optional.empty());
        given(ldapService.userExists(USER_ID)).willReturn(false);

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

        given(structureRepo.existsByUserAndStaffMembersContaining(user, manager)).willReturn(true);

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

        given(structureRepo.existsByUserAndStaffMembersContaining(user, manager)).willReturn(false);

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

        given(structureRepo.findByUser(user)).willReturn(Optional.of(structure));

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

        given(structureRepo.findByUser(user)).willReturn(Optional.empty());

        given(ldapService.getManagerId(user)).willReturn(managerId);
        given(ldapService.userExists(USER_ID)).willReturn(true);

        given(userRepo.findById(managerId)).willReturn(Optional.of(expectedManager));

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

        given(structureRepo.findByUser(user)).willReturn(Optional.of(structure));

        given(userDataRepo.existsByUser(staffMemberWithData)).willReturn(true);
        given(userDataRepo.existsByUser(staffMemberWithoutData)).willReturn(false);

        given(userDataRepo.findByUserIn(staffMembers, sort))
                .willReturn(Arrays.asList(staffMemberWithDataData, staffMemberWithoutDataData));

        given(ldapService.getUserData(Collections.singletonList(staffMemberWithoutData)))
                .willReturn(Collections.singletonList(staffMemberWithoutDataData));

        // when
        List<UserData> actualStaffData = ldapUserService.getStaffMemberDataOfUser(user, sort);

        // then
        assertThat(actualStaffData).containsExactlyInAnyOrder(staffMemberWithDataData, staffMemberWithoutDataData);

        verify(ldapService).getUserData(userListCaptor.capture());
        assertThat(userListCaptor.getValue()).containsExactlyInAnyOrder(staffMemberWithoutData);

        verify(userDataRepo).saveAll(Collections.singletonList(staffMemberWithoutDataData));
    }

    @Test
    public void save() {
        // given
        User user = new User(USER_ID);

        given(userRepo.save(user)).willReturn(user);

        // when
        User actualUser = ldapUserService.save(user);

        // then
        assertThat(actualUser).isEqualTo(user);

        verify(userRepo).save(user);
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

        given(structureRepo.findAllByUserIn(users)).willReturn(Collections.singletonList(structureForUserWithExistingStruct));

        given(ldapService.isManager(userWithoutExistingStructId)).willReturn(false);

        // when
        Map<User, Boolean> actualUserManagerMap = ldapUserService.usersAreManagers(users);

        // then
        assertThat(actualUserManagerMap).containsOnly(entry(userWithExistingStruct, true), entry(userWithoutExistingStruct, false));

        verify(ldapService).isManager(userWithoutExistingStructId);
    }

    @Test
    public void validateExistenceReturnsUserWhenUserExistsInAD() {
        // given
        User expectedUser = new User(USER_ID);

        given(ldapService.userExists(USER_ID)).willReturn(true);

        // when
        User actualUser = ldapUserService.validateExistence(expectedUser);

        // then
        assertThat(actualUser).isEqualTo(expectedUser);
    }

    @Test
    public void validateExistenceThrowsExceptionWhenUserDoesNotExistInAD() {
        // given
        User user = new User(USER_ID);

        given(ldapService.userExists(USER_ID)).willReturn(false);

        // when
        assertThatThrownBy(() -> ldapUserService.validateExistence(user))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("No user with the given ID exists in the AD!");
    }

}