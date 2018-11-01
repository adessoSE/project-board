package de.adesso.projectboard.base.security;

import de.adesso.projectboard.base.user.service.UserServiceImpl;

/**
 * Interface used by the {@link UserServiceImpl} to get the id of the currently authenticated user.
 *
 * @see de.adesso.projectboard.rest.security.KeycloakAuthenticationInfo
 * @see UserServiceImpl
 */
public interface AuthenticationInfo {

    /**
     *
     * @return
     *          The id of the currently authenticated user.
     */
    String getUserId();

}
