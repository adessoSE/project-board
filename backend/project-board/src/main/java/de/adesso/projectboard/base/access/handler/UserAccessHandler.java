package de.adesso.projectboard.base.access.handler;

import de.adesso.projectboard.base.access.rest.UserAccessController;
import de.adesso.projectboard.base.user.persistence.User;

/**
 * Interface used by {@link UserAccessController} to handle
 * user access grants.
 */
@FunctionalInterface
public interface UserAccessHandler {

    /**
     *
     * @param user
     *          The {@link User} who has been granted access.
     */
    void onAccessGranted(User user);

}
