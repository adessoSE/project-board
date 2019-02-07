package de.adesso.projectboard.base.user.rest;

import de.adesso.projectboard.base.project.projection.FullProjectProjection;
import de.adesso.projectboard.base.project.projection.ReducedProjectProjection;
import de.adesso.projectboard.base.project.service.ProjectService;
import de.adesso.projectboard.base.projection.BaseProjectionFactory;
import de.adesso.projectboard.base.user.bookmark.payload.BookmarkPayload;
import de.adesso.projectboard.base.user.service.BookmarkService;
import de.adesso.projectboard.base.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/users")
public class BookmarkController {

    private final UserService userService;

    private final ProjectService projectService;

    private final BookmarkService bookmarkService;

    private final BaseProjectionFactory projectionFactory;

    @Autowired
    public BookmarkController(UserService userService,
                              ProjectService projectService,
                              BookmarkService bookmarkService,
                              BaseProjectionFactory projectionFactory) {
        this.userService = userService;
        this.projectService = projectService;
        this.bookmarkService = bookmarkService;
        this.projectionFactory = projectionFactory;
    }

    @PreAuthorize("hasPermissionToAccessUser(#userId) || hasRole('admin')")
    @DeleteMapping(value = "/{userId}/bookmarks/{projectId}")
    public void deleteBookmarkOfUser(@PathVariable("userId") String userId, @PathVariable("projectId") String projectId) {
        var user = userService.getUserById(userId);
        var project = projectService.getProjectById(projectId);

        bookmarkService.removeBookmarkOfUser(user, project);
    }

    @PreAuthorize("(hasPermissionToAccessUser(#userId) && hasAccessToProjects()) || hasRole('admin')")
    @PostMapping(value = "/{userId}/bookmarks")
    public ResponseEntity<?> createBookmarkForUser(@Valid @RequestBody BookmarkPayload payload, @PathVariable("userId") String userId) {
        var user = userService.getUserById(userId);
        var project = projectService.getProjectById(payload.getProjectId());
        var bookmarkedProject = bookmarkService.addBookmarkToUser(user, project);

        var projection = projectionFactory.createProjectionForAuthenticatedUser(bookmarkedProject,
                ReducedProjectProjection.class, FullProjectProjection.class);
        return ResponseEntity.ok(projection);
    }

    @PreAuthorize("hasPermissionToAccessUser(#userId) || hasRole('admin')")
    @GetMapping(path = "/{userId}/bookmarks")
    public ResponseEntity<?> getBookmarksOfUser(@PathVariable("userId") String userId) {
        var user = userService.getUserById(userId);
        var bookmarkedProjects = bookmarkService.getBookmarksOfUser(user);

        var projections = projectionFactory.createProjectionsForAuthenticatedUser(bookmarkedProjects,
                ReducedProjectProjection.class, FullProjectProjection.class);
        return ResponseEntity.ok(projections);
    }

}
