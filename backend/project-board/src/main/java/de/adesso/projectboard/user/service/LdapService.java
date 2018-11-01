package de.adesso.projectboard.user.service;

import de.adesso.projectboard.ldap.configuration.LdapConfigurationProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.query.LdapQuery;
import org.springframework.stereotype.Service;

import static org.springframework.ldap.query.LdapQueryBuilder.query;

@Profile("adesso-ad")
@Service
public class LdapService {

    private final LdapTemplate ldapTemplate;

    private final LdapConfigurationProperties ldapProperties;

    @Autowired
    public LdapService(LdapTemplate ldapTemplate, LdapConfigurationProperties ldapProperties) {
        this.ldapTemplate = ldapTemplate;
        this.ldapProperties = ldapProperties;
    }

    /**
     *
     * @param userId
     *          The ID of the user.
     *
     * @return
     *          {@code true}, iff the LDAP query searching for
     *          a <i>person</i> at the {@link LdapConfigurationProperties#getLdapBase() base}
     *          where the {@link LdapConfigurationProperties#getUserIdAttribute() user ID attribute}
     *          is equal to the given {@code userId} is <b>not empty</b>.
     *
     * @see LdapConfigurationProperties#getLdapBase()
     * @see LdapConfigurationProperties#getUserIdAttribute()
     */
    public boolean userExists(String userId) {
        String idAttribute = ldapProperties.getUserIdAttribute();
        String base = ldapProperties.getLdapBase();

        LdapQuery query = query()
                .countLimit(1)
                .base(base)
                .attributes(idAttribute, "objectClass")
                .where("objectClass").is("person")
                .and(idAttribute).isPresent()
                .and(idAttribute).is(userId);

        return !ldapTemplate.search(query, (AttributesMapper<String>) attributes -> (String) attributes.get(idAttribute).get()).isEmpty();
    }

}
