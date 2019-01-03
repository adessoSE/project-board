package de.adesso.projectboard.base.project.rest;

import de.adesso.projectboard.base.project.dto.ProjectDtoMapper;
import de.adesso.projectboard.base.project.dto.ProjectRequestDTO;
import de.adesso.projectboard.base.project.persistence.Project;
import de.adesso.projectboard.base.project.service.ProjectService;
import de.adesso.projectboard.base.user.service.UserAuthService;
import de.adesso.projectboard.base.user.service.UserProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * {@link RestController REST Controller} to access/create/delete a single
 * {@link Project}.
 *
 * @see NonPageableProjectController
 * @see PageableProjectController
 */
@RestController
@RequestMapping(path = "/projects")
public class BaseProjectController {

    private final ProjectService projectService;

    private final UserProjectService userProjectService;

    private final UserAuthService userAuthService;

    private final ProjectDtoMapper projectDtoMapper;

    @Autowired
    public BaseProjectController(ProjectService projectService,
                                 UserProjectService userProjectService,
                                 UserAuthService userAuthService,
                                 ProjectDtoMapper projectDtoMapper) {
        this.projectService = projectService;
        this.userProjectService = userProjectService;
        this.userAuthService = userAuthService;
        this.projectDtoMapper = projectDtoMapper;
    }

    @PreAuthorize("hasAccessToProject(#projectId) || hasRole('admin')")
    @GetMapping(path = "/{projectId}")
    public Project getById(@PathVariable String projectId) {
        return projectService.getProjectById(projectId);
    }

    @PreAuthorize("hasPermissionToCreateProjects() || hasRole('admin')")
    @PostMapping
    public Project createProject(@Valid @RequestBody ProjectRequestDTO projectDTO) {
        var user = userAuthService.getAuthenticatedUser();

        return userProjectService.createProjectForUser(projectDtoMapper.toProject(projectDTO), user);
    }

    @PreAuthorize("hasPermissionToEditProject(#projectId) || hasRole('admin')")
    @PutMapping(path = "/{projectId}")
    public Project updateProject(@PathVariable String projectId, @Valid @RequestBody ProjectRequestDTO projectDTO) {
        return projectService.updateProject(projectDtoMapper.toProject(projectDTO), projectId);
    }

    @PreAuthorize("hasPermissionToEditProject(#projectId) || hasRole('admin')")
    @DeleteMapping(path = "/{projectId}")
    public void deleteProject(@PathVariable String projectId) {
        projectService.deleteProjectById(projectId);
    }

}
