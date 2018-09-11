package de.adesso.projectboard.core.base.rest.user;

import de.adesso.projectboard.core.base.rest.user.application.persistence.ProjectApplication;
import de.adesso.projectboard.core.base.rest.exceptions.BookmarkNotFoundException;
import de.adesso.projectboard.core.base.rest.exceptions.ProjectNotFoundException;
import de.adesso.projectboard.core.base.rest.exceptions.UserNotFoundException;
import de.adesso.projectboard.core.base.rest.project.persistence.AbstractProject;
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
     * user. Creates a new (persisted) {@link User} object when no
     * {@link User} object exists for the current user.
     *
     * @return
     *          The {@link User} object of the currently
     *          authenticated user.
     *
     * @see AuthenticationInfo#getUserId()
     */
    public User getCurrentUser() {
        Optional<User> optionalUser = userRepo.findById(authInfo.getUserId());

        return optionalUser.orElseGet(() -> userRepo.save(new User(authInfo.getUserId())));
    }

    /**
     *
     * @return
     *          The user ID of the current user returned by
     *          {@link #getCurrentUser()}.
     *
     * @see #getCurrentUser()
     */
    public String getCurrentUserId() {
        return getCurrentUser().getId();
    }

    /**
     *
     * @param userId
     *          The id of the user.
     *
     * @return
     *          <i>true</i>, if the user with the given
     *          id exists, <i>false</i> otherwise.
     */
    public boolean userExists(String userId) {
        return userRepo.existsById(userId);
    }

    /**
     *
     * @param userId
     *          The id of the {@link User} to check for the bookmark.
     *
     * @param projectId
     *          The id of the {@link de.adesso.projectboard.core.base.rest.project.persistence.AbstractProject}
     *          the bookmark refers to.
     *
     * @return
     *          <i>true</i>, when a {@link AbstractProject} with the the given
     *          {@code projectId} exists and the user's {@link User#getBookmarks() bookmarks}
     *          contains the project.
     *          <br>
     *          <i>false</i> when the project/user with the given id does
     *          not exist or the the user's {@link User#getBookmarks() bookmarks} don't contain
     *          the project.
     *
     */
    public boolean userHasBookmark(String userId, long projectId) {
        Optional<User> userOptional = userRepo.findById(userId);

        if(userOptional.isPresent()) {
            Optional<AbstractProject> projectOptional = projectRepo.findById(projectId);

            if(projectOptional.isPresent()) {
                return userRepo.existsByIdAndBookmarksContaining(userId, projectOptional.get());
            }
        }

        return false;
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
     *          The id of the {@link AbstractProject} to add a bookmark for.
     *
     * @return
     *          The {@link AbstractProject} added to the bookmarks.
     *
     * @throws UserNotFoundException
     *          When no {@link User} is found for the given {@code userId}.
     *
     * @throws ProjectNotFoundException
     *          When no {@link AbstractProject} is found for the given {@code projectId}.
     *
     * @see #getUserById(String)
     */
    public AbstractProject addBookmarkToUser(String userId, long projectId) throws UserNotFoundException, ProjectNotFoundException {

        // get the user with the given id
        User user = getUserById(userId);

        // get the project with the given id
        Optional<AbstractProject> projectOptional = projectRepo.findById(projectId);
        if(!projectOptional.isPresent()) {
            throw new ProjectNotFoundException();
        }

        // add the project and persist the entity
        AbstractProject project = projectOptional.get();

        user.addBookmark(project);
        userRepo.save(user);

        return project;
    }

    /**
     * Adds the given {@link ProjectApplication} to the user and persists the
     * updated entity.
     *
     * <p>
     *     <b>Note:</b> The {@code application} gets persisted as well.
     * </p>
     *
     * @param userId
     *          The id of the {@link User} to add the application to.
     *
     * @param application
     *          The {@link ProjectApplication} to add to the user.
     *
     * @return
     *          The application <b>after</b> persisting it.
     *
     * @throws UserNotFoundException
     *          When no {@link User} is found for the given {@code userId}.
     *
     * @throws IllegalArgumentException
     *          When the application's {@link ProjectApplication#getUser() user id} does not match
     *          the given {@code userId}.
     *
     * @see #getUserById(String)
     */
    public ProjectApplication addApplicationToUser(String userId, ProjectApplication application) throws UserNotFoundException, IllegalArgumentException {

        // check integrity
        if(!application.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("The application can only be added to the user with the same id!");
        }

        // persist the application
        ProjectApplication savedApplication = applicationRepo.save(application);

        // add the application and update the user
        User user = getUserById(userId);
        user.addApplication(savedApplication);
        userRepo.save(user);

        return savedApplication;
    }

    /**
     * Removes a bookmarked {@link AbstractProject} from the users bookmarks
     * and persists the updated entity.
     *
     * @param userId
     *          The id of the {@link User} to remove the bookmark from.
     *
     * @param projectId
     *          The id of the bookmarked {@link AbstractProject}.
     *
     * @throws UserNotFoundException
     *          When no {@link User} is found for the given {@code userId}.
     *
     * @throws ProjectNotFoundException
     *          When no {@link AbstractProject} is found for the given {@code projectId}.
     *
     * @throws BookmarkNotFoundException
     *          When the user has not bookmarked the {@link AbstractProject}.
     *
     * @see #getUserById(String)
     */
    public void removeBookmarkFromUser(String userId, long projectId) throws UserNotFoundException, ProjectNotFoundException, BookmarkNotFoundException {

        // get the user with the given id
        User user = getUserById(userId);

        // get the project with the given id
        Optional<AbstractProject> projectOptional = projectRepo.findById(projectId);
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

}
