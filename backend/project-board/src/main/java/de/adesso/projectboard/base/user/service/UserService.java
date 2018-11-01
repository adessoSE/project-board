package de.adesso.projectboard.base.user.service;

import de.adesso.projectboard.base.access.service.UserAccessService;
import de.adesso.projectboard.base.exceptions.UserNotFoundException;
import de.adesso.projectboard.base.user.persistence.User;
import de.adesso.projectboard.base.user.persistence.data.UserData;
import de.adesso.projectboard.base.user.persistence.structure.OrganizationStructure;
import de.adesso.projectboard.base.util.Sort;

import java.util.List;

/**
 * Service interface to provide functionality to manage {@link User}s and their
 * corresponding data.
 *
 * @see UserAccessService
 */
public interface UserService {

    /**
     *
     * @return
     *          The currently authenticated {@link User}.
     *
     * @throws UserNotFoundException
     *          When the authenticated user does not have a
     *          corresponding {@link User} instance.
     */
    User getAuthenticatedUser() throws UserNotFoundException;

    /**
     *
     * @return
     *          The currently authenticated {@link User}'s
     *          {@link User#id ID}.
     */
    String getAuthenticatedUserId();

    /**
     *
     * @param userId
     *          The {@link User#id ID} of the {@link User}.
     *
     * @return
     *          {@code true}, iff a {@link User} instance with the
     *          given {@code userId} exists.
     */
    boolean userExists(String userId);

    /**
     *
     * @param userId
     *          The {@link User#id ID} of the {@link User}.
     *
     * @return
     *          {@code true}, iff a {@link User} with the given {@code userId}
     *          exists and is a manager.
     */
    boolean isManager(String userId);

    /**
     *
     * @param userId
     *          The {@link User#id ID} of the {@link User}.
     *
     * @return
     *          The {@link OrganizationStructure} instance for the {@link User}
     *          with the given {@code userId}.
     *
     * @throws UserNotFoundException
     *          When no {@link User} with the given {@code userId} was found.
     */
    OrganizationStructure getStructureForUser(String userId) throws UserNotFoundException;

    /**
     *
     * @param userId
     *          The {@link User#id ID} of the {@link User}.
     *
     * @return
     *          The {@link UserData user data} for the {@link User}
     *          with the given {@code userId}.
     *
     * @throws UserNotFoundException
     *          When no {@link User} with the given {@code userId} was found.
     */
    UserData getUserData(String userId) throws UserNotFoundException;

    /**
     *
     * @param userId
     *          The {@link User#id ID} of the {@link User}.
     *
     * @return
     *          The corresponding {@link User} instance iff
     *          {@link #userExists(String)} returns {@code true}.
     *
     * @throws UserNotFoundException
     *          When no {@link User} with the given {@code userId}
     *          was found.
     */
    User getUserById(String userId) throws UserNotFoundException;

    /**
     *
     * @param userId
     *          The {@link User#id ID} of the {@link User} to check.
     *
     * @param staffId
     *          The {@link User#id ID} of the staff member.
     *
     * @return
     *          {@code true}, iff the {@link User} with the given
     *          {@code userId} has a staff member with the given
     *          {@code staffId}.
     */
    boolean userHasStaffMember(String userId, String staffId);

    /**
     *
     * @param userId
     *          The {@link User#id ID} of the {@link User} to get the
     *          staff members of.
     *
     * @param sorts
     *          A {@link List} of {@link Sort} instances to sort by.
     *
     * @return
     *          A {@link List} of staff members belonging to the {@link User}.
     *
     * @throws UserNotFoundException
     *          When no {@link User} with the given {@code userId}
     *          was found.
     */
    List<User> getStaffMembersOfUser(String userId, List<Sort> sorts) throws UserNotFoundException;

    /**
     *
     * @param user
     *          The {@link User} to save.
     *
     * @return
     *          The saved user instance.
     */
    User save(User user);

    /**
     *
     * @param user
     *          The {@link User} to delete.
     */
    void delete(User user);



}
