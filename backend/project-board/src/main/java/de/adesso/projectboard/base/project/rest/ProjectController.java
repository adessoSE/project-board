package de.adesso.projectboard.base.project.rest;

import de.adesso.projectboard.base.access.rest.UserAccessController;
import de.adesso.projectboard.base.application.rest.ApplicationController;
import de.adesso.projectboard.base.project.dto.ProjectRequestDTO;
import de.adesso.projectboard.base.project.persistence.Project;
import de.adesso.projectboard.base.project.service.ProjectService;
import de.adesso.projectboard.base.user.persistence.User;
import de.adesso.projectboard.base.user.rest.BookmarkController;
import de.adesso.projectboard.base.user.rest.UserController;
import de.adesso.projectboard.base.user.service.UserProjectService;
import de.adesso.projectboard.base.user.service.UserService;
import de.adesso.projectboard.base.util.Sorting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * {@link RestController REST Controller} to access/create/update {@link Project}s.
 *
 * @see UserController
 * @see BookmarkController
 * @see ApplicationController
 * @see UserAccessController
 */
@RestController
@RequestMapping(path = "/projects")
public class ProjectController {

    private final UserProjectService userProjectService;

    private final ProjectService projectService;

    private final UserService userService;

    @Autowired
    public ProjectController(UserProjectService userProjectService,
                             ProjectService projectService,
                             UserService userService) {
        this.userProjectService = userProjectService;
        this.projectService = projectService;
        this.userService = userService;
    }

    @PreAuthorize("hasAccessToProject(#projectId) || hasRole('admin')")
    @GetMapping(path = "/{projectId}")
    public Project getById(@PathVariable String projectId) {
        return projectService.getProjectById(projectId);
    }

    @PreAuthorize("hasAccessToProjects() || hasRole('admin')")
    @GetMapping
    public Iterable<Project> getAllForUser(@SortDefault(direction = Sort.Direction.DESC, sort = "updated") Sort sort) {
        return userProjectService.getProjectsForUser(userService.getAuthenticatedUser(), Sorting.fromSort(sort));
    }

    @PreAuthorize("hasPermissionToCreateProjects() || hasRole('admin')")
    @PostMapping
    public Project createProject(@Valid @RequestBody ProjectRequestDTO projectDTO) {
        return userProjectService.createProjectForUser(projectDTO, userService.getAuthenticatedUser());
    }

    @PreAuthorize("hasPermissionToEditProject(#projectId) || hasRole('admin')")
    @PutMapping(path = "/{projectId}")
    public Project updateProject(@PathVariable String projectId, @Valid @RequestBody ProjectRequestDTO projectDTO) {
            return projectService.updateProject(projectDTO, projectId);
    }

    @PreAuthorize("hasAccessToProjects() || hasRole('admin')")
    @GetMapping(path = "/search", params = "keyword")
    public Iterable<Project> searchByKeyword(@RequestParam String keyword, @SortDefault(direction = Sort.Direction.DESC, sort = "updated") Sort sort) {
        if(keyword == null || keyword.isEmpty()) {
            return getAllForUser(sort);
        } else {
            User user = userService.getAuthenticatedUser();

            return userProjectService.searchProjectsForUser(user, keyword, Sorting.fromSort(sort));
        }
    }

    @PreAuthorize("hasPermissionToEditProject(#projectId) || hasRole('admin')")
    @DeleteMapping(path = "/{projectId}")
    public void deleteProject(@PathVariable String projectId) {
        projectService.deleteProjectById(projectId);
    }

}
