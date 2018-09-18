package de.adesso.projectboard.core.base.rest.user;

import de.adesso.projectboard.core.base.rest.exceptions.ApplicationNotFoundException;
import de.adesso.projectboard.core.base.rest.exceptions.ProjectNotFoundException;
import de.adesso.projectboard.core.base.rest.exceptions.UserNotFoundException;
import de.adesso.projectboard.core.base.rest.project.persistence.AbstractProject;
import de.adesso.projectboard.core.base.rest.project.persistence.ProjectRepository;
import de.adesso.projectboard.core.base.rest.user.application.ProjectApplicationHandler;
import de.adesso.projectboard.core.base.rest.user.application.dto.ProjectApplicationRequestDTO;
import de.adesso.projectboard.core.base.rest.user.application.dto.ProjectApplicationResponseDTO;
import de.adesso.projectboard.core.base.rest.user.application.persistence.ProjectApplication;
import de.adesso.projectboard.core.base.rest.user.application.persistence.ProjectApplicationRepository;
import de.adesso.projectboard.core.base.rest.user.persistence.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.websocket.server.PathParam;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class ApplicationController {

    private final UserService userService;

    private final ProjectRepository projectRepo;

    private final ProjectApplicationRepository applicationRepo;

    private final ProjectApplicationHandler applicationHandler;

    @Autowired
    public ApplicationController(UserService userService,
                                 ProjectRepository projectRepo,
                                 ProjectApplicationRepository applicationRepo,
                                 ProjectApplicationHandler applicationHandler) {
        this.userService = userService;
        this.projectRepo = projectRepo;
        this.applicationRepo = applicationRepo;
        this.applicationHandler = applicationHandler;
    }

    /**
     *
     * @param projectApplicationClientDTO
     *          The {@link ProjectApplicationRequestDTO} send by the user.
     *
     * @return
     *          The {@link ProjectApplicationResponseDTO} of the created {@link ProjectApplication}.
     *
     * @throws ProjectNotFoundException
     *          When the {@link AbstractProject} with the {@link ProjectApplicationRequestDTO#getProjectId() given id}
     *          is not found.
     */
    @PreAuthorize("hasPermissionToApply() || hasRole('admin')")
    @PostMapping(path = "/{userId}/applications",
            consumes = "application/json",
            produces = "application/json"
    )
    public ProjectApplicationResponseDTO createApplicationForUser(@Valid @RequestBody ProjectApplicationRequestDTO projectApplicationClientDTO,
                                                                  @PathVariable("userId") String userId) throws ProjectNotFoundException, UserNotFoundException {

        // get the project by the given id
        Optional<AbstractProject> projectOptional = projectRepo.findById(projectApplicationClientDTO.getProjectId());
        if(!projectOptional.isPresent()) {
            throw new ProjectNotFoundException();
        }

        // create a new project application instance
        ProjectApplication application
                = new ProjectApplication(projectOptional.get(), projectApplicationClientDTO.getComment(), userService.getCurrentUser());

        // persist the application
        ProjectApplication savedApplication
                = userService.addApplicationToUser(userId, application);

        // call the handler method
        applicationHandler.onApplicationReceived(savedApplication);

        // return a DTO
        return ProjectApplicationResponseDTO.fromApplication(savedApplication);
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
     */
    @PreAuthorize("hasPermissionToAccessUser(#userId) || hasRole('admin')")
    @GetMapping(path = "/{userId}/applications",
            produces = "application/json"
    )
    public Iterable<ProjectApplicationResponseDTO> getApplicationsOfUser(@PathVariable("userId") String userId) throws UserNotFoundException {
        return userService.getUserById(userId).getApplications().stream()
                .map(ProjectApplicationResponseDTO::fromApplication)
                .collect(Collectors.toList());
    }



}
