package de.adesso.projectboard.ldap.service.util;

import de.adesso.projectboard.base.user.persistence.User;
import de.adesso.projectboard.ldap.service.util.data.StringStructure;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DnStructureMapperTest {

    @Mock
    User user;

    @Mock
    Attributes attributes;

    @Mock
    Attribute managerAttr;

    @Mock
    Attribute distinguishedNameAttr;

    DnStructureMapper structureMapper;

    @Before
    public void setUp() {
        this.structureMapper = new DnStructureMapper(user);

        // set up attributes mock
        when(attributes.get("manager")).thenReturn(managerAttr);
        when(attributes.get("distinguishedName")).thenReturn(distinguishedNameAttr);
    }

    @Test
    public void testMapFromAttributes() throws NamingException {
        // set up attribute mocks
        when(managerAttr.get()).thenReturn("managerDN");
        when(distinguishedNameAttr.get()).thenReturn("selfDN");

        StringStructure stringStructure = structureMapper.mapFromAttributes(attributes);

        assertEquals(user, stringStructure.getOwner());
        assertEquals("managerDN", stringStructure.getManager());
        assertEquals("selfDN", stringStructure.getUser());
        assertTrue(stringStructure.getStaffMembers().isEmpty());
    }

}