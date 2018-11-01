package de.adesso.projectboard.base.access.service;


import de.adesso.projectboard.base.access.persistence.AccessInfo;
import de.adesso.projectboard.base.exceptions.UserNotFoundException;
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
     * @param userId
     *          The {@link User#id ID} of the {@link User} to give access to.
     *
     * @param until
     *          The {@link LocalDateTime} until the user should have access.
     *
     * @return
     *          The {@link AccessInfo} instance.
     *
     * @throws IllegalArgumentException
     *          When the given {@link LocalDateTime} is {@link LocalDateTime#isBefore(ChronoLocalDateTime) before}
     *          the current time.
     */
    AccessInfo giveUserAccessUntil(String userId, LocalDateTime until) throws IllegalArgumentException;

    /**
     *
     * @param userId
     *          The {@link User#id ID} of the {@link User} to remove access from.
     *
     * @throws UserNotFoundException
     *          When no {@link User} with the given {@code userId} was found.
     */
    void removeAccessFromUser(String userId) throws UserNotFoundException;

    /**
     *
     * @param userId
     *          The {@link User#id ID} of the {@link User}.
     *
     * @return
     *          {@code true}, iff the user has access.
     */
    boolean userHasAccess(String userId);

}
