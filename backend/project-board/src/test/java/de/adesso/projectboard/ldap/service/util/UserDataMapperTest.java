package de.adesso.projectboard.ldap.service.util;

import de.adesso.projectboard.base.user.persistence.User;
import de.adesso.projectboard.base.user.persistence.data.UserData;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserDataMapperTest {

    @Mock
    Attributes attributes;

    @Mock
    Attribute userIdAttribute;

    @Mock
    Attribute fullNameAttribute;

    @Mock
    Attribute firstNameAttribute;

    @Mock
    Attribute lobAttribute;

    @Mock
    Attribute mailAttribute;

    @Mock
    User user;

    UserDataMapper userDataMapper;

    @Before
    public void setUp() throws Exception {
        // set up user mock
        when(user.getId()).thenReturn("user");

        // set up attribute mocks
        when(userIdAttribute.get()).thenReturn("user");
        when(fullNameAttribute.get()).thenReturn("User, Test");
        when(firstNameAttribute.get()).thenReturn("Test");
        when(lobAttribute.get()).thenReturn("LoB");
        when(mailAttribute.get()).thenReturn("test@test.com");

        // set up attributes mock
        when(attributes.get("id")).thenReturn(userIdAttribute);
        when(attributes.get("name")).thenReturn(fullNameAttribute);
        when(attributes.get("givenName")).thenReturn(firstNameAttribute);
        when(attributes.get("division")).thenReturn(lobAttribute);

        // create new userDataMapper instance
        userDataMapper = new UserDataMapper(Collections.singletonList(user), "id");
    }

    @Test
    public void testMapFromAttributes_MailSet() throws NamingException {
        // set up attributes mock
        when(attributes.get("mail")).thenReturn(mailAttribute);

        UserData userData = testMapFromAttributes_General();
        testMapFromAttributes_Email(userData);
    }

    @Test
    public void testMapFromAttributes_MailNotSet() throws NamingException {
        // set up attributes mock
        when(attributes.get("mail")).thenReturn(null);
        when(attributes.get("userPrincipalName")).thenReturn(mailAttribute);

        UserData userData = testMapFromAttributes_General();
        testMapFromAttributes_Email(userData);
    }

    @Test
    public void testMapFromAttributes_NoneSet() throws NamingException {
        // set up attributes mock
        when(attributes.get("mail")).thenReturn(null);
        when(attributes.get("userPrincipalName")).thenReturn(null);

        UserData userData = testMapFromAttributes_General();
        assertNotNull(userData.getEmail());
    }

    @Test
    public void testExtractLastName_LastNameFirstName() {
        String lastName = userDataMapper.extractLastName("Test", "User, Test");

        assertEquals("User", lastName);
    }

    @Test
    public void testExtractLastName_FirstNameLastName() {
        String lastName = userDataMapper.extractLastName("Test", "Test User");

        assertEquals("User", lastName);
    }

    private UserData testMapFromAttributes_General() throws NamingException {
        UserData userData = userDataMapper.mapFromAttributes(attributes);

        assertEquals(user, userData.getUser());
        assertEquals("Test", userData.getFirstName());
        assertEquals("User", userData.getLastName());
        assertEquals("LoB", userData.getLob());

        return userData;
    }

    private void testMapFromAttributes_Email(UserData userData) {
        assertEquals("test@test.com", userData.getEmail());
    }

}