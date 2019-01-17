package de.adesso.projectboard.ad.service.mapper;

import de.adesso.projectboard.ad.service.node.LdapUserNode;
import org.junit.Before;
import org.junit.Test;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class LdapUserNodeMapperTest {

    private final String ID_ATTR_ID = "sAMAccountName";

    private LdapUserNodeMapper mapper;

    @Before
    public void setUp() {
        this.mapper = new LdapUserNodeMapper(ID_ATTR_ID);
    }

    @Test
    public void mapFromAttributeReturnsExpectedNode() throws NamingException {
        // given
        var expectedId = "ID";
        var expectedName = "full name";
        var expectedGivenName = "given name";
        var expectedMail = "mail@test.com";
        var expectedManagerDn = "manager DN";
        var expectedSurname = "surname";
        var expectedDepartment = "department";
        var expectedDivision = "division";
        var expectedDn = "user DN";
        var expectedDirectReports = List.of("D1", "D2");

        var idAttr = createAttribute(ID_ATTR_ID, expectedId);
        var nameAttr = createAttribute("name", expectedName);
        var givenNameAttr = createAttribute("givenName", expectedGivenName);
        var mailAttr = createAttribute("mail", expectedMail);
        var managerAttr = createAttribute("manager", expectedManagerDn);
        var surnameAttr = createAttribute("sn", expectedSurname);
        var departmentAttr = createAttribute("department", expectedDepartment);
        var divisionAttr = createAttribute("division", expectedDivision);
        var dnAttr = createAttribute("distinguishedName", expectedDn);
        var directReportsAttr = createAttribute("directReports", "D1", "D2");

        var attributes = new BasicAttributes();
        for(var attr : List.of(idAttr, nameAttr, givenNameAttr, mailAttr, managerAttr,
                surnameAttr, departmentAttr, divisionAttr, dnAttr, directReportsAttr)) {
            attributes.put(attr);
        }

        var expectedNode = new LdapUserNode(expectedId, expectedName, expectedGivenName, expectedSurname,
                expectedMail, expectedDivision, expectedDepartment, expectedDn,
                expectedManagerDn, expectedDirectReports);

        // when
        var actualNode = mapper.mapFromAttributes(attributes);

        // then
        assertThat(actualNode).isEqualTo(expectedNode);
    }

    private Attribute createAttribute(String attrId, Object... attrValues) {
        var attribute = new BasicAttribute(attrId);

        for(var value : attrValues) {
            attribute.add(value);
        }

        return attribute;
    }

}