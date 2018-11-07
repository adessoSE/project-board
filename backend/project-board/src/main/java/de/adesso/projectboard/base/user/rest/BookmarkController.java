package de.adesso.projectboard.base.user.rest;

import de.adesso.projectboard.base.access.rest.UserAccessController;
import de.adesso.projectboard.base.application.rest.ApplicationController;
import de.adesso.projectboard.base.project.persistence.Project;
import de.adesso.projectboard.base.project.rest.ProjectController;
import de.adesso.projectboard.base.user.bookmark.dto.BookmarkRequestDTO;
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

    @PreAuthorize("hasPermissionToAccessUser(#userId) || hasRole('admin')")
    @DeleteMapping(value = "/{userId}/bookmarks/{projectId}")
    public void deleteBookmarkOfUser(@PathVariable("userId") String userId, @PathVariable("projectId") String projectId) {
        bookmarkService.removeBookmarkOfUser(userId, projectId);
    }

    @PreAuthorize("(hasPermissionToAccessUser(#userId) && hasAccessToProjects()) || hasRole('admin')")
    @PostMapping(value = "/{userId}/bookmarks")
    public Project createBookmarkForUser(@Valid @RequestBody BookmarkRequestDTO bookmarkRequestDTO, @PathVariable("userId") String userId) {
        return bookmarkService.addBookmarkToUser(userId, bookmarkRequestDTO.getProjectId());
    }

    @PreAuthorize("hasPermissionToAccessUser(#userId) || hasRole('admin')")
    @GetMapping(path = "/{userId}/bookmarks")
    public Iterable<Project> getBookmarksOfUser(@PathVariable("userId") String userId) {
        return bookmarkService.getBookmarksOfUser(userId);
    }

}
