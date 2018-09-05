package de.adesso.projectboard.core.rest.security;

import de.adesso.projectboard.core.base.rest.user.UserService;
import de.adesso.projectboard.core.base.rest.user.persistence.User;
import de.adesso.projectboard.core.rest.useraccess.persistence.UserAccessInfo;
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

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserAccessExpressionEvaluatorTest {

    // TODO: fix test

    @Mock
    private KeycloakAuthenticationInfo authInfo;

    @Mock
    private UserAccessInfoRepository accessInfoRepo;

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
        LocalDateTime accessStart = LocalDateTime.now().plus(7L, ChronoUnit.WEEKS);
        LocalDateTime accessEnd = LocalDateTime.now().plus(2L, ChronoUnit.WEEKS);

        UserAccessInfo info = new UserAccessInfo(new User(authInfo.getUserId()), accessEnd);
        info.setAccessStart(accessStart);

        when(accessInfoRepo.findFirstByUserOrderByAccessEndDesc(any()))
                .thenReturn(Optional.of(info));

        assertTrue(evaluator.hasAccessToProjects(null));
    }

    /*@Test
    public void testHasAccessToProjects_NoOptionalValue() {
        when(accessInfoRepo.findFirstByUserIdOrderByAccessEndDesc(anyString()))
                .thenReturn(Optional.empty());

        assertFalse(evaluator.hasAccessToProjects(null));
    }

    @Test
    public void testHasAccessToProjects_NoAccess() {
        LocalDateTime accessStart = LocalDateTime.now().minus(6L, ChronoUnit.DAYS);
        LocalDateTime accessEnd = LocalDateTime.now().minus(1L, ChronoUnit.DAYS);

        UserAccessInfo info = new UserAccessInfo(authInfo.getUsername(), accessEnd);
        info.setAccessStart(accessStart);

        when(accessInfoRepo.findFirstByUserIdOrderByAccessEndDesc(anyString()))
                .thenReturn(Optional.of(info));

        assertFalse(evaluator.hasAccessToProjects(null));
    }*/

}