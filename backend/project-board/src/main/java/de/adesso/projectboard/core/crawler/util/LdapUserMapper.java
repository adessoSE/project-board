package de.adesso.projectboard.core.crawler.util;

import org.springframework.ldap.core.AttributesMapper;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;

/**
 * {@link AttributesMapper} implementation to map LDAP query results to {@link LdapUser}
 * instances.
 *
 * @see de.adesso.projectboard.core.crawler.UserCrawler
 */
public class LdapUserMapper implements AttributesMapper<LdapUser> {

    @Override
    public LdapUser mapFromAttributes(Attributes attributes) throws NamingException {
        String distinguishedName = (String) attributes.get("distinguishedName").get();
        String sAMAccountName = (String) attributes.get("sAMAccountName").get();
        String name = (String) attributes.get("name").get();
        String givenName = (String) attributes.get("givenName").get();
        String mail = (String) attributes.get("mail").get();
        String division = (String) attributes.get("division").get();
        String manager = (String) attributes.get("manager").get();

        return new LdapUser(distinguishedName, sAMAccountName, name,
                givenName, mail, division, manager);
    }

}
