package de.adesso.projectboard.base.user.persistence;

import de.adesso.projectboard.base.access.persistence.AccessInfo;
import de.adesso.projectboard.base.application.persistence.ProjectApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserTest {

    @Mock
    ProjectApplication application;

    @Mock
    AccessInfo accessInfo;

    @Test
    public void testConstructor_String() {
        User user = new User("user");

        assertEquals("user", user.id);
    }

    @Test
    public void testAddApplication_OK() {
        User user = new User();

        // set up mock
        when(application.getUser()).thenReturn(user);

        user.addApplication(application);

        assertTrue(user.applications.contains(application));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddApplication_WrongUser() {
        User user = new User();
        User wrongUser = new User();

        // set up mock
        when(application.getUser()).thenReturn(wrongUser);

        user.addApplication(application);
    }

    @Test
    public void testAddAccessInfo_OK() {
        User user = new User();

        // set up mock
        when(accessInfo.getUser()).thenReturn(user);

        user.addAccessInfo(accessInfo);

        assertTrue(user.accessInfoList.contains(accessInfo));
    }

    @Test
    public void testGetLatestAccessInfo_NotNull() {
        User user = new User();
        user.accessInfoList = Collections.singletonList(accessInfo);

        assertEquals(accessInfo, user.getLatestAccessInfo());
    }

    @Test
    public void testGetLatestAccessInfo_Null() {
        User user = new User();

        assertNull(user.getLatestAccessInfo());
    }

}