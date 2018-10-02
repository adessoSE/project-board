package de.adesso.projectboard.core.base.rest.user.service;

import de.adesso.projectboard.core.base.rest.exceptions.UserNotFoundException;
import de.adesso.projectboard.core.base.rest.user.persistence.SuperUser;
import de.adesso.projectboard.core.base.rest.user.persistence.User;
import de.adesso.projectboard.core.base.rest.user.persistence.UserRepository;
import de.adesso.projectboard.core.base.rest.user.useraccess.dto.UserAccessInfoRequestDTO;
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

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional
public class UserAccessServiceIntegrationTest {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private UserAccessService accessService;

    @MockBean
    private UserService userService;

    @Before
    public void setUp() {
        // DB setup
        SuperUser superUser = new SuperUser("super-user");
        superUser.setFullName("Super", "User");
        superUser.setLob("LOB Test");
        superUser.setEmail("super.user@test.com");

        User user = new User("user", superUser);
        user.setFullName("Normal", "User");
        user.setLob("LOB Test");
        user.setEmail("normal.user@test.com");

        userRepo.saveAll(Arrays.asList(superUser, user));

        // mock setup
        when(userService.getUserById(anyString())).thenThrow(UserNotFoundException.class);
        when(userService.getUserById(eq("super-user"))).thenReturn(userRepo.findById("super-user").get());
        when(userService.getUserById(eq("user"))).thenReturn(userRepo.findById("user").get());
    }

    @Test
    public void testDeleteAccessForUser() {
        User user = userRepo.findById("user").get();

        LocalDateTime accessEndTime = LocalDateTime.now().plus(2L, ChronoUnit.DAYS);
        user.giveAccessUntil(accessEndTime);
        userRepo.save(user);

        user = userRepo.findById("user").get();
        assertTrue(user.hasAccess());

        accessService.deleteAccessForUser(user.getId());
        user = userRepo.findById("user").get();
        assertFalse(user.hasAccess());
    }

    @Test
    public void testCreateAccessForUser() {
        User user = userRepo.findById("user").get();
        assertFalse(user.hasAccess());

        LocalDateTime accessEndTime = LocalDateTime.now().plus(2L, ChronoUnit.DAYS);

        UserAccessInfoRequestDTO dto = new UserAccessInfoRequestDTO();
        dto.setAccessEnd(accessEndTime);
        accessService.createAccessForUser(dto, user.getId());

        user = userRepo.findById("user").get();
        assertTrue(user.hasAccess());
        assertEquals(accessEndTime, user.getAccessObject().getAccessEnd());
    }

}