package de.adesso.projectboard.base.project.rest;

import de.adesso.projectboard.base.project.persistence.Project;
import de.adesso.projectboard.base.project.projection.ProjectProjectionFactory;
import de.adesso.projectboard.base.project.service.ProjectService;
import de.adesso.projectboard.base.user.service.UserAuthService;
import de.adesso.projectboard.base.user.service.UserProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
public class NonPageableProjectController extends BaseProjectController {

    private final ProjectService projectService;

    private final UserProjectService userProjectService;

    private final UserAuthService userAuthService;

    private final ProjectProjectionFactory projectProjectionFactory;

    @Autowired
    public NonPageableProjectController(ProjectService projectService,
                                        UserProjectService userProjectService,
                                        UserAuthService userAuthService,
                                        ProjectProjectionFactory projectProjectionFactory) {
        this.projectService = projectService;
        this.userProjectService = userProjectService;
        this.userAuthService = userAuthService;
        this.projectProjectionFactory = projectProjectionFactory;
    }

    @PreAuthorize("hasAccessToProjects() || hasRole('admin')")
    @GetMapping("/{projectId}")
    @Override
    public ResponseEntity<?> getById(@PathVariable String projectId) {
        var project = projectService.getProjectById(projectId);
        var authenticatedUser = userAuthService.getAuthenticatedUser();
        var projection = projectProjectionFactory.createProjectionForUser(project, authenticatedUser);

        return ResponseEntity.ok(projection);
    }

    @PreAuthorize("hasAccessToProjects() || hasRole('admin')")
    @GetMapping
    public Iterable<Project> getAllForUser(@SortDefault(direction = Sort.Direction.DESC, sort = "updated") Sort sort) {
        return userProjectService.getProjectsForUser(userAuthService.getAuthenticatedUser(), sort);
    }

    @PreAuthorize("hasAccessToProjects() || hasRole('admin')")
    @GetMapping(path = "/search", params = "keyword")
    public Iterable<Project> searchByKeyword(@RequestParam String keyword, @SortDefault(direction = Sort.Direction.DESC, sort = "updated") Sort sort) {
        if(keyword == null || keyword.isEmpty()) {
            return getAllForUser(sort);
        } else {
            var user = userAuthService.getAuthenticatedUser();

            return userProjectService.searchProjectsForUser(user, keyword, sort);
        }
    }

}
