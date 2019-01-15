package de.adesso.projectboard.base.user.service;

import de.adesso.projectboard.ad.project.service.RepositoryProjectService;
import de.adesso.projectboard.base.exceptions.BookmarkNotFoundException;
import de.adesso.projectboard.base.project.persistence.Project;
import de.adesso.projectboard.base.user.persistence.User;

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
     * @param user
     *          The {@link User}.
     *
     * @param project
     *          The {@link Project} to add a bookmark for.
     *
     * @return
     *          The bookmarked {@link Project}.
     */
    Project addBookmarkToUser(User user, Project project);

    /**
     *  @param user
     *          The {@link User}.
     *
     * @param project
     *          The {@link Project} to remove the bookmark of.
     *
     * @throws BookmarkNotFoundException
     *          When the given {@code user} has not bookmarked the given
     *          {@code project}.
     */
    void removeBookmarkOfUser(User user, Project project) throws BookmarkNotFoundException;

    /**
     *
     * @param user
     *          The {@link User}.
     *
     * @return
     *          A {@link List} of the bookmarked {@link Project}s
     *          of the given {@code user}.
     */
    List<Project> getBookmarksOfUser(User user);

    /**
     *
     * @param user
     *          The {@link User}.
     *
     * @param project
     *          The {@link Project}.
     *
     * @return
     *          {@code true}, iff the user has bookmarked the given
     *          {@code project}.
     */
    boolean userHasBookmark(User user, Project project);

}
