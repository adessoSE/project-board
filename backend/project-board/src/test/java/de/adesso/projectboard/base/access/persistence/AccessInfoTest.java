package de.adesso.projectboard.base.access.persistence;

import org.junit.Test;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AccessInfoTest {

    @Test(expected = IllegalArgumentException.class)
    public void testConstructor_EndBeforeStart() {
        LocalDateTime startTime = LocalDateTime.of(2018, 1, 2, 13,37);
        LocalDateTime endTime = LocalDateTime.of(2018, 1, 1, 13,37);
        SuperUser user = new SuperUser("user");

        AccessInfo info = new AccessInfo(user, startTime, endTime);
    }

    @Test
    public void testIsCurrentylyActive_Active() {
        LocalDateTime startTime = LocalDateTime.now().minus(10L, ChronoUnit.DAYS);
        LocalDateTime endTime = LocalDateTime.now().plus(10L, ChronoUnit.DAYS);
        SuperUser user = new SuperUser("user");

        AccessInfo accessInfo = new AccessInfo(user, startTime, endTime);

        assertTrue(accessInfo.isCurrentlyActive());
    }

    @Test
    public void testIsCurrentylyActive_Inactive() {
        LocalDateTime startTime = LocalDateTime.now().minus(10L, ChronoUnit.DAYS);
        LocalDateTime endTime = LocalDateTime.now().minus(1L, ChronoUnit.DAYS);
        SuperUser user = new SuperUser("user");

        AccessInfo accessInfo = new AccessInfo(user, startTime, endTime);

        assertFalse(accessInfo.isCurrentlyActive());
    }

}