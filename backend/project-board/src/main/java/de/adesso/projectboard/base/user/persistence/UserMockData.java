package de.adesso.projectboard.base.user.persistence;

import de.adesso.projectboard.ldap.user.LdapUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("test-data")
@Component
public class UserMockData {

    private final LdapUserService userService;

    @Autowired
    public UserMockData(LdapUserService userService) {
        this.userService = userService;

        addUsers();
    }

    private void addUsers() {
        // creating mock-users for test-reasons
    }

}
