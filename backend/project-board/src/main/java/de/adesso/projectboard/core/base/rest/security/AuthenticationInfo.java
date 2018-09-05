package de.adesso.projectboard.core.base.rest.security;

/**
 * Interface used in REST controllers to get user specific information.
 *
 * @see de.adesso.projectboard.core.rest.security.KeycloakAuthenticationInfo
 * @see de.adesso.projectboard.core.base.rest.application.ProjectApplicationController
 * @see de.adesso.projectboard.core.base.rest.bookmark.ProjectBookmarkController
 */
public interface AuthenticationInfo {

    /**
     *
     * @return
     *          The id of the currently authenticated user.
     */
    String getUserId();

}
