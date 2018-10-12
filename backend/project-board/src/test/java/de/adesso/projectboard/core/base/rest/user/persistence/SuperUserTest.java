package de.adesso.projectboard.core.base.rest.user.persistence;

import org.junit.Test;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.Assert.*;

public class SuperUserTest {

    @Test
    public void testBossSetToSelf() {
        SuperUser superUser = new SuperUser("user-1");

        assertEquals(superUser, superUser.getBoss());
        assertTrue(superUser.getStaffMembers().contains(superUser));
    }

    @Test
    public void testAddStaffMember() {
        SuperUser superUser = new SuperUser("user-1");
        SuperUser superUser2 = new SuperUser("user-2");
        User user = new User("user-3", superUser);

        assertNotNull(user.getBoss());
        assertEquals(superUser, user.getBoss());
        assertTrue(superUser.getStaffMembers().contains(user));

        superUser2.addStaffMember(user);

        assertNotNull(user.getBoss());
        assertEquals(superUser2, user.getBoss());
        assertTrue(superUser2.getStaffMembers().contains(user));
        assertFalse(superUser.getStaffMembers().contains(user));
    }

    @Test
    public void testHasRemoveAccess() {
        SuperUser superUser = new SuperUser("user-1");
        assertFalse(superUser.hasAccess());

        LocalDateTime accessEndNew = LocalDateTime.now().plus(10L, ChronoUnit.DAYS);
        superUser.giveAccessUntil(accessEndNew);
        assertTrue(superUser.hasAccess());
        assertNotNull(superUser.getAccessObject());

        superUser.removeAccess();
        assertFalse(superUser.hasAccess());
    }

    @Test
    public void testGiveAccessUntil() {
        SuperUser superUser = new SuperUser("user-1");

        LocalDateTime accessEnd = LocalDateTime.now().plus(2L, ChronoUnit.DAYS);
        superUser.giveAccessUntil(accessEnd);
        assertEquals(accessEnd, superUser.getAccessObject().getAccessEnd());

        // test elongation of the currently active user access
        LocalDateTime accessEndNew = LocalDateTime.now().plus(10L, ChronoUnit.DAYS);
        superUser.giveAccessUntil(accessEndNew);
        assertEquals(accessEndNew, superUser.getAccessObject().getAccessEnd());
        assertEquals(1L, superUser.getAccessInfoList().size());

        superUser.removeAccess();
        superUser.giveAccessUntil(accessEndNew);
        assertEquals(accessEndNew, superUser.getAccessObject().getAccessEnd());
        assertEquals(2L, superUser.getAccessInfoList().size());
    }

}