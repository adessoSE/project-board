package de.adesso.projectboard.ldap.user;

import de.adesso.projectboard.base.exceptions.BookmarkNotFoundException;
import de.adesso.projectboard.base.exceptions.ProjectNotFoundException;
import de.adesso.projectboard.base.exceptions.UserNotFoundException;
import de.adesso.projectboard.base.project.persistence.Project;
import de.adesso.projectboard.base.project.service.ProjectServiceImpl;
import de.adesso.projectboard.base.user.persistence.User;
import de.adesso.projectboard.base.user.persistence.UserRepository;
import de.adesso.projectboard.base.user.service.BookmarkService;
import de.adesso.projectboard.base.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link Service} to to provide functionality to manage {@link Project Project Bookmarks}.
 *
 * @see LdapUserService
 * @see ProjectServiceImpl
 */
@Service
public class BookmarkServiceImpl implements BookmarkService {

    private final UserRepository userRepo;

    private final UserService userService;

    private final ProjectServiceImpl projectService;

    @Autowired
    public BookmarkServiceImpl(UserRepository userRepo,
                               UserService userService,
                               ProjectServiceImpl projectService) {
        this.userRepo = userRepo;
        this.userService = userService;
        this.projectService = projectService;
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
     */
    @Override
    public Project addBookmarkToUser(String userId, String projectId) throws UserNotFoundException, ProjectNotFoundException {

        // get the user/project with the given id
        User user = userService.getUserById(userId);
        Project project = projectService.getProjectById(projectId);

        // add the project and persist the entity
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
     */
    @Override
    public void removeBookmarkFromUser(String userId, String projectId) throws UserNotFoundException, ProjectNotFoundException, BookmarkNotFoundException {

        // get the user/project with the given id
        User user = userService.getUserById(userId);
        Project project = projectService.getProjectById(projectId);

        if(userRepo.existsByIdAndBookmarksContaining(userId, project)) {

            // remove the bookmark and update the entity
            user.removeBookmark(project);
            userRepo.save(user);

        } else {
            throw new BookmarkNotFoundException();
        }
    }

    /**
     *
     * @param userId
     *          The id of the {@link User} to get the bookmarks from.
     *
     * @return
     *          The bookmarked {@link Project}s of the user.
     *
     * @throws UserNotFoundException
     *          When no {@link User} with the given {@code userId} was found.
     *
     * @see LdapUserService#getUserById(String)
     */
    @Override
    public List<Project> getBookmarksOfUser(String userId) throws UserNotFoundException {
        return new ArrayList<>(userService.getUserById(userId).getBookmarks());
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
     * @see UserRepository#existsByIdAndBookmarksContaining(String, Project)
     */
    @Override
    public boolean userHasBookmark(String userId, String projectId) {
        if(projectService.projectExists(projectId)) {
            Project project = projectService.getProjectById(projectId);

            return userRepo.existsByIdAndBookmarksContaining(userId, project);
        }

        return false;
    }

}
