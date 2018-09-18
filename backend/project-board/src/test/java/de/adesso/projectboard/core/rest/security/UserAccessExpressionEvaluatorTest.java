package de.adesso.projectboard.core.rest.security;

import de.adesso.projectboard.core.base.rest.user.UserService;
import de.adesso.projectboard.core.base.rest.user.persistence.User;
import de.adesso.projectboard.core.base.rest.user.useraccess.persistence.UserAccessInfo;
import de.adesso.projectboard.core.rest.useraccess.persistence.UserAccessInfoRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserAccessExpressionEvaluatorTest {

    @Mock
    private KeycloakAuthenticationInfo authInfo;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserAccessExpressionEvaluator evaluator;

    @Before
    public void setUp() {
        when(authInfo.getUserId()).thenReturn("user");
    }

    @Test
    public void testHasAccessToProjects_HasAccess() {
        LocalDateTime accessStart = LocalDateTime.now().minus(1L, ChronoUnit.WEEKS);
        LocalDateTime accessEnd = LocalDateTime.now().plus(2L, ChronoUnit.WEEKS);

        User user = new User(authInfo.getUserId());
        UserAccessInfo info = new UserAccessInfo(user, accessEnd);
        info.setAccessStart(accessStart);

        when(accessInfoRepo.getLatestAccessInfo(any()))
                .thenReturn(Optional.of(info));

        assertTrue(evaluator.hasAccessToProjects(null, user));
    }

    @Test
    public void testHasAccessToProjects_NoOptionalValue() {
        when(accessInfoRepo.getLatestAccessInfo(any()))
                .thenReturn(Optional.empty());

        assertFalse(evaluator.hasAccessToProjects(null, new User("test")));
    }

    @Test
    public void testHasAccessToProjects_NoAccess() {
        LocalDateTime accessStart = LocalDateTime.now().minus(6L, ChronoUnit.DAYS);
        LocalDateTime accessEnd = LocalDateTime.now().minus(1L, ChronoUnit.DAYS);

        User user = new User(authInfo.getUserId());
        UserAccessInfo info = new UserAccessInfo(user, accessEnd);
        info.setAccessStart(accessStart);

        when(accessInfoRepo.getLatestAccessInfo(any()))
                .thenReturn(Optional.of(info));

        assertFalse(evaluator.hasAccessToProjects(null, user));
    }

}