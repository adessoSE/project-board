package de.adesso.projectboard.base.access.handler;

import de.adesso.projectboard.base.access.persistence.AccessInterval;
import de.adesso.projectboard.base.access.rest.UserAccessController;
import de.adesso.projectboard.base.user.persistence.User;

/**
 * Interface used by {@link UserAccessController} to handle user access
 * grants.
 */
public interface UserAccessEventHandler {

    /**
     *
     * @param user
     *          The {@link User} who has been granted access.
     *
     * @param accessInterval
     *          The corresponding {@link AccessInterval}.
     */
    void onAccessCreated(User user, AccessInterval accessInterval);

    /**
     *
     * @param user
     *          The {@link User} the access was changed of.
     *
     * @param accessInterval
     *          The updated {@link AccessInterval} of the user.
     */
    void onAccessChanged(User user, AccessInterval accessInterval);

    /**
     *
     * @param user
     *          The {@link User} the access got revoked of.
     */
    void onAccessRevoked(User user);

}
