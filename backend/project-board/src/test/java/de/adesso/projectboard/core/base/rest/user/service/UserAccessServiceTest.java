package de.adesso.projectboard.core.base.rest.user.service;

import de.adesso.projectboard.core.base.rest.exceptions.UserNotFoundException;
import de.adesso.projectboard.core.base.rest.user.persistence.SuperUser;
import de.adesso.projectboard.core.base.rest.user.persistence.User;
import de.adesso.projectboard.core.base.rest.user.persistence.UserRepository;
import de.adesso.projectboard.core.base.rest.user.useraccess.dto.UserAccessInfoRequestDTO;
import net.bytebuddy.description.type.TypeDefinition;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserAccessServiceTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserAccessService accessService;

    private User testUser;

    @Before
    public void setUp() {
        // data setup
        SuperUser testSuperUser = new SuperUser("super-user");
        testSuperUser.setFullName("Super", "User");
        testSuperUser.setLob("LOB Test");
        testSuperUser.setEmail("super.user@test.com");

        this.testUser = new User("user", testSuperUser);
        this.testUser.setFullName("Normal", "User");
        this.testUser.setLob("LOB Test");
        this.testUser.setEmail("normal.user@test.com");

        // mock setup
        when(userService.getUserById(anyString())).thenThrow(UserNotFoundException.class);
        when(userService.getUserById(eq(testUser.getId()))).thenReturn(testUser);

        // just return the passed argument
        when(userService.save(any(User.class))).thenAnswer((Answer<User>) invocation -> {
            Object[] args = invocation.getArguments();
            return (User) args[0];
        });
    }

    @Test
    public void testDeleteAccessForUser_OK() {
        LocalDateTime accessEndTime = LocalDateTime.now().plus(2L, ChronoUnit.DAYS);
        testUser.giveAccessUntil(accessEndTime);
        assertTrue(testUser.hasAccess());

        User returnedUser = accessService.deleteAccessForUser(testUser.getId());
        verify(userService).save(testUser);
        assertEquals(testUser, returnedUser);
        assertFalse(returnedUser.hasAccess());
    }

    @Test(expected = UserNotFoundException.class)
    public void testDeleteAccessForUser_UserNotExists() {
        accessService.deleteAccessForUser("non-existent-user");
    }

    @Test
    public void testCreateAccessForUser_OK() {
        LocalDateTime accessEndTime = LocalDateTime.now().plus(2L, ChronoUnit.DAYS);
        UserAccessInfoRequestDTO dto = new UserAccessInfoRequestDTO(accessEndTime);

        User returnedUser = accessService.createAccessForUser(dto, testUser.getId());
        verify(userService).save(testUser);
        assertEquals(testUser, returnedUser);
        assertTrue(returnedUser.hasAccess());
        assertEquals(accessEndTime, returnedUser.getAccessObject().getAccessEnd());
    }

    @Test(expected = UserNotFoundException.class)
    public void testCreateAccessForUser_UserNotExists() {
        LocalDateTime accessEndTime = LocalDateTime.now().plus(2L, ChronoUnit.DAYS);
        UserAccessInfoRequestDTO dto = new UserAccessInfoRequestDTO(accessEndTime);

        accessService.createAccessForUser(dto, "non-existent-user");
    }

}