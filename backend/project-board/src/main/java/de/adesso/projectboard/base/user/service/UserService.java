package de.adesso.projectboard.base.user.service;

import de.adesso.projectboard.base.access.service.UserAccessService;
import de.adesso.projectboard.base.exceptions.UserNotFoundException;
import de.adesso.projectboard.base.user.persistence.User;
import de.adesso.projectboard.base.user.persistence.data.UserData;
import de.adesso.projectboard.base.user.persistence.structure.OrganizationStructure;
import org.springframework.data.domain.Sort;

import java.util.*;

/**
 * Service interface to provide functionality to manage {@link User}s and their
 * corresponding data.
 *
 * @see UserAccessService
 */
public interface UserService {

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
     * @param user
     *          The {@link User}.
     *
     * @return
     *          {@code true}, iff the {@link User} is a manager.
     */
    boolean userIsManager(User user);

    /**
     *
     * @param user
     *          The {@code User} to get the {@link OrganizationStructure}
     *          for.
     *
     * @return
     *          The {@link OrganizationStructure} instance for the given
     *          {@code user}.
     */
    OrganizationStructure getStructureForUser(User user);

    /**
     *
     * @param user
     *          The {@link User} to get the {@link UserData} for.
     *
     * @return
     *          The {@link UserData user data} for the given {@code user}.
     */
    UserData getUserData(User user);

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
     *          When no {@link User} with the given {@code userId} was found.
     */
    User getUserById(String userId) throws UserNotFoundException;

    /**
     *
     * @param user
     *          The {@link User} to check.
     *
     * @param staffMember
     *          The {@link User} instance of the staff member.
     *
     * @return
     *          {@code true}, iff the {@code user} has the given {@code staffMember}
     *          as a staff member.
     */
    boolean userHasStaffMember(User user, User staffMember);

    /**
     *
     * @param user
     *          The {@link User} to get the manager of.
     *
     * @return
     *          The manager of the {@code user}.
     */
    User getManagerOfUser(User user);

    /**
     *
     * @param user
     *          The {@link User} to get the staff members'
     *          {@link UserData} of.
     *
     * @param sort
     *          The {@link Sort} instance to sort by.
     *
     * @return
     *          A {@link List} of the {@link UserData} belonging to
     *          the staff members.
     */
    List<UserData> getStaffMemberUserDataOfUser(User user, Sort sort);

    /**
     *
     * @param user
     *          The {@link User} to get the staff members of.
     *
     * @return
     *          The {@code user}'s staff members.
     */
    List<User> getStaffMembersOfUser(User user);

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

    /**
     *
     * @param userId
     *          The {@link User#id ID} of the {@link User} to delete.
     */
    void deleteUserById(String userId);

    /**
     * Method to validate the existence of a given {@link User}
     * instance.
     *
     * @param user
     *          The {@link User} to validate.
     *
     * @return
     *          The given {@code user}.
     *
     * @throws UserNotFoundException
     *          When the no {@link User} with the given {@code user}'s
     *          {@link User#id ID} exists.
     *
     * @see #userExists(String)
     */
    default User validateExistence(User user) throws UserNotFoundException {
        User givenUser = Objects.requireNonNull(user);

        if(userExists(givenUser.getId())) {
            return givenUser;
        } else {
            throw new UserNotFoundException();
        }
    }

    /**
     * Method similar to {@link #userIsManager(User)} but for
     * multiple {@link User}s at once.
     *
     * @param users
     *          The users to check.
     *
     * @return
     *          A {@link Map} that maps a {@link User} to
     *          a boolean value that indicates whether the user is
     *          a manager or not.
     *
     * @see #userIsManager(User)
     */
    default Map<User, Boolean> usersAreManagers(Set<User> users) {
        Objects.requireNonNull(users);

        Map<User, Boolean> returnMap = new HashMap<>();

        users.forEach(user -> returnMap.put(user, userIsManager(user)));

        return returnMap;
    }

}
