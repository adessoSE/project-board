package de.adesso.projectboard.core.base.rest.project;

import de.adesso.projectboard.core.base.rest.exceptions.ProjectNotEditableException;
import de.adesso.projectboard.core.base.rest.exceptions.ProjectNotFoundException;
import de.adesso.projectboard.core.base.rest.project.dto.ProjectRequestDTO;
import de.adesso.projectboard.core.base.rest.project.persistence.Project;
import de.adesso.projectboard.core.base.rest.project.service.ProjectService;
import de.adesso.projectboard.core.base.rest.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectService projectService;

    private final UserService userService;

    @Autowired
    public ProjectController(ProjectService projectService, UserService userService) {
        this.projectService = projectService;
        this.userService = userService;
    }

    @PreAuthorize("hasAccessToProject(#projectId) || hasRole('admin')")
    @GetMapping(value = "/{projectId}",
            produces = "application/json"
    )
    public Project getById(@PathVariable String projectId) throws ProjectNotFoundException {
        return projectService.getProjectById(projectId);
    }

    @PreAuthorize("hasAccessToProjects() || hasRole('admin')")
    @GetMapping(produces = "application/json")
    public Iterable<Project> getAllForUser() {
        return projectService.getProjectsForUser(userService.getCurrentUser());
    }

    @PreAuthorize("hasPermissionToCreateProjects() || hasRole('admin')")
    @PostMapping(consumes = "application/json",
            produces = "application/json"
    )
    public Project createProject(@Valid @RequestBody ProjectRequestDTO projectDTO) {
        return projectService.createProject(projectDTO, userService.getCurrentUserId());
    }

    @PreAuthorize("hasPermissionToEditProject(#projectId) || hasRole('admin')")
    @PutMapping(
            path = "/{projectId}",
            consumes = "application/json",
            produces = "application/json"
    )
    public Project updateProject(@PathVariable String projectId, @Valid @RequestBody ProjectRequestDTO projectDTO)
            throws ProjectNotFoundException, ProjectNotEditableException {
            return projectService.updateProject(projectDTO, projectId);
    }

    @PreAuthorize("hasPermissionToEditProject(#projectId) || hasRole('admin')")
    @DeleteMapping(
            path = "/{projectId}"
    )
    public void deleteProject(@PathVariable String projectId) throws ProjectNotFoundException, ProjectNotEditableException {
        projectService.deleteProjectById(projectId);
    }

}
