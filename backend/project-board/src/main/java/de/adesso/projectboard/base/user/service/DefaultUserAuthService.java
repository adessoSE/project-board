package de.adesso.projectboard.base.user.service;

import de.adesso.projectboard.base.security.AuthenticationInfoRetriever;
import de.adesso.projectboard.base.user.persistence.User;

/**
 * Autoconfigured {@link UserAuthService} implementation when no other
 * bean is present. Uses the {@link UserService} and {@link AuthenticationInfoRetriever}
 * for implementation.
 */
public class DefaultUserAuthService implements UserAuthService {

    private final UserService userService;

    private final AuthenticationInfoRetriever retriever;

    public DefaultUserAuthService(UserService userService, AuthenticationInfoRetriever retriever) {
        this.userService = userService;
        this.retriever = retriever;
    }

    @Override
    public User getAuthenticatedUser() {
        return userService.getUserById(retriever.getUserId());
    }

    @Override
    public String getAuthenticatedUserId() {
        return retriever.getUserId();
    }

    @Override
    public boolean authenticatedUserIsAdmin() {
        return retriever.hasAdminRole();
    }

    @Override
    public boolean userIsEffectivelyAManager(User user) {
        return (getAuthenticatedUser().equals(user) && authenticatedUserIsAdmin()) ||
                (userService.userIsManager(user));
    }

}
