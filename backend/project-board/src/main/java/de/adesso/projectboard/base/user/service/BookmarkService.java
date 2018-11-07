package de.adesso.projectboard.base.user.service;

import de.adesso.projectboard.base.exceptions.ProjectNotFoundException;
import de.adesso.projectboard.base.exceptions.UserNotFoundException;
import de.adesso.projectboard.base.project.persistence.Project;
import de.adesso.projectboard.base.user.persistence.User;
import de.adesso.projectboard.project.service.RepositoryProjectService;

import java.util.List;

/**
 * Service interface to provide functionality to manage bookmarks
 * of {@link User}s.
 *
 * @see UserService
 * @see RepositoryProjectService
 */
public interface BookmarkService {

    /**
     *
     * @param userId
     *          The {@link User#id ID} of the {@link User}.
     *
     * @param projectId
     *          The {@link Project#id ID} of the {@link Project}
     *          to add a bookmark for.
     *
     * @return
     *          The bookmarked {@link Project}.
     *
     * @throws UserNotFoundException
     *          When no {@link User} with the given {@code userId}
     *          was found.
     *
     * @throws ProjectNotFoundException
     *          When no {@link Project} with the given {@code projectId}
     *          was found.
     */
    Project addBookmarkToUser(String userId, String projectId) throws UserNotFoundException, ProjectNotFoundException;

    /**
     *
     * @param userId
     *          The {@link User#id ID} of the {@link User}.
     *
     * @param projectId
     *          The {@link Project#id ID} of the {@link Project} to
     *          remove the bookmark of.
     *
     * @throws UserNotFoundException
     *          When no {@link User} with the given {@code userId}
     *          was found.
     *
     * @throws ProjectNotFoundException
     *          When no {@link Project} with the given {@code projectId}
     *          was found.
     */
    void removeBookmarkOfUser(String userId, String projectId) throws UserNotFoundException, ProjectNotFoundException;

    /**
     *
     * @param userId
     *          The {@link User#id ID} of the {@link User}.
     *
     * @return
     *          A {@link List} of the bookmarked {@link Project}s
     *          of the user.
     *
     * @throws UserNotFoundException
     *          When no {@link User} with the given {@code userId}
     *          was found.
     */
    List<Project> getBookmarksOfUser(String userId) throws UserNotFoundException;

    /**
     *
     * @param userId
     *          The {@link User#id ID} of the {@link User}.
     *
     * @param projectId
     *          The {@link Project#id ID} of the {@link Project}.
     *
     * @return
     *          {@code true}, iff the user has bookmarked a project
     *          with the given {@code projectId}.
     */
    boolean userHasBookmark(String userId, String projectId);

}
