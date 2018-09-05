package de.adesso.projectboard.core.base.rest.bookmark;

import de.adesso.projectboard.core.base.project.persistence.AbstractProject;
import de.adesso.projectboard.core.base.project.persistence.ProjectRepository;
import de.adesso.projectboard.core.base.rest.bookmark.persistence.ProjectBookmark;
import de.adesso.projectboard.core.base.rest.bookmark.persistence.ProjectBookmarkRepository;
import de.adesso.projectboard.core.base.rest.exceptions.ProjectNotFoundException;
import de.adesso.projectboard.core.base.rest.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/bookmarks")
public class ProjectBookmarkController {

    private final ProjectBookmarkRepository bookmarkRepository;

    private final ProjectRepository projectRepository;

    private final UserService userService;

    @Autowired
    public ProjectBookmarkController(ProjectBookmarkRepository bookmarkRepository,
                                     ProjectRepository projectRepository,
                                     UserService userService) {
        this.bookmarkRepository = bookmarkRepository;
        this.projectRepository = projectRepository;
        this.userService = userService;
    }

    @GetMapping(value = "/", produces = "application/json")
    public Iterable<ProjectBookmark> getBookmarksForCurrentUser() {
        return userService.getCurrentUser().getBookmarks();
    }


    @PreAuthorize("hasRole('admin') || hasAccessToProjects()")
    @PostMapping(value = "/",
            consumes = "application/json",
            produces = "application/json"
    )
    public ProjectBookmark createBookmark(@Valid @RequestBody BookmarkDTO bookmarkDTO) {
        Optional<AbstractProject> projectOptional
                = projectRepository.findById(bookmarkDTO.getProjectId());

        if(projectOptional.isPresent()) {
            ProjectBookmark bookmark = new ProjectBookmark(projectOptional.get());

            return userService.addBookmarkToUser(userService.getCurrentUser(), bookmark);
        } else {
            throw new ProjectNotFoundException();
        }
    }


    @PreAuthorize("hasRole('admin') || hasPermissionToAccessBookmark(#bookmarkId)")
    @DeleteMapping(value = "/{bookmarkId}",
            produces = "application/json"
    )
    public void deleteBookmark(@PathVariable("bookmarkId") long bookmarkId) {
        Optional<ProjectBookmark> bookmarkOptional = bookmarkRepository.findById(bookmarkId);

        if(bookmarkOptional.isPresent()) {
            userService.removeBookmarkFromUser(userService.getCurrentUser(), bookmarkOptional.get());
        } else {
            throw new IllegalArgumentException();
        }
    }

}
