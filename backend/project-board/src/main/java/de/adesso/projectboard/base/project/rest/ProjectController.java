package de.adesso.projectboard.base.project.rest;

import de.adesso.projectboard.base.access.rest.UserAccessController;
import de.adesso.projectboard.base.application.rest.ApplicationController;
import de.adesso.projectboard.base.exceptions.ProjectNotEditableException;
import de.adesso.projectboard.base.exceptions.ProjectNotFoundException;
import de.adesso.projectboard.base.project.dto.ProjectRequestDTO;
import de.adesso.projectboard.base.project.persistence.Project;
import de.adesso.projectboard.base.project.persistence.ProjectOrigin;
import de.adesso.projectboard.base.project.service.ProjectService;
import de.adesso.projectboard.base.user.persistence.User;
import de.adesso.projectboard.base.user.rest.BookmarkController;
import de.adesso.projectboard.base.user.rest.UserController;
import de.adesso.projectboard.base.user.service.UserServiceImpl;
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

    private final ProjectService projectService;

    private final UserServiceImpl userService;

    @Autowired
    public ProjectController(ProjectService projectService, UserServiceImpl userService) {
        this.projectService = projectService;
        this.userService = userService;
    }

    /**
     *
     * @param projectId
     *          The id of the {@link Project} to retrieve.
     *
     * @return
     *          The {@link Project} with the given {@code projectId}.
     *
     * @throws ProjectNotFoundException
     *          When no {@link Project} with the given {@code projectId} was found.
     */
    @PreAuthorize("hasAccessToProject(#projectId) || hasRole('admin')")
    @GetMapping(path = "/{projectId}")
    public Project getById(@PathVariable String projectId) throws ProjectNotFoundException {
        return projectService.getProjectById(projectId);
    }

    /**
     *
     * @param sort
     *          The {@link Sort} to apply. Sorted in descending order
     *          by {@link Project#updated} by default.
     *
     * @return
     *          A {@link Iterable} of {@link Project}s.
     *
     * @see ProjectService#getProjectsForUser(User, Sort)
     */
    @PreAuthorize("hasAccessToProjects() || hasRole('admin')")
    @GetMapping
    public Iterable<Project> getAllForUser(@SortDefault(direction = Sort.Direction.DESC, sort = "updated") Sort sort) {
        return projectService.getProjectsForUser(userService.getCurrentUser(), sort);
    }

    /**
     *
     * @param projectDTO
     *          The {@link ProjectRequestDTO} sent by the client.
     *
     * @return
     *          The created {@link Project}.
     *
     * @see ProjectService#createProject(ProjectRequestDTO, String)
     */
    @PreAuthorize("hasPermissionToCreateProjects() || hasRole('admin')")
    @PostMapping
    public Project createProject(@Valid @RequestBody ProjectRequestDTO projectDTO) {
        return projectService.createProject(projectDTO, userService.getCurrentUserId());
    }

    /**
     *
     * @param projectId
     *          The id of the {@link Project} to update.
     *
     * @param projectDTO
     *          The {@link ProjectRequestDTO} sent by the client.
     *
     * @return
     *          The updated {@link Project}.
     *
     * @throws ProjectNotFoundException
     *          When no {@link Project} with the given {@code projectId} was found.
     *
     * @throws ProjectNotEditableException
     *          When the {@link Project} exists but it's {@link Project#getOrigin() origin}
     *          is not equal to {@link ProjectOrigin#CUSTOM}.
     *
     * @see ProjectService#updateProject(ProjectRequestDTO, String)
     */
    @PreAuthorize("hasPermissionToEditProject(#projectId) || hasRole('admin')")
    @PutMapping(path = "/{projectId}")
    public Project updateProject(@PathVariable String projectId, @Valid @RequestBody ProjectRequestDTO projectDTO)
            throws ProjectNotFoundException, ProjectNotEditableException {
            return projectService.updateProject(projectDTO, projectId);
    }

    /**
     *
     * @param keyword
     *          The keyword to search by.
     *
     * @param sort
     *          The {@link Sort} to apply.
     *
     * @return
     *          A {@link Iterable} of {@link Project}s sorted accordingly.
     *
     * @see #getAllForUser(Sort)
     * @see ProjectService#getProjectsForUserContainingKeyword(User, String, Sort)
     */
    @PreAuthorize("hasAccessToProjects() || hasRole('admin')")
    @GetMapping(path = "/search", params = "keyword")
    public Iterable<Project> searchByKeyword(@RequestParam String keyword, @SortDefault(direction = Sort.Direction.DESC, sort = "updated") Sort sort) {
        if(keyword == null || keyword.isEmpty()) {
            return getAllForUser(sort);
        } else {
            User currentUser = userService.getCurrentUser();

            return projectService.getProjectsForUserContainingKeyword(currentUser, keyword, sort);
        }
    }

    /**
     *
     * @param projectId
     *          The id of the {@link Project} to delete.
     *
     * @throws ProjectNotFoundException
     *          When no {@link Project} with the given id was found.
     *
     * @throws ProjectNotEditableException
     *          When the {@link Project} exists but it's {@link Project#getOrigin() origin}
     *          is not equal to {@link ProjectOrigin#CUSTOM}.
     */
    @PreAuthorize("hasPermissionToEditProject(#projectId) || hasRole('admin')")
    @DeleteMapping(path = "/{projectId}")
    public void deleteProject(@PathVariable String projectId) throws ProjectNotFoundException, ProjectNotEditableException {
        projectService.deleteProjectById(projectId);
    }

}
