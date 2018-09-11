package de.adesso.projectboard.core.base.rest.security;

/**
 * Interface used by the {@link de.adesso.projectboard.core.base.rest.user.UserService} to
 * get the id of the currently authenticated user.
 *
 * @see de.adesso.projectboard.core.rest.security.KeycloakAuthenticationInfo
 * @see de.adesso.projectboard.core.base.rest.user.UserService
 */
public interface AuthenticationInfo {

    /**
     *
     * @return
     *          The id of the currently authenticated user.
     */
    String getUserId();

}
