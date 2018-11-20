package de.adesso.projectboard.base.user.rest;

import de.adesso.projectboard.base.access.rest.UserAccessController;
import de.adesso.projectboard.base.application.rest.ApplicationController;
import de.adesso.projectboard.base.project.persistence.Project;
import de.adesso.projectboard.base.project.rest.NonPageableProjectController;
import de.adesso.projectboard.base.project.service.ProjectService;
import de.adesso.projectboard.base.user.bookmark.dto.BookmarkRequestDTO;
import de.adesso.projectboard.base.user.persistence.User;
import de.adesso.projectboard.base.user.service.BookmarkService;
import de.adesso.projectboard.base.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * {@link RestController REST Controller} to manage {@link Project} bookmarks.
 *
 * @see NonPageableProjectController
 * @see ApplicationController
 * @see UserAccessController
 * @see UserController
 */
@RestController
@RequestMapping("/users")
public class BookmarkController {

    private final UserService userService;

    private final ProjectService projectService;

    private final BookmarkService bookmarkService;

    @Autowired
    public BookmarkController(UserService userService,
                              ProjectService projectService,
                              BookmarkService bookmarkService) {
        this.userService = userService;
        this.projectService = projectService;
        this.bookmarkService = bookmarkService;
    }

    @PreAuthorize("hasPermissionToAccessUser(#userId) || hasRole('admin')")
    @DeleteMapping(value = "/{userId}/bookmarks/{projectId}")
    public void deleteBookmarkOfUser(@PathVariable("userId") String userId, @PathVariable("projectId") String projectId) {
        User user = userService.getUserById(userId);
        Project project = projectService.getProjectById(projectId);

        bookmarkService.removeBookmarkOfUser(user, project);
    }

    @PreAuthorize("(hasPermissionToAccessUser(#userId) && hasAccessToProjects()) || hasRole('admin')")
    @PostMapping(value = "/{userId}/bookmarks")
    public Project createBookmarkForUser(@Valid @RequestBody BookmarkRequestDTO bookmarkRequestDTO, @PathVariable("userId") String userId) {
        User user = userService.getUserById(userId);
        Project project = projectService.getProjectById(bookmarkRequestDTO.getProjectId());

        return bookmarkService.addBookmarkToUser(user, project);
    }

    @PreAuthorize("hasPermissionToAccessUser(#userId) || hasRole('admin')")
    @GetMapping(path = "/{userId}/bookmarks")
    public Iterable<Project> getBookmarksOfUser(@PathVariable("userId") String userId) {
        User user = userService.getUserById(userId);

        return bookmarkService.getBookmarksOfUser(user);
    }

}
