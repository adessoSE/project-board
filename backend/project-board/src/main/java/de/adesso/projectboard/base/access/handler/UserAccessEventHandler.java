package de.adesso.projectboard.base.access.handler;

import de.adesso.projectboard.base.access.persistence.AccessInterval;
import de.adesso.projectboard.base.access.rest.UserAccessController;
import de.adesso.projectboard.base.user.persistence.User;

import java.time.LocalDateTime;

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
     *          The {@link User} the access duration has changed of.
     *
     * @param accessInterval
     *          The changed access interval.
     *
     * @param previousEndTime
     *          The previous access end time.
     */
    void onAccessChanged(User user, AccessInterval accessInterval, LocalDateTime previousEndTime);

    /**
     *
     * @param user
     *          The {@link User} the access got revoked of.
     *
     * @param previousEndTime
     *          The previous access end time.
     */
    void onAccessRevoked(User user, LocalDateTime previousEndTime);

}
