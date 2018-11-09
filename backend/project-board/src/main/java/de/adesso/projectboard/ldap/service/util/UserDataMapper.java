package de.adesso.projectboard.ldap.service.util;

import de.adesso.projectboard.base.user.persistence.User;
import de.adesso.projectboard.base.user.persistence.data.UserData;
import org.springframework.ldap.core.AttributesMapper;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import java.util.List;
import java.util.Objects;

/**
 * {@link AttributesMapper} implementation to map LDAP query results
 * to {@link UserData} instances.
 * <p/>
 * <p>
 *     Requires the <i>name, givenName, ID Attribute</i> and <i>division</i>
 *     attributes to be present in the {@link Attributes} to map
 *     the result.
 * </p>
 */
public class UserDataMapper implements AttributesMapper<UserData> {

    private final List<User> users;

    private final String idAttribute;

    /**
     *
     * @param users
     *          The {@link User}s the mapped {@link UserData}
     *          instances belong to.
     *
     * @param idAttribute
     *          The attribute name of the attribute used as the
     *          user ID.
     */
    public UserDataMapper(List<User> users, String idAttribute) {
        this.users = Objects.requireNonNull(users);
        this.idAttribute = Objects.requireNonNull(idAttribute);
    }

    @Override
    public UserData mapFromAttributes(Attributes attributes) throws NamingException {
        String userId = (String) attributes.get(idAttribute).get();
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

        // get the corresponding user from the users list
        User owningUser = users.stream()
                .filter(user -> userId.equals(user.getId()))
                .findFirst()
                .orElseThrow(IllegalStateException::new);

        return new UserData(owningUser, firstName, lastName, email, lob);
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
