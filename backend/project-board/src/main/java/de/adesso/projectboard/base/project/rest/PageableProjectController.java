package de.adesso.projectboard.base.project.rest;

import de.adesso.projectboard.base.project.persistence.Project;
import de.adesso.projectboard.base.project.projection.FullProjectProjection;
import de.adesso.projectboard.base.project.projection.ReducedProjectProjection;
import de.adesso.projectboard.base.project.service.ProjectService;
import de.adesso.projectboard.base.projection.BaseProjectionFactory;
import de.adesso.projectboard.base.user.service.PageableUserProjectService;
import de.adesso.projectboard.base.user.service.UserAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * {@link RestController REST Controller} to access {@link Project}s. This
 * controller supports pagination and is activated via the {@code rest-pagination} profile.
 *
 * @see NonPageableProjectController
 */
@ConditionalOnProperty(
        prefix = "projectboard.project-pagination",
        name = "enabled",
        havingValue = "true"
)
@RestController
@RequestMapping(path = "/projects")
public class PageableProjectController extends BaseProjectController {

    private final ProjectService projectService;

    private final PageableUserProjectService userProjectService;

    private final UserAuthService userAuthService;

    private final BaseProjectionFactory projectionFactory;

    @Autowired
    public PageableProjectController(ProjectService projectService,
                                     PageableUserProjectService userProjectService,
                                     UserAuthService userAuthService,
                                     BaseProjectionFactory projectionFactory) {
        this.projectService = projectService;
        this.userProjectService = userProjectService;
        this.userAuthService = userAuthService;
        this.projectionFactory = projectionFactory;
    }

    @PreAuthorize("hasAccessToProjects() || hasRole('admin')")
    @GetMapping("/{projectId}")
    @Override
    public ResponseEntity<?> getById(@PathVariable String projectId) {
        var project = projectService.getProjectById(projectId);

        var projection = projectionFactory.createProjectionForAuthenticatedUser(project,
                ReducedProjectProjection.class, FullProjectProjection.class);
        return ResponseEntity.ok(projection);
    }

    @PreAuthorize("hasAccessToProjects() || hasRole('admin')")
    @GetMapping
    public ResponseEntity<?> getAllForUser(@SortDefault(direction = Sort.Direction.DESC, sort = "updated") Pageable pageable) {
        var authenticatedUser = userAuthService.getAuthenticatedUser();
        var projectsPage = userProjectService.getProjectsForUserPaginated(authenticatedUser, pageable);

        var projectionsPage = projectionFactory.createProjectionsForAuthenticatedUser(projectsPage,
                ReducedProjectProjection.class, FullProjectProjection.class);
        return ResponseEntity.ok(projectionsPage);
    }

    @PreAuthorize("hasAccessToProjects() || hasRole('admin')")
    @GetMapping(path = "/search", params = "query")
    public ResponseEntity<?> searchByKeyword(@RequestParam String query, @SortDefault(direction = Sort.Direction.DESC, sort = "updated") Pageable pageable) {
        if(query == null || query.isEmpty()) {
            return getAllForUser(pageable);
        } else {
            var authenticatedUser = userAuthService.getAuthenticatedUser();
            var projectsPage = userProjectService.searchProjectsForUserPaginated(query, authenticatedUser, pageable);

            var projectionsPage = projectionFactory.createProjectionsForAuthenticatedUser(projectsPage,
                    ReducedProjectProjection.class, FullProjectProjection.class);
            return ResponseEntity.ok(projectionsPage);
        }
    }

}
