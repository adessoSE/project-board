package de.adesso.projectboard.core.crawler.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
public class LdapUser {

    private final String distinguishedName;

    private final String sAMAccountName;

    private final String name;

    private final String givenName;

    private final String mail;

    private final String division;

    private final String managerDN;

    public String getFirstName() {
        return givenName;
    }

    public String getLastName() {
        if(name != null && givenName != null) {
            String lastName = name.replace(givenName, "");

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

        return name;
    }

    @Override
    public boolean equals(Object other) {
        if(other instanceof LdapUser) {
            LdapUser otherUser = (LdapUser) other;

            return distinguishedName.equals(otherUser.distinguishedName);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(distinguishedName);
    }

}