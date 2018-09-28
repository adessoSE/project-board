package de.adesso.projectboard.core.base.rest.user.persistence;

import de.adesso.projectboard.core.base.rest.user.application.persistence.ProjectApplication;
import de.adesso.projectboard.core.base.rest.exceptions.BookmarkNotFoundException;
import de.adesso.projectboard.core.base.rest.exceptions.ProjectNotFoundException;
import de.adesso.projectboard.core.base.rest.exceptions.UserNotFoundException;
import de.adesso.projectboard.core.base.rest.project.persistence.Project;
import de.adesso.projectboard.core.base.rest.project.persistence.ProjectRepository;
import de.adesso.projectboard.core.base.rest.security.AuthenticationInfo;
import de.adesso.projectboard.core.base.rest.user.application.persistence.ProjectApplicationRepository;
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

    private final ProjectApplicationRepository applicationRepo;

    private final AuthenticationInfo authInfo;

    @Autowired
    public UserService(UserRepository userRepo,
                       ProjectRepository projectRepo,
                       ProjectApplicationRepository applicationRepo,
                       AuthenticationInfo authInfo) {
        this.userRepo = userRepo;
        this.projectRepo = projectRepo;
        this.applicationRepo = applicationRepo;
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
     *          The id of the {@link User} to check for the bookmark.
     *
     * @param projectId
     *          The id of the {@link Project}
     *          the bookmark refers to.
     *
     * @return
     *          {@code true}, when a {@link Project} with the the given
     *          {@code projectId} exists and the user's {@link User#getBookmarks() bookmarks}
     *          contains the project.
     *          <br>
     *          {@code false} when the project/user with the given id does
     *          not exist or the the user's {@link User#getBookmarks() bookmarks} don't contain
     *          the project.
     *
     */
    public boolean userHasBookmark(String userId, String projectId) {
        Optional<User> userOptional = userRepo.findById(userId);

        if(userOptional.isPresent()) {
            Optional<Project> projectOptional = projectRepo.findById(projectId);

            if(projectOptional.isPresent()) {
                return userRepo.existsByIdAndBookmarksContaining(userId, projectOptional.get());
            }
        }

        return false;
    }

    /**
     *
     * @param userId
     *          The id of the {@link User}.
     *
     * @param project
     *          The {@link Project}.
     *
     * @return
     *          {@code true}, when the user with the given {@code userId}
     *          has a {@link ProjectApplication} that {@link ProjectApplication#getProject() references}
     *          the {@code project}.
     *
     * @throws UserNotFoundException
     *          When no user with the given {@code userId} is found.
     *
     */
    public boolean userHasAppliedForProject(String userId, Project project) throws UserNotFoundException {
        User user = getUserById(userId);
        return user.getApplications().stream()
                .anyMatch(application -> application.getProject().equals(project));
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
     * Adds a new bookmark to the {@link User#getBookmarks() user's bookmarks} and
     * persists the updated entity.
     *
     * @param userId
     *          The id of the {@link User} to add the bookmark to.
     *
     * @param projectId
     *          The id of the {@link Project} to add a bookmark for.
     *
     * @return
     *          The {@link Project} added to the bookmarks.
     *
     * @throws UserNotFoundException
     *          When no {@link User} is found for the given {@code userId}.
     *
     * @throws ProjectNotFoundException
     *          When no {@link Project} is found for the given {@code projectId}.
     *
     * @see #getUserById(String)
     */
    public Project addBookmarkToUser(String userId, String projectId) throws UserNotFoundException, ProjectNotFoundException {

        // get the user with the given id
        User user = getUserById(userId);

        // get the project with the given id
        Optional<Project> projectOptional = projectRepo.findById(projectId);
        if(!projectOptional.isPresent()) {
            throw new ProjectNotFoundException();
        }

        // add the project and persist the entity
        Project project = projectOptional.get();

        user.addBookmark(project);
        userRepo.save(user);

        return project;
    }

    /**
     * Removes a bookmarked {@link Project} from the users bookmarks
     * and persists the updated entity.
     *
     * @param userId
     *          The id of the {@link User} to remove the bookmark from.
     *
     * @param projectId
     *          The id of the bookmarked {@link Project}.
     *
     * @throws UserNotFoundException
     *          When no {@link User} is found for the given {@code userId}.
     *
     * @throws ProjectNotFoundException
     *          When no {@link Project} is found for the given {@code projectId}.
     *
     * @throws BookmarkNotFoundException
     *          When the user has not bookmarked the {@link Project}.
     *
     * @see #getUserById(String)
     */
    public void removeBookmarkFromUser(String userId, String projectId) throws UserNotFoundException, ProjectNotFoundException, BookmarkNotFoundException {

        // get the user with the given id
        User user = getUserById(userId);

        // get the project with the given id
        Optional<Project> projectOptional = projectRepo.findById(projectId);
        if(!projectOptional.isPresent()) {
            throw new ProjectNotFoundException();
        }

        if(userRepo.existsByIdAndBookmarksContaining(userId, projectOptional.get())) {

            // remove the bookmark and update the entity
            user.removeBookmark(projectOptional.get());
            userRepo.save(user);

        } else {
            throw new BookmarkNotFoundException();
        }
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
