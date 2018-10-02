package de.adesso.projectboard.core.base.rest.user.service;

import de.adesso.projectboard.core.base.rest.exceptions.UserNotFoundException;
import de.adesso.projectboard.core.base.rest.security.AuthenticationInfo;
import de.adesso.projectboard.core.base.rest.user.persistence.SuperUser;
import de.adesso.projectboard.core.base.rest.user.persistence.User;
import de.adesso.projectboard.core.base.rest.user.persistence.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional
public class UserServiceIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @MockBean
    private AuthenticationInfo authInfo;

    @Before
    public void setUp() {
        // DB setup
        userRepository.saveAll(getUserList());
    }

    @Test
    public void testGetCurrentUser() {
        when(authInfo.getUserId()).thenReturn("first-user");

        User currentUser = userService.getCurrentUser();

        assertEquals("first-user", currentUser.getId());
    }

    @Test
    public void testGetUserById_OK() {
        User user = userService.getUserById("first-user");

        assertEquals("first-user", user.getId());
    }

    @Test(expected = UserNotFoundException.class)
    public void testGetUserById_NotExisting() {
        userService.getUserById("non-existent-user-id");
    }

    @Test
    public void testUserExists() {
        assertTrue(userService.userExists("first-user"));
        assertFalse(userService.userExists("non-existent-user-id"));
    }

    @Test
    public void testDeleteUser_SuperUser() {
        User userToDelete = userService.getUserById("first-super-user");
        userService.delete(userToDelete);

        assertFalse(userRepository.existsById("first-super-user"));
        assertEquals(2L, userRepository.count());
    }

    @Test
    public void testDeleteUser_User() {
        User userToDelete = userService.getUserById("first-user");
        userService.delete(userToDelete);

        assertTrue(userRepository.existsById("first-super-user"));
        assertFalse(userRepository.existsById("first-user"));
        assertEquals(4L, userRepository.count());

        User firstSuperUser = userRepository.findById("first-super-user").get();
        assertEquals(2L, firstSuperUser.getStaffMembers().size());
    }

    private List<User> getUserList() {
        SuperUser firstSuperUser = new SuperUser("first-super-user");
        firstSuperUser.setFullName("First Super Test", "User");
        firstSuperUser.setEmail("first-super-test-user@test.com");
        firstSuperUser.setLob("LOB Test");

        User firstUser = new User("first-user", firstSuperUser);
        firstUser.setFullName("First Test", "User");
        firstUser.setEmail("first-test-user@test.com");
        firstUser.setLob("LOB Test");

        User secondUser = new User("second-user", firstSuperUser);
        secondUser.setFullName("Second Test", "User");
        secondUser.setEmail("second-test-user@test.com");
        secondUser.setLob("LOB Test");

        SuperUser secondSuperUser = new SuperUser("second-super-user");
        secondSuperUser.setFullName("Second Super Test", "User");
        secondSuperUser.setEmail("second-super-test-user@test.com");
        secondSuperUser.setLob("LOB Test");

        User thirdUser = new User("third-user", secondSuperUser);
        thirdUser.setFullName("Third Test", "User");
        thirdUser.setEmail("third-test-user@test.com");
        thirdUser.setLob("LOB Test");

        return Arrays.asList(firstSuperUser, secondSuperUser, firstUser, secondSuperUser, thirdUser);
    }

}