package de.adesso.projectboard.ad.service.mapper;

import de.adesso.projectboard.ad.service.node.LdapUserNode;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;

public class LdapUserNodeMapper extends BaseAttributeMapper<LdapUserNode> {

    private final String idAttribute;

    public LdapUserNodeMapper(String idAttribute) {
        this.idAttribute = idAttribute;
    }

    @Override
    public LdapUserNode mapFromAttributes(Attributes attributes) throws NamingException {
        var idAttrValue = getSingleAttributeValue(attributes, idAttribute, String.class);
        var nameAttrValue = getSingleAttributeValue(attributes, "name", String.class);
        var givenNameAttrValue = getSingleAttributeValue(attributes, "givenName", String.class);
        var userPrincipalAttrValue = getSingleAttributeValue(attributes, "userPrincipalName", String.class);
        var managerAttrValue = getSingleAttributeValue(attributes, "manager", String.class);
        var snAttrValue = getSingleAttributeValue(attributes, "sn", String.class);
        var departmentAttrValue = getSingleAttributeValue(attributes, "department", String.class);
        var divisionAttrValue = getSingleAttributeValue(attributes, "division", String.class);
        var dnAttrValue = getSingleAttributeValue(attributes, "distinguishedName", String.class);

        var directReportsAttrValue = getAllAttributeValues(attributes, "directReports", String.class);

        return new LdapUserNode(idAttrValue, nameAttrValue, givenNameAttrValue, snAttrValue, userPrincipalAttrValue,
                divisionAttrValue, departmentAttrValue, dnAttrValue, managerAttrValue, directReportsAttrValue);
    }

}
