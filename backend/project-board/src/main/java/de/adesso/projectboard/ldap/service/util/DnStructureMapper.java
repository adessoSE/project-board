package de.adesso.projectboard.ldap.service.util;

import de.adesso.projectboard.base.user.persistence.User;
import de.adesso.projectboard.ldap.service.util.data.StringStructure;
import org.springframework.ldap.core.AttributesMapper;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import java.util.Objects;

/**
 * {@link AttributesMapper} implementation to map LDAP query
 * results to {@link StringStructure} instances.
 * <p/>
 * <p>
 *      Requires the <i>manager</i> and the <i>distinguishedName</i> attributes
 *      to be present in the {@link Attributes} to map the result.
 * </p>
 */
public class DnStructureMapper implements AttributesMapper<StringStructure> {

    final User user;

    /**
     *
     * @param user
     *          The {@link User} the structure is created
     *          for.
     */
    public DnStructureMapper(User user) {
        this.user = Objects.requireNonNull(user);
    }

    @Override
    public StringStructure mapFromAttributes(Attributes attributes) throws NamingException {
        String managerDn = (String) attributes.get("manager").get();
        String userDN = (String) attributes.get("distinguishedName").get();

        return new StringStructure()
                .setOwner(user)
                .setUser(userDN)
                .setManager(managerDn);
    }

}
