package de.adesso.projectboard.core.base.rest.security;

import de.adesso.projectboard.core.base.rest.user.application.ProjectApplicationController;
import de.adesso.projectboard.core.base.rest.user.bookmark.ProjectBookmarkController;

/**
 * Interface used in REST controllers to get user specific information.
 *
 * @see de.adesso.projectboard.core.rest.security.KeycloakAuthenticationInfo
 * @see ProjectApplicationController
 * @see ProjectBookmarkController
 */
public interface AuthenticationInfo {

    /**
     *
     * @return
     *          The id of the currently authenticated user.
     */
    String getUserId();

}
