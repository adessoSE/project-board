package de.adesso.projectboard.ldap.service.util;

import de.adesso.projectboard.base.user.persistence.User;
import de.adesso.projectboard.ldap.service.util.data.StringStructure;
import org.springframework.ldap.core.AttributesMapper;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import java.util.LinkedHashSet;
import java.util.Objects;

/**
 * {@link AttributesMapper} implementation to map LDAP query
 * results to {@link StringStructure} instances.
 * <p/>
 * <p>
 *      Requires the <i>manager</i> attribute to be present
 *      in the {@link Attributes} to map the result.
 * </p>
 */
public class StructureMapper implements AttributesMapper<StringStructure> {

    private final User user;

    /**
     *
     * @param user
     *          The {@link User} to get the organizational
     *          structure from.
     */
    public StructureMapper(User user) {
        this.user = Objects.requireNonNull(user);
    }

    @Override
    public StringStructure mapFromAttributes(Attributes attributes) throws NamingException {
        String managerDn = (String) attributes.get("manager").get();

        StringStructure structure = new StringStructure()
                .setUser(user)
                .setManager(managerDn);

        // split the 'directReports' distinguished names
        // into a set if it is present
        Attribute directReports = attributes.get("directReports");
        if(directReports != null) {
            LinkedHashSet<String> directReportsDn = new LinkedHashSet<>();

            for(int i = 0; i < directReports.size(); i++) {
                String dn = (String) directReports.get(i);

                directReportsDn.add(dn);
            }

            structure.setStaffMembers(directReportsDn);
        }

        return structure;
    }

}
