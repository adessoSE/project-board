package de.adesso.projectboard.base.user.rest;

import de.adesso.projectboard.base.access.rest.UserAccessController;
import de.adesso.projectboard.base.application.rest.ApplicationController;
import de.adesso.projectboard.base.exceptions.BookmarkNotFoundException;
import de.adesso.projectboard.base.exceptions.ProjectNotFoundException;
import de.adesso.projectboard.base.exceptions.UserNotFoundException;
import de.adesso.projectboard.base.project.persistence.Project;
import de.adesso.projectboard.base.project.rest.ProjectController;
import de.adesso.projectboard.base.user.bookmark.dto.BookmarkRequestDTO;
import de.adesso.projectboard.base.user.persistence.User;
import de.adesso.projectboard.base.user.service.BookmarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * {@link RestController REST Controller} to manage {@link Project} bookmarks.
 *
 * @see ProjectController
 * @see ApplicationController
 * @see UserAccessController
 * @see UserController
 */
@RestController
@RequestMapping("/users")
public class BookmarkController {

    private final BookmarkService bookmarkService;

    @Autowired
    public BookmarkController(BookmarkService bookmarkService) {
        this.bookmarkService = bookmarkService;
    }

    /**
     *
     * @param userId
     *          The id of the {@link User} to delete the bookmark from.
     *
     * @param projectId
     *          The id of the {@link Project} to remove the bookmark from.
     *
     * @throws UserNotFoundException
     *          When no {@link User} with the given {@code userId} is found.
     *
     * @throws ProjectNotFoundException
     *          When no {@link Project} with the given {@code projectId} is found.
     *
     * @throws BookmarkNotFoundException
     *          When no the user has no bookmark for this project.
     *
     * @see BookmarkService#removeBookmarkFromUser(String, String)
     */
    @PreAuthorize("hasPermissionToAccessUser(#userId) || hasRole('admin')")
    @DeleteMapping(value = "/{userId}/bookmarks/{projectId}")
    public void deleteBookmarkOfUser(@PathVariable("userId") String userId, @PathVariable("projectId") String projectId)
            throws UserNotFoundException, ProjectNotFoundException, BookmarkNotFoundException {
        bookmarkService.removeBookmarkFromUser(userId, projectId);
    }

    /**
     *
     * @param bookmarkRequestDTO
     *          The {@link BookmarkRequestDTO} sent by the user.
     *
     * @return
     *          The {@link Project} bookmarked.
     *
     * @throws ProjectNotFoundException
     *          When no {@link Project project} is found for the
     *          given {@link BookmarkRequestDTO#getProjectId() id}.
     *
     * @see BookmarkService#addBookmarkToUser(String, String)
     */
    @PreAuthorize("(hasPermissionToAccessUser(#userId) && hasAccessToProjects()) || hasRole('admin')")
    @PostMapping(value = "/{userId}/bookmarks")
    public Project createBookmarkForUser(@Valid @RequestBody BookmarkRequestDTO bookmarkRequestDTO, @PathVariable("userId") String userId)
            throws ProjectNotFoundException {
        return bookmarkService.addBookmarkToUser(userId, bookmarkRequestDTO.getProjectId());
    }

    /**
     *
     * @param userId
     *          The id of the {@link User} to get the bookmarked projects
     *          of.
     *
     * @return
     *          A {@link Iterable} of all bookmarked {@link Project}s of the
     *          user.
     *
     * @throws UserNotFoundException
     *          When no {@link User} with the given {@code userId} is found.
     *
     * @see BookmarkService#getBookmarksOfUser(String)
     */
    @PreAuthorize("hasPermissionToAccessUser(#userId) || hasRole('admin')")
    @GetMapping(path = "/{userId}/bookmarks")
    public Iterable<Project> getBookmarksOfUser(@PathVariable("userId") String userId) throws UserNotFoundException {
        return bookmarkService.getBookmarksOfUser(userId);
    }

}
