package de.adesso.projectboard.core.base.rest.user;

import de.adesso.projectboard.core.base.rest.exceptions.BookmarkNotFoundException;
import de.adesso.projectboard.core.base.rest.exceptions.ProjectNotFoundException;
import de.adesso.projectboard.core.base.rest.exceptions.UserNotFoundException;
import de.adesso.projectboard.core.base.rest.project.persistence.Project;
import de.adesso.projectboard.core.base.rest.user.bookmark.dto.BookmarkRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/users")
public class BookmarkController {

    private final UserService userService;

    @Autowired
    public BookmarkController(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("hasPermissionToAccessUser(#userId) || hasRole('admin')")
    @DeleteMapping(value = "/{userId}/bookmarks/{projectId}",
            produces = "application/json"
    )
    public void deleteBookmarkOfUser(@PathVariable("userId") String userId, @PathVariable("projectId") String projectId)
            throws UserNotFoundException, ProjectNotFoundException, BookmarkNotFoundException {
        userService.removeBookmarkFromUser(userId, projectId);
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
     */
    @PreAuthorize("(hasPermissionToAccessUser(#userId) && hasAccessToProjects()) || hasRole('admin')")
    @PostMapping(value = "/{userId}/bookmarks",
            consumes = "application/json",
            produces = "application/json"
    )
    public Project createBookmarkForUser(@Valid @RequestBody BookmarkRequestDTO bookmarkRequestDTO, @PathVariable("userId") String userId)
            throws ProjectNotFoundException {
        return userService.addBookmarkToUser(userId, bookmarkRequestDTO.getProjectId());
    }

    @PreAuthorize("hasPermissionToAccessUser(#userId) || hasRole('admin')")
    @GetMapping(path = "/{userId}/bookmarks",
            produces = "application/json"
    )
    public Iterable<Project> getBookmarksOfUser(@PathVariable("userId") String userId) throws UserNotFoundException {
        return userService.getUserById(userId).getBookmarks();
    }

}
