package de.adesso.projectboard.base.project.rest;

import de.adesso.projectboard.base.project.persistence.Project;
import de.adesso.projectboard.base.user.persistence.User;
import de.adesso.projectboard.base.user.service.PageableUserProjectService;
import de.adesso.projectboard.base.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * {@link RestController REST Controller} to access {@link Project}s. This
 * controller supports pagination and is activated via the {@code rest-pagination} profile.
 *
 * @see NonPageableProjectController
 */
@Profile("rest-pagination")
@RestController
@RequestMapping(path = "/projects")
public class PageableProjectController {

    private final PageableUserProjectService userProjectService;

    private final UserService userService;

    @Autowired
    public PageableProjectController(PageableUserProjectService userProjectService,
                             UserService userService) {
        this.userProjectService = userProjectService;
        this.userService = userService;
    }

    @PreAuthorize("hasAccessToProjects() || hasRole('admin')")
    @GetMapping
    public Iterable<Project> getAllForUser(@SortDefault(direction = Sort.Direction.DESC, sort = "updated") Pageable pageable) {
        User user = userService.getAuthenticatedUser();

        return userProjectService.getProjectsForUserPaginated(user, pageable);
    }

    @PreAuthorize("hasAccessToProjects() || hasRole('admin')")
    @GetMapping(path = "/search", params = "keyword")
    public Iterable<Project> searchByKeyword(@RequestParam String keyword, @SortDefault(direction = Sort.Direction.DESC, sort = "updated") Pageable pageable) {
        if(keyword == null || keyword.isEmpty()) {
            return getAllForUser(pageable);
        } else {
            User user = userService.getAuthenticatedUser();

            return userProjectService.searchProjectsForUserPaginated(keyword, user, pageable);
        }
    }

}
