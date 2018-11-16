package de.adesso.projectboard.base.access.persistence;

import de.adesso.projectboard.base.user.persistence.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class AccessInfoTest {

    @Mock
    User user;

    @Test
    public void testConstructor_User_DateTime() {
        LocalDateTime endTime = LocalDateTime.now().plus(10L, ChronoUnit.DAYS);
        AccessInfo accessInfo = new AccessInfo(user, endTime);

        assertEquals(user, accessInfo.user);
        assertNotNull(accessInfo.accessStart);
        assertEquals(endTime, accessInfo.accessEnd);
        verify(user).addAccessInfo(accessInfo);
    }

    @Test
    public void testConstructor_User_DateTime_DateTime_OK() {
        LocalDateTime startTime = LocalDateTime.now().minus(10L, ChronoUnit.DAYS);
        LocalDateTime endTime = LocalDateTime.now().plus(10L, ChronoUnit.DAYS);
        AccessInfo accessInfo = new AccessInfo(user, startTime, endTime);

        assertEquals(user, accessInfo.user);
        assertEquals(startTime, accessInfo.accessStart);
        assertEquals(endTime, accessInfo.accessEnd);
        verify(user).addAccessInfo(accessInfo);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructor_User_DateTime_DateTime_StartAfterEnd() {
        LocalDateTime startTime = LocalDateTime.now().plus(10L, ChronoUnit.DAYS);
        LocalDateTime endTime = LocalDateTime.now().minus(10L, ChronoUnit.DAYS);

        new AccessInfo(user, startTime, endTime);
    }

    @Test
    public void testIsCurrentlyActive_Active() {
        LocalDateTime startTime = LocalDateTime.now().minus(10L, ChronoUnit.DAYS);
        LocalDateTime endTime = LocalDateTime.now().plus(10L, ChronoUnit.DAYS);

        AccessInfo accessInfo = new AccessInfo();
        accessInfo.accessStart = startTime;
        accessInfo.accessEnd = endTime;

        assertTrue(accessInfo.isCurrentlyActive());
    }

    @Test
    public void testIsCurrentlyActive_InActive() {
        LocalDateTime startTime = LocalDateTime.now().minus(10L, ChronoUnit.DAYS);
        LocalDateTime endTime = LocalDateTime.now().minus(5L, ChronoUnit.DAYS);

        AccessInfo accessInfo = new AccessInfo();
        accessInfo.accessStart = startTime;
        accessInfo.accessEnd = endTime;

        assertFalse(accessInfo.isCurrentlyActive());
    }

}