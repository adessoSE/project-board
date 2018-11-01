package de.adesso.projectboard.base.application.rest;

import de.adesso.projectboard.base.access.rest.UserAccessController;
import de.adesso.projectboard.base.application.dto.ProjectApplicationRequestDTO;
import de.adesso.projectboard.base.application.dto.ProjectApplicationResponseDTO;
import de.adesso.projectboard.base.application.handler.ProjectApplicationHandler;
import de.adesso.projectboard.base.application.persistence.ProjectApplication;
import de.adesso.projectboard.base.exceptions.AlreadyAppliedException;
import de.adesso.projectboard.base.exceptions.ProjectNotFoundException;
import de.adesso.projectboard.base.exceptions.UserNotFoundException;
import de.adesso.projectboard.base.project.persistence.Project;
import de.adesso.projectboard.base.project.rest.ProjectController;
import de.adesso.projectboard.base.user.persistence.User;
import de.adesso.projectboard.base.user.rest.BookmarkController;
import de.adesso.projectboard.base.user.rest.UserController;
import de.adesso.projectboard.service.ApplicationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.stream.Collectors;

/**
 * {@link RestController REST Controller} for {@link ProjectApplication}s.
 *
 * @see ProjectController
 * @see BookmarkController
 * @see UserAccessController
 * @see UserController
 */
@RestController
@RequestMapping("/users")
public class ApplicationController {

    private final ApplicationServiceImpl applicationService;

    private final ProjectApplicationHandler applicationHandler;

    @Autowired
    public ApplicationController(ApplicationServiceImpl applicationService, ProjectApplicationHandler applicationHandler) {
        this.applicationService = applicationService;
        this.applicationHandler = applicationHandler;
    }

    /**
     *
     * @param requestDTO
     *          The {@link ProjectApplicationRequestDTO} send by the user.
     *
     * @return
     *          The {@link ProjectApplicationResponseDTO} of the created {@link ProjectApplication}.
     *
     * @throws ProjectNotFoundException
     *          When the {@link Project} with the {@link ProjectApplicationRequestDTO#getProjectId() given id}
     *          is not found.
     *
     * @throws UserNotFoundException
     *          When no {@link User} with the given {@code userId} is found.
     *
     * @throws AlreadyAppliedException
     *          When the user has already applied for the {@link Project}.
     *
     * @see ApplicationServiceImpl#createApplicationForUser(ProjectApplicationRequestDTO, String)
     */
    @PreAuthorize("(hasPermissionToAccessUser(#userId) && hasPermissionToApply()) || hasRole('admin')")
    @PostMapping(path = "/{userId}/applications")
    public ProjectApplicationResponseDTO createApplicationForUser(@Valid @RequestBody ProjectApplicationRequestDTO requestDTO,
                                                                  @PathVariable("userId") String userId)
            throws ProjectNotFoundException, UserNotFoundException, AlreadyAppliedException {

        ProjectApplication application = applicationService.createApplicationForUser(requestDTO, userId);

        // call the handler method
        applicationHandler.onApplicationReceived(application);

        // return a DTO
        return ProjectApplicationResponseDTO.fromApplication(application);

    }

    /**
     *
     * @param userId
     *          The id of the {@link User} get the applications of.
     *
     * @return
     *          A {@link Iterable} of all project applications as DTOs.
     *
     * @throws UserNotFoundException
     *          When the user with the given {@code userId} is not found.
     *
     * @see ApplicationServiceImpl#getApplicationsOfUser(String)
     */
    @PreAuthorize("hasPermissionToAccessUser(#userId) || hasRole('admin')")
    @GetMapping(path = "/{userId}/applications")
    public Iterable<ProjectApplicationResponseDTO> getApplicationsOfUser(@PathVariable("userId") String userId) throws UserNotFoundException {
        return applicationService.getApplicationsOfUser(userId).stream()
                .map(ProjectApplicationResponseDTO::fromApplication)
                .collect(Collectors.toList());
    }

}
