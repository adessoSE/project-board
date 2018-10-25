package de.adesso.projectboard.core.base.rest.security;

import de.adesso.projectboard.core.base.rest.user.service.UserService;

/**
 * Interface used by the {@link UserService} to get the id of the currently authenticated user.
 *
 * @see de.adesso.projectboard.core.rest.security.KeycloakAuthenticationInfo
 * @see UserService
 */
public interface AuthenticationInfo {

    /**
     *
     * @return
     *          The id of the currently authenticated user.
     */
    String getUserId();

}
