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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
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

    private User user;

    @Before
    public void setUp() {
        // data setup
        this.superUser = new SuperUser("first-super-user");
        this.superUser.setFullName("First Super Test", "User");
        this.superUser.setEmail("first-super-test-user@test.com");
        this.superUser.setLob("LOB Test");

        this.user = new User("first-user", superUser);
        this.user.setFullName("First Test", "User");
        this.user.setEmail("first-test-user@test.com");
        this.user.setLob("LOB Test");

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
    }

    @Test
    public void testGetCurrentUser() {
        User currentUser = userService.getCurrentUser();

        assertEquals(user, currentUser);
    }

    @Test
    public void testGetUserById_OK() {
        User user = userService.getUserById("first-user");

        assertEquals("first-user", user.getId());
    }

    @Test
    public void testSave() {
        User savedUser = userService.save(user);

        assertEquals(user, savedUser);
        verify(userRepository).save(user);
    }

    @Test(expected = UserNotFoundException.class)
    public void testGetUserById_NotExisting() {
        userService.getUserById("non-existent-user-id");
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

}