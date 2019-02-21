package de.adesso.projectboard.base.project.rest;

import de.adesso.projectboard.base.project.persistence.Project;
import de.adesso.projectboard.base.project.projection.FullProjectProjection;
import de.adesso.projectboard.base.project.projection.ReducedProjectProjection;
import de.adesso.projectboard.base.project.service.ProjectService;
import de.adesso.projectboard.base.projection.BaseProjectionFactory;
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

    private final BaseProjectionFactory projectionFactory;

    @Autowired
    public NonPageableProjectController(ProjectService projectService,
                                        UserProjectService userProjectService,
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
    public ResponseEntity<?> getAllForUser(@SortDefault(direction = Sort.Direction.DESC, sort = "updated") Sort sort) {
        var authenticatedUser = userAuthService.getAuthenticatedUser();
        var projects = userProjectService.getProjectsForUser(authenticatedUser, sort);

        var projections = projectionFactory.createProjectionsForAuthenticatedUser(projects,
                ReducedProjectProjection.class, FullProjectProjection.class);
        return ResponseEntity.ok(projections);
    }

    @PreAuthorize("hasAccessToProjects() || hasRole('admin')")
    @GetMapping(path = "/search", params = "query")
    public ResponseEntity<?> searchByKeyword(@RequestParam String query, @SortDefault(direction = Sort.Direction.DESC, sort = "updated") Sort sort) {
        if(query == null || query.isEmpty()) {
            return getAllForUser(sort);
        } else {
            var user = userAuthService.getAuthenticatedUser();
            var projectsMatchingKeyword = userProjectService.searchProjectsForUser(user, query, sort);

            var projections = projectionFactory.createProjectionsForAuthenticatedUser(projectsMatchingKeyword,
                    ReducedProjectProjection.class, FullProjectProjection.class);
            return ResponseEntity.ok(projections);
        }
    }

}
