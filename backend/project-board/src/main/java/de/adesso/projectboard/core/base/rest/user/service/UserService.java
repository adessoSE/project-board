package de.adesso.projectboard.core.base.rest.user.service;

import de.adesso.projectboard.core.base.rest.exceptions.BookmarkNotFoundException;
import de.adesso.projectboard.core.base.rest.exceptions.ProjectNotFoundException;
import de.adesso.projectboard.core.base.rest.exceptions.UserNotFoundException;
import de.adesso.projectboard.core.base.rest.project.persistence.Project;
import de.adesso.projectboard.core.base.rest.project.persistence.ProjectRepository;
import de.adesso.projectboard.core.base.rest.security.AuthenticationInfo;
import de.adesso.projectboard.core.base.rest.user.application.persistence.ProjectApplication;
import de.adesso.projectboard.core.base.rest.user.persistence.SuperUser;
import de.adesso.projectboard.core.base.rest.user.persistence.User;
import de.adesso.projectboard.core.base.rest.user.persistence.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Wrapper for the {@link UserRepository} to manage {@link User}s.
 *
 * @see UserRepository
 * @see AuthenticationInfo
 */
@Service
public class UserService {

    private final UserRepository userRepo;

    private final ProjectRepository projectRepo;

    private final AuthenticationInfo authInfo;

    @Autowired
    public UserService(UserRepository userRepo,
                       ProjectRepository projectRepo,
                       AuthenticationInfo authInfo) {
        this.userRepo = userRepo;
        this.projectRepo = projectRepo;
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
     * {@link SuperUser#staffMembers}.
     *
     * @param user
     *          The {@link User} to remove.
     *
     * @see SuperUser#removeStaffMember(User)
     */
    public void delete(User user) {
        SuperUser boss = user.getBoss();
        boss.removeStaffMember(user);
        userRepo.save(boss);
    }
}
