package de.adesso.projectboard.core.base.rest.user.bookmark;

import de.adesso.projectboard.core.base.rest.exceptions.ProjectNotFoundException;
import de.adesso.projectboard.core.base.rest.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/projects/bookmark")
public class ProjectBookmarkController {

    private final UserService userService;

    @Autowired
    public ProjectBookmarkController(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("hasRole('admin') || hasAccessToProjects()")
    @PostMapping(value = "/",
            consumes = "application/json",
            produces = "application/json"
    )
    public void createBookmark(@Valid @RequestBody BookmarkClientDTO bookmarkClientDTO) throws ProjectNotFoundException {
       userService.addBookmarkToUser(userService.getCurrentUserId(), bookmarkClientDTO.getProjectId());
    }

}
