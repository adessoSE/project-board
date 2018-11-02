package de.adesso.projectboard.crawler.util;

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
