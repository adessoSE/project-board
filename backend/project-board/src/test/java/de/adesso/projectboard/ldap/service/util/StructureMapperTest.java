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
public class StructureMapperTest {

    @Mock
    User user;

    @Mock
    Attributes attributes;

    @Mock
    Attribute managerAttr;

    @Mock
    Attribute directReportsAttr;

    StructureMapper structureMapper;

    @Before
    public void setUp() {
        this.structureMapper = new StructureMapper(user);

        // set up attributes mock
        when(attributes.get("manager")).thenReturn(managerAttr);
        when(attributes.get("directReports")).thenReturn(directReportsAttr);
    }

    @Test
    public void testMapFromAttributes() throws NamingException {
        // set up attribute mocks
        when(managerAttr.get()).thenReturn("manager-ID");
        when(directReportsAttr.size()).thenReturn(2);
        when(directReportsAttr.get(0)).thenReturn("member-1-ID");
        when(directReportsAttr.get(1)).thenReturn("member-2-ID");

        StringStructure stringStructure = structureMapper.mapFromAttributes(attributes);

        assertEquals(user, stringStructure.getUser());
        assertEquals("manager-ID", stringStructure.getManager());
        assertEquals(2, stringStructure.getStaffMembers().size());
        assertTrue(stringStructure.getStaffMembers().stream().anyMatch("member-1-ID"::equals));
        assertTrue(stringStructure.getStaffMembers().stream().anyMatch("member-2-ID"::equals));
    }

}