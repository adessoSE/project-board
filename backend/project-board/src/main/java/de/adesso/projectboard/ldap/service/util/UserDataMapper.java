package de.adesso.projectboard.ldap.service.util;

import de.adesso.projectboard.base.user.persistence.User;
import de.adesso.projectboard.base.user.persistence.data.UserData;
import org.springframework.ldap.core.AttributesMapper;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import java.util.Objects;

/**
 * {@link AttributesMapper} implementation to map LDAP query results
 * to {@link UserData} instances.
 * <p/>
 * <p>
 *     Requires the <i>name, givenName</i> and <i>division</i>
 *     attributes to be present in the {@link Attributes} to map
 *     the result.
 * </p>
 */
public class UserDataMapper implements AttributesMapper<UserData> {

    private final User user;

    /**
     *
     * @param user
     *          The {@link User} the mapped {@link UserData}
     *          instances belong to.
     */
    public UserDataMapper(User user) {
        this.user = Objects.requireNonNull(user);
    }

    @Override
    public UserData mapFromAttributes(Attributes attributes) throws NamingException {
        String fullName = (String) attributes.get("name").get();
        String firstName = (String) attributes.get("givenName").get();
        String lob = (String) attributes.get("division").get();
        String lastName = extractLastName(firstName, fullName);

        // mail attribute not set on every user
        // use userPrincipalName as a fallback
        String email = "placeholder";
        if(attributes.get("mail") != null) {
            email = (String) attributes.get("mail").get();
        } else if(attributes.get("userPrincipalName") != null) {
            email = (String) attributes.get("userPrincipalName").get();
        }

        return new UserData(user, firstName, lastName, email, lob);
    }

    /**
     *
     * @param givenName
     *          The given name.
     *
     * @param fullName
     *          The full name.
     *
     * @return
     *          The last name.
     */
    String extractLastName(String givenName, String fullName) {
        String lastName = fullName.replace(givenName, "");

        // remove ','
        if(lastName.contains(",")) {
            lastName = lastName.replace(",", "");
        }

        // remove leading whitespace
        if(lastName.charAt(0) == ' ') {
            lastName = lastName.substring(1);
        }

        // remove trailing whitespace
        if(lastName.charAt(lastName.length() - 1) == ' ') {
            lastName = lastName.substring(0, lastName.length() - 1);
        }

        return lastName;
    }

}
