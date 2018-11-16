package de.adesso.projectboard.ldap.access;

import de.adesso.projectboard.base.access.persistence.AccessInfo;
import de.adesso.projectboard.base.access.persistence.AccessInfoRepository;
import de.adesso.projectboard.base.user.persistence.User;
import de.adesso.projectboard.ldap.user.LdapUserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class LdapUserAccessServiceTest {

    @Mock
    LdapUserService userService;

    @Mock
    AccessInfoRepository infoRepo;

    @InjectMocks
    LdapUserAccessService accessService;

    @Mock
    User user;

    @Mock
    AccessInfo accessInfo;

    @Test(expected = IllegalArgumentException.class)
    public void testGiveUserAccessUntil_IllegalArgument() {
        accessService.giveUserAccessUntil(user, LocalDateTime.now().minus(10L, ChronoUnit.DAYS));
    }

    @Test
    public void testGiveUserAccessUntil_LatestNull_NewInstance() {
        LocalDateTime accessEnd = LocalDateTime.now().plus(5L ,ChronoUnit.DAYS);

        // set up user mock
        when(user.getLatestAccessInfo()).thenReturn(null);

        accessService.giveUserAccessUntil(user, accessEnd);

        verify(userService).save(user);
        verify(user).addAccessInfo(any());
    }

    @Test
    public void testGiveUserAccessUntil_LatestNotInActive_NewInstance() {
        LocalDateTime accessEnd = LocalDateTime.now().plus(5L ,ChronoUnit.DAYS);

        // set up entity mocks
        when(accessInfo.isCurrentlyActive()).thenReturn(false);
        when(user.getLatestAccessInfo()).thenReturn(accessInfo);

        accessService.giveUserAccessUntil(user, accessEnd);

        verify(userService).save(user);
        verify(user).addAccessInfo(any());
    }

    @Test
    public void testGiveUserAccessUntil_LatestActive_NoNewInstance() {
        LocalDateTime accessEnd = LocalDateTime.now().plus(5L ,ChronoUnit.DAYS);

        // set up entity mocks
        when(accessInfo.isCurrentlyActive()).thenReturn(true);
        when(user.getLatestAccessInfo()).thenReturn(accessInfo);

        accessService.giveUserAccessUntil(user, accessEnd);

        verify(user, never()).addAccessInfo(any());
        verify(accessInfo).setAccessEnd(accessEnd);
        verify(infoRepo).save(accessInfo);
    }

    @Test
    public void testRemoveAccessFromUser_HasAccess() {
        // set up entity mocks
        when(accessInfo.isCurrentlyActive()).thenReturn(true);
        when(user.getLatestAccessInfo()).thenReturn(accessInfo);

        accessService.removeAccessFromUser(user);

        verify(accessInfo).setAccessEnd(any());
        verify(infoRepo).save(accessInfo);
    }

    @Test
    public void testRemoveAccessFromUser_NoAccess() {
        // set up entity mocks
        when(accessInfo.isCurrentlyActive()).thenReturn(false);
        when(user.getLatestAccessInfo()).thenReturn(accessInfo);

        accessService.removeAccessFromUser(user);

        verify(accessInfo, never()).setAccessEnd(any());
        verify(infoRepo, never()).save(accessInfo);
    }

    @Test
    public void testUserHasAccess_ActiveInfo() {
        // set up entity mocks
        when(accessInfo.isCurrentlyActive()).thenReturn(true);
        when(user.getLatestAccessInfo()).thenReturn(accessInfo);

        assertTrue(accessService.userHasAccess(user));
    }

    @Test
    public void testUserHasAccess_NoActiveInfo() {
        // set up user/service mock
        when(user.getLatestAccessInfo()).thenReturn(null);

        assertFalse(accessService.userHasAccess(user));
    }

}