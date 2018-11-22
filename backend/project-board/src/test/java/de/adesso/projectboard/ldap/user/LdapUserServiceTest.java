package de.adesso.projectboard.ldap.user;

import de.adesso.projectboard.base.exceptions.UserNotFoundException;
import de.adesso.projectboard.base.security.AuthenticationInfo;
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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.data.domain.Sort;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class LdapUserServiceTest {

    @Mock
    UserRepository userRepo;

    @Mock
    UserDataRepository userDataRepo;

    @Mock
    OrganizationStructureRepository orgStructRepo;

    @Mock
    LdapService ldapService;

    @Mock
    AuthenticationInfo authInfo;

    @InjectMocks
    LdapUserService ldapUserService;

    @Mock
    User user;

    @Mock
    OrganizationStructure orgStruct;

    @Mock
    UserData userData;

    @Before
    public void setUp() {
        // set up save methods of every repo to return the passed instance
        when(userRepo.save(any(User.class))).thenAnswer((Answer<User>) invocation -> {
            Object[] args = invocation.getArguments();

            return (User) args[0];
        });
        when(userDataRepo.save(any(UserData.class))).thenAnswer((Answer<UserData>) invocation -> {
            Object[] args = invocation.getArguments();

            return (UserData) args[0];
        });
        when(orgStructRepo.save(any(OrganizationStructure.class))).thenAnswer((Answer<OrganizationStructure>) invocation -> {
           Object[] args = invocation.getArguments();

           return (OrganizationStructure) args[0];
        });

        // set up user repo mock to "contain" the mock user
        when(userRepo.findById("user")).thenReturn(Optional.of(user));
        when(userRepo.existsById("user")).thenReturn(true);

        // set up mock objects
        when(user.getId()).thenReturn("user");
    }

    @Test
    public void testGetAuthenticatedUser() {
        // set up mock, repo mocks already set up in setUp method
        when(authInfo.getUserId()).thenReturn("user");

        User returnedUser = ldapUserService.getAuthenticatedUser();

        assertEquals(user, returnedUser);
    }

    @Test
    public void testGetAuthenticatedUserId() {
        // set up mock
        when(authInfo.getUserId()).thenReturn("user-id");

        assertEquals("user-id", ldapUserService.getAuthenticatedUserId());
    }

    @Test
    public void testUserExists_ExistsInRepo() {
        // mock already set up in setUp method

        assertTrue(ldapUserService.userExists("user"));
    }

    @Test
    public void testUserExists_DoesNotExistInRepo() {
        // set up service mock
        when(ldapService.userExists("not-in-repo")).thenReturn(true);

        assertTrue(ldapUserService.userExists("not-in-repo"));
    }

    @Test
    public void testUserExist_DoesNotExist() {
        assertFalse(ldapUserService.userExists("non-existent-user"));
    }

    @Test
    public void testUserIsManager_Cached_IsManager() {
        // set up repo mock
        when(orgStructRepo.existsByUser(user)).thenReturn(true);
        when(orgStructRepo.existsByUserAndUserIsManager(user, true)).thenReturn(true);

        assertTrue(ldapUserService.userIsManager(user));
        verify(ldapService, never()).isManager("user");
    }

    @Test
    public void testUserIsManager_Cached_NoManager() {
        // set up repo mock
        when(orgStructRepo.existsByUser(user)).thenReturn(true);
        when(orgStructRepo.existsByUserAndUserIsManager(user, true)).thenReturn(false);

        assertFalse(ldapUserService.userIsManager(user));
        verify(ldapService, never()).isManager("user");
    }

    @Test
    public void testUserIsManager_NotCached_IsManager() {
        // set up service mock
        when(ldapService.isManager("user")).thenReturn(true);

        assertTrue(ldapUserService.userIsManager(user));
        verify(ldapService).isManager("user");
    }

    @Test
    public void testUserIsManager_NotCached_NoManager() {
        // set up service mocks
        when(ldapService.isManager("user")).thenReturn(false);

        assertFalse(ldapUserService.userIsManager(user));
        verify(ldapService).isManager("user");
    }

    @Test
    public void testGetStructureForUser_Cached() {
        // set up repo mock
        when(orgStructRepo.findByUser(user)).thenReturn(Optional.of(orgStruct));

        OrganizationStructure returnedStruct = ldapUserService.getStructureForUser(user);

        assertEquals(orgStruct, returnedStruct);
        verify(ldapService, never()).getIdStructure(user);
    }

    @Test
    public void testGetStructureForUser_NotCached() {
        // create new mock for ldap service
        StringStructure idStructure = mock(StringStructure.class);
        when(idStructure.getManager()).thenReturn("manager");
        when(idStructure.getStaffMembers()).thenReturn(Collections.singleton("staff"));

        // create new mock users
        User staffMember = mock(User.class);
        User manager = mock(User.class);

        // set up repo/service mock
        when(orgStructRepo.findByUser(user)).thenReturn(Optional.empty());
        when(ldapService.getIdStructure(user)).thenReturn(idStructure);
        when(userRepo.findById("manager")).thenReturn(Optional.of(manager));
        when(userRepo.findById("staff")).thenReturn(Optional.of(staffMember));

        OrganizationStructure structureForUser = ldapUserService.getStructureForUser(user);

        assertEquals(user, structureForUser.getUser());
        assertEquals(manager, structureForUser.getManager());
        assertEquals(1, structureForUser.getStaffMembers().size());
        assertTrue(structureForUser.getStaffMembers().contains(staffMember));
        assertTrue(structureForUser.isUserIsManager());

        verify(orgStructRepo).save(any());
    }

    @Test
    public void testGetUserData_Cached() {
        // set up repo mock
        when(userDataRepo.findByUser(user)).thenReturn(Optional.of(userData));

        UserData returnedData = ldapUserService.getUserData(user);

        assertEquals(userData, returnedData);
        verify(ldapService, never()).getUserData(any());
    }

    @Test
    public void testGetUserData_NotCached() {
        // set up repo/service mock
        when(userDataRepo.findByUser(user)).thenReturn(Optional.empty());
        when(ldapService.getUserData(anyList())).thenReturn(Collections.singletonList(userData));

        UserData returnedData = ldapUserService.getUserData(user);

        assertEquals(userData, returnedData);
        verify(userDataRepo).save(any());
    }

    @Test
    public void testGetUserById_ExistsInRepo() {
        // mocks already set up in setUp method

        User returnedUser = ldapUserService.getUserById("user");

        assertEquals(user, returnedUser);
        verify(ldapService, never()).userExists(anyString());
    }

    @Test
    public void testGetUserById_DoesNotExistInRepo() {
        // set up service mock
        when(ldapService.userExists("new-user")).thenReturn(true);

        User returnedUser = ldapUserService.getUserById("new-user");

        assertEquals("new-user", returnedUser.getId());
        verify(userRepo).save(any());
    }

    @Test(expected = UserNotFoundException.class)
    public void testGetUserById_DoesNotExist() {
        ldapUserService.getUserById("non-existent-user");
    }

    @Test
    public void testUserHasStaffMember_Contains() {
        // create new entity mock
        User staffMember = mock(User.class);

        // set up repo/entity mock
        when(orgStructRepo.findByUser(user)).thenReturn(Optional.of(orgStruct));
        when(orgStruct.getStaffMembers()).thenReturn(Collections.singleton(staffMember));

        assertTrue(ldapUserService.userHasStaffMember(user, staffMember));
    }

    @Test
    public void testUserHasStaffMember_DoesNotContain() {
        // create new entity mock
        User staffMember = mock(User.class);

        // set up repo/entity mock
        when(orgStructRepo.findByUser(user)).thenReturn(Optional.of(orgStruct));
        when(orgStruct.getStaffMembers()).thenReturn(Collections.emptySet());

        assertFalse(ldapUserService.userHasStaffMember(user, staffMember));
    }

    @Test
    public void testGetManagerOfUser_Cached() {
        // create new entity mock
        User manager = mock(User.class);

        // set up repo/entity mock
        when(orgStructRepo.findByUser(user)).thenReturn(Optional.of(orgStruct));
        when(orgStruct.getManager()).thenReturn(manager);

        User returnedManager = ldapUserService.getManagerOfUser(user);

        assertEquals(manager, returnedManager);
        verify(ldapService, never()).getManagerId(user);
    }

    @Test
    public void testGetManagerOfUser_NotCached_ManagerExists() {
        // create new entity mock
        User manager = mock(User.class);

        // set up repo/service mock
        when(orgStructRepo.findByUser(user)).thenReturn(Optional.empty());
        when(ldapService.getManagerId(user)).thenReturn("manager");
        when(userRepo.findById("manager")).thenReturn(Optional.of(manager));

        User returnedManager = ldapUserService.getManagerOfUser(user);

        assertEquals(manager, returnedManager);
        verify(userRepo, never()).save(any());
    }

    @Test
    public void testGetManagerOfUser_NotCached_MangerDoesNotExist() {
        // set up repo/service mock
        when(orgStructRepo.findByUser(user)).thenReturn(Optional.empty());
        when(ldapService.getManagerId(user)).thenReturn("manager");
        when(ldapService.userExists("manager")).thenReturn(true);
        when(userRepo.findById("manager")).thenReturn(Optional.empty());

        User returnedManager = ldapUserService.getManagerOfUser(user);

        assertEquals("manager", returnedManager.getId());
        verify(userRepo).save(any());
    }

    @Test
    public void testGetStaffMemberDataOfUser() {
        // set up new entity mocks
        User firstStaffMember = mock(User.class);
        User secondStaffMember = mock(User.class);
        UserData secondUserData = mock(UserData.class);

        Sort sorting = mock(Sort.class);

        // set up repo/service mock
        when(orgStructRepo.findByUser(user)).thenReturn(Optional.of(orgStruct));
        when(orgStruct.getStaffMembers()).thenReturn(new HashSet<>(Arrays.asList(firstStaffMember, secondStaffMember)));
        when(userDataRepo.existsByUser(firstStaffMember)).thenReturn(true);
        when(ldapService.getUserData(anyList())).thenReturn(Collections.singletonList(secondUserData));

        ldapUserService.getStaffMemberDataOfUser(user, sorting);

        verify(userDataRepo).saveAll(anyList());
    }

    @Test
    public void testSave() {
        User returnedUser = ldapUserService.save(user);

        assertEquals(user, returnedUser);
        verify(userRepo).save(user);
    }

    @Test
    public void testUsersAreManagers() {
        // create new entity mocks
        User cachedManager = mock(User.class);
        User nonCachedManager = mock(User.class);
        User nonCachedUser = mock(User.class);
        OrganizationStructure cachedManagerStructure = mock(OrganizationStructure.class);

        // set up new entity mocks
        when(nonCachedManager.getId()).thenReturn("non-cached-manager");
        when(nonCachedUser.getId()).thenReturn("non-cached-user");

        when(cachedManagerStructure.isUserIsManager()).thenReturn(true);
        when(cachedManagerStructure.getUser()).thenReturn(cachedManager);

        // set up service/repo mocks
        when(orgStructRepo.findAllByUserIn(any())).thenReturn(Arrays.asList(cachedManagerStructure));
        when(ldapService.isManager("non-cached-manager")).thenReturn(true);
        when(ldapService.isManager("non-cached-user")).thenReturn(false);

        // call tested method
        Set<User> userSet = Stream.of(cachedManager, nonCachedManager, nonCachedUser).collect(Collectors.toSet());
        Map<User, Boolean> userManagerMap = ldapUserService.usersAreManagers(userSet);

        assertEquals(3, userManagerMap.entrySet().size());
        assertTrue(userManagerMap.get(cachedManager));
        assertTrue(userManagerMap.get(nonCachedManager));
        assertFalse(userManagerMap.get(nonCachedUser));
    }

}