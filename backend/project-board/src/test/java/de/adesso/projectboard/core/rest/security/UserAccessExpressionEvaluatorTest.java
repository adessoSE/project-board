package de.adesso.projectboard.core.rest.security;

import de.adesso.projectboard.core.base.rest.user.persistence.SuperUser;
import de.adesso.projectboard.core.base.rest.user.persistence.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class UserAccessExpressionEvaluatorTest {

    private UserAccessExpressionEvaluator evaluator;

    @Test
    public void testHasAccessToProjects_HasAccess() {
        User testUser = getNewTestUser();

        testUser.giveAccessUntil(LocalDateTime.now().plus(7L, ChronoUnit.DAYS));

        assertTrue(evaluator.hasAccessToProjects(null, testUser));
    }

    @Test
    public void testHasAccessToProjects_NoAccess() {
        assertFalse(evaluator.hasAccessToProjects(null, getNewTestUser()));
    }

    private User getNewTestUser() {
        SuperUser firstUser = new SuperUser("first-user");
        firstUser.setFullName("First", "User");
        firstUser.setEmail("first.user@example.com");
        firstUser.setLob("LOB Test");

        User secondUser = new User("second-user", firstUser);
        secondUser.setFullName("Second", "User");
        secondUser.setEmail("second.user@example.com");
        secondUser.setLob("LOB Test");

        return secondUser;
    }

}