package de.adesso.projectboard.base.access.service;


import de.adesso.projectboard.base.access.persistence.AccessInterval;
import de.adesso.projectboard.base.user.persistence.User;
import de.adesso.projectboard.base.user.service.UserService;

import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDateTime;

/**
 * Service interface to provide functionality to manage access of {@link User}s.
 *
 * @see UserService
 */
public interface UserAccessService {

    /**
     *
     * @param user
     *          The {@link User} to give access to.
     *
     * @param until
     *          The {@link LocalDateTime} until the user should have access.
     *
     * @return
     *          The {@link User}.
     *
     * @throws IllegalArgumentException
     *          When the given {@link LocalDateTime} is {@link LocalDateTime#isBefore(ChronoLocalDateTime) before}
     *          the current time.
     *
     */
    User giveUserAccessUntil(User user, LocalDateTime until) throws IllegalArgumentException;

    /**
     *
     * @param user
     *          The {@link User} to remove access from.
     *
     * @return
     *          The {@link User}.
     */
    User removeAccessFromUser(User user);

    /**
     *
     * @param user
     *          The {@link User}.
     *
     * @return
     *          {@code true}, iff the given {@link User}'s latest
     *          {@link AccessInterval} instance
     *          is currently active (the current time is in the interval {@code [start, end]}).
     */
    boolean userHasActiveAccessInterval(User user);

}
