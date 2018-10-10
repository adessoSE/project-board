package de.adesso.projectboard.core.base.rest.user.service;

import de.adesso.projectboard.core.base.rest.exceptions.UserNotFoundException;
import de.adesso.projectboard.core.base.rest.security.AuthenticationInfo;
import de.adesso.projectboard.core.base.rest.user.persistence.SuperUser;
import de.adesso.projectboard.core.base.rest.user.persistence.User;
import de.adesso.projectboard.core.base.rest.user.persistence.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticationInfo authInfo;

    @InjectMocks
    private UserService userService;

    private SuperUser superUser;

    private SuperUser otherSuperUser;

    private User user;

    private User otherUser;

    @Before
    public void setUp() {
        // data setup
        setUpMockData();

        // mock setup
        when(authInfo.getUserId()).thenReturn(user.getId());

        when(userRepository.findById(anyString())).thenReturn(Optional.empty());
        when(userRepository.findById(eq(user.getId()))).thenReturn(Optional.of(user));

        when(userRepository.existsById(anyString())).thenReturn(false);
        when(userRepository.existsById(eq(user.getId()))).thenReturn(true);

        when(userRepository.findAll()).thenReturn(Arrays.asList(superUser, user));

        // just return the passed argument
        when(userRepository.save(any(User.class))).thenAnswer((Answer<User>) invocation -> {
            Object[] args = invocation.getArguments();
            return (User) args[0];
        });

        // just return superuser's staff members in a list
        when(userRepository.findAllByBossEquals(any(SuperUser.class), any(Sort.class)))
                .thenAnswer((Answer<List<User>>) invocation -> {
                    Object[] args = invocation.getArguments();
                    SuperUser superUser = (SuperUser) args[0];

                    return new ArrayList<>(superUser.getStaffMembers());
                });

        when(userRepository.existsByIdAndBoss(anyString(), any(SuperUser.class))).thenReturn(false);
        when(userRepository.existsByIdAndBoss(eq(otherUser.getId()), eq(otherSuperUser))).thenReturn(true);
        when(userRepository.existsByIdAndBoss(eq(user.getId()), eq(superUser))).thenReturn(true);
    }

    @Test
    public void testGetCurrentUser() {
        User currentUser = userService.getCurrentUser();

        assertEquals(user, currentUser);
    }

    @Test
    public void testGetCurrentUserId() {
        String currentUserId = userService.getCurrentUserId();

        verify(authInfo).getUserId();
        assertEquals(user.getId(), currentUserId);
    }

    @Test
    public void testUserExists() {
        assertTrue(userService.userExists(user.getId()));
        assertFalse(userService.userExists("non-existent-user-id"));
    }

    @Test
    public void testGetAllUsers() {
        ArrayList<User> allUsers = new ArrayList<>();
        userService.getAllUsers().forEach(allUsers::add);

        assertEquals(2L, allUsers.size());
        assertTrue(allUsers.contains(superUser));
        assertTrue(allUsers.contains(user));
    }

    @Test
    public void testGetUserById_OK() {
        User retrievedUser = userService.getUserById(user.getId());

        assertEquals(user, retrievedUser);
    }

    @Test(expected = UserNotFoundException.class)
    public void testGetUserById_NotExisting() {
        userService.getUserById("non-existent-user-id");
    }

    @Test
    public void testUserHasStaffMember() {
        assertTrue(userService.userHasStaffMember(superUser, user.getId()));
        assertTrue(userService.userHasStaffMember(otherSuperUser, otherUser.getId()));

        assertFalse(userService.userHasStaffMember(superUser, otherUser.getId()));
        assertFalse(userService.userHasStaffMember(otherSuperUser, user.getId()));
    }

    @Test
    public void testSave() {
        User savedUser = userService.save(user);

        verify(userRepository).save(user);
        assertEquals(user, savedUser);
    }

    @Test
    public void testDelete_User() {
        userService.delete(superUser);

        verify(userRepository).delete(superUser);
        assertFalse(superUser.getStaffMembers().contains(superUser));
    }

    @Test
    public void testDelete_SuperUser() {
        userService.delete(user);

        verify(userRepository).save(superUser);
        verify(userRepository).delete(user);
        assertFalse(superUser.getStaffMembers().contains(user));
    }

    @Test
    public void testGetStaffMembersOfUser() {
        Sort sort = Sort.unsorted();

        List<User> userStaff = userService.getStaffMembersOfUser(user, sort);
        assertEquals(0L, userStaff.size());

        List<User> superUserStaff = userService.getStaffMembersOfUser(superUser, Sort.unsorted());
        assertEquals(2L, superUserStaff.size());
        assertTrue(superUserStaff.contains(user));
        assertTrue(superUserStaff.contains(superUser));
    }

    private void setUpMockData() {
        this.superUser = new SuperUser("first-super-user");
        this.superUser.setFullName("First Super Test", "User");
        this.superUser.setEmail("first-super-test-user@test.com");
        this.superUser.setLob("LOB Test");

        this.user = new User("first-user", superUser);
        this.user.setFullName("First Test", "User");
        this.user.setEmail("first-test-user@test.com");
        this.user.setLob("LOB Test");

        this.otherSuperUser = new SuperUser("second-super-user");
        this.otherSuperUser.setFullName("Second Super Test", "User");
        this.otherSuperUser.setEmail("second-super-test-user@test.com");
        this.otherSuperUser.setLob("LOB Test");

        this.otherUser = new User("second-user", otherSuperUser);
        this.otherUser.setFullName("First Test", "User");
        this.otherUser.setEmail("first-test-user@test.com");
        this.otherUser.setLob("LOB Test");
    }

}