package de.adesso.projectboard.core.base.rest.bookmark;

import de.adesso.projectboard.core.base.project.persistence.ProjectRepository;
import de.adesso.projectboard.core.base.rest.bookmark.persistence.ProjectBookmark;
import de.adesso.projectboard.core.base.rest.bookmark.persistence.ProjectBookmarkRepository;
import de.adesso.projectboard.core.base.rest.security.AuthenticationInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@RestController
@RequestMapping("/bookmarks")
public class BookmarkController {

    private final ProjectBookmarkRepository bookmarkRepository;

    private final ProjectRepository projectRepository;

    private final AuthenticationInfo authInfo;

    @Autowired
    public BookmarkController(ProjectBookmarkRepository bookmarkRepository,
                              ProjectRepository projectRepository,
                              AuthenticationInfo authInfo) {
        this.bookmarkRepository = bookmarkRepository;
        this.projectRepository = projectRepository;
        this.authInfo = authInfo;
    }

    @GetMapping(value = "/my/", produces = "application/json")
    public Iterable<ProjectBookmark> getBookmarksForUser() {

        // TODO: implement
        // get user by userid from userrepo and return the user's bookmarks(lazy init!)
        return Collections.emptyList();

    }

    @PreAuthorize("hasAccessToProjects()")
    @PostMapping(value = "/", consumes = "application/json", produces = "application/json")
    public ProjectBookmark createBookmark(@RequestBody BookmarkDTO bookmarkDTO) {

        // TODO: implement
        // get user by userid (create it if necessary) and add a new ProjectBookmark to the users
        // bookmarks
        return null;

    }

    @DeleteMapping(value = "/{id}", produces = "application/json")
    public void deleteBookmark(@PathVariable("id") long id) {
        bookmarkRepository.deleteById(id);
    }

}
