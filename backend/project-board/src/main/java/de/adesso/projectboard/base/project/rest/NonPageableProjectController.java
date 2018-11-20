package de.adesso.projectboard.base.project.rest;

import de.adesso.projectboard.base.project.persistence.Project;
import de.adesso.projectboard.base.user.persistence.User;
import de.adesso.projectboard.base.user.service.UserProjectService;
import de.adesso.projectboard.base.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * {@link RestController REST Controller} to access {@link Project}s. This
 * controller does not support pagination. A {@link PageableProjectController paginated controller}
 * can be activated via the {@code rest-pagination} profile.
 *
 * @see BaseProjectController
 */
@Profile("!rest-pagination")
@RestController
@RequestMapping(path = "/projects")
public class NonPageableProjectController {

    private final UserProjectService userProjectService;

    private final UserService userService;

    @Autowired
    public NonPageableProjectController(UserProjectService userProjectService,
                                        UserService userService) {
        this.userProjectService = userProjectService;
        this.userService = userService;
    }

    @PreAuthorize("hasAccessToProjects() || hasRole('admin')")
    @GetMapping
    public Iterable<Project> getAllForUser(@SortDefault(direction = Sort.Direction.DESC, sort = "updated") Sort sort) {
        return userProjectService.getProjectsForUser(userService.getAuthenticatedUser(), sort);
    }

    @PreAuthorize("hasAccessToProjects() || hasRole('admin')")
    @GetMapping(path = "/search", params = "keyword")
    public Iterable<Project> searchByKeyword(@RequestParam String keyword, @SortDefault(direction = Sort.Direction.DESC, sort = "updated") Sort sort) {
        if(keyword == null || keyword.isEmpty()) {
            return getAllForUser(sort);
        } else {
            User user = userService.getAuthenticatedUser();

            return userProjectService.searchProjectsForUser(user, keyword, sort);
        }
    }

}
