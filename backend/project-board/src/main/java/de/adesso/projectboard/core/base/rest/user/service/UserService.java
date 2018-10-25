package de.adesso.projectboard.core.base.rest.user.service;

import de.adesso.projectboard.core.base.rest.exceptions.UserNotFoundException;
import de.adesso.projectboard.core.base.rest.security.AuthenticationInfo;
import de.adesso.projectboard.core.base.rest.user.persistence.SuperUser;
import de.adesso.projectboard.core.base.rest.user.persistence.User;
import de.adesso.projectboard.core.base.rest.user.persistence.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * {@link Service} to to provide functionality to manage {@link User}s.
 *
 * @see UserRepository
 * @see AuthenticationInfo
 */
@Service
public class UserService {

    private final UserRepository userRepo;

    private final AuthenticationInfo authInfo;

    @Autowired
    public UserService(UserRepository userRepo, AuthenticationInfo authInfo) {
        this.userRepo = userRepo;
        this.authInfo = authInfo;
    }

    /**
     * Returns the {@link User} object of the currently authenticated
     * user.
     *
     * @return
     *          The result of {@link #getUserById(String)} with the
     *          returned value of {@link AuthenticationInfo#getUserId()}
     *          as the {@code userId}.
     *
     * @throws UserNotFoundException
     *          When no user was found.
     *
     * @see AuthenticationInfo#getUserId()
     * @see #getUserById(String)
     */
    public User getCurrentUser() throws UserNotFoundException {
        return getUserById(authInfo.getUserId());
    }

    /**
     *
     * @return
     *          The user ID of the currently authenticated user.
     *
     * @see AuthenticationInfo#getUserId()
     */
    public String getCurrentUserId() {
        return authInfo.getUserId();
    }

    /**
     *
     * @param userId
     *          The id of the user.
     *
     * @return
     *          {@code true}, if the user with the given
     *          id exists, {@code false} otherwise.
     */
    public boolean userExists(String userId) {
        return userRepo.existsById(userId);
    }

    /**
     *
     * @return
     *          A {@link Iterable} of all {@link User}s
     */
    public Iterable<User> getAllUsers() {
        return userRepo.findAll();
    }

    /**
     *
     * @param userId
     *          The id of the user to retrieve.
     *
     * @return
     *          The {@link User} with the given id.
     *
     * @throws UserNotFoundException
     *          When no {@link User} with the given id is found.
     *
     * @see UserRepository#findById(Object)
     */
    public User getUserById(String userId) throws UserNotFoundException {
        Optional<User> userOptional = userRepo.findById(userId);

        if(!userOptional.isPresent()) {
            throw new UserNotFoundException();
        }

        return userOptional.get();
    }

    /**
     *
     * @param user
     *          The {@link SuperUser boss} to check for the {@link User staff member}.
     *
     * @param staffMemberId
     *          The id of the {@link User staff member}.
     *
     * @return
     *          {@code true}, when a {@link User} with the given {@code user}
     *          as the {@link User#boss} exists, {@code false} otherwise.
     *
     * @see UserRepository#existsByIdAndBoss(String, SuperUser)
     */
    public boolean userHasStaffMember(SuperUser user, String staffMemberId) {
        return userRepo.existsByIdAndBoss(staffMemberId, user);
    }

    /**
     *
     * @param user
     *          The {@link User} to get the {@link User staff members}
     *          of.
     *
     * @param sort
     *          The {@link Sort} to apply.
     *
     * @return
     *          A {@link List} of all {@link User}s who's {@link User#boss} is equal
     *          to the given {@code user} in case the {@code user} is a {@link SuperUser}
     *          or a empty {@link List} returned by {@link Collections#emptyList()}. Sorted
     *          accordingly.
     *
     * @see UserRepository#findAllByBossEquals(SuperUser, Sort)
     */
    public List<User> getStaffMembersOfUser(User user, Sort sort) {
        if(user instanceof SuperUser) {
            return userRepo.findAllByBossEquals((SuperUser) user, sort);
        }

        return Collections.emptyList();
    }

    /**
     *
     * @param user
     *          The {@link User} to persist.
     *
     * @return
     *          The result of {@link UserRepository#save(Object)}.
     */
    public User save(User user) {
        return userRepo.save(user);
    }

    /**
     * Deletes a {@link User} from the database by removing it from the boss'
     * {@link SuperUser#staffMembers} and deleting the user.
     *
     * @param user
     *          The {@link User} to remove.
     *
     * @see SuperUser#removeStaffMember(User)
     */
    public void delete(User user) {
        if(!user.getBoss().equals(user)) {
            SuperUser boss = user.getBoss();
            boss.removeStaffMember(user);
            userRepo.delete(user);
            userRepo.save(boss);
        } else {
            SuperUser boss = user.getBoss();
            boss.removeStaffMember(user);
            userRepo.delete(user);
        }
    }

}
