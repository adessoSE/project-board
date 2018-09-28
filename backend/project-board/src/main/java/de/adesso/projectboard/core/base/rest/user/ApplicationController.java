package de.adesso.projectboard.core.base.rest.user;

import de.adesso.projectboard.core.base.rest.exceptions.AlreadyAppliedException;
import de.adesso.projectboard.core.base.rest.exceptions.ProjectNotFoundException;
import de.adesso.projectboard.core.base.rest.exceptions.UserNotFoundException;
import de.adesso.projectboard.core.base.rest.project.persistence.Project;
import de.adesso.projectboard.core.base.rest.project.persistence.ProjectRepository;
import de.adesso.projectboard.core.base.rest.user.application.ProjectApplicationHandler;
import de.adesso.projectboard.core.base.rest.user.application.dto.ProjectApplicationRequestDTO;
import de.adesso.projectboard.core.base.rest.user.application.dto.ProjectApplicationResponseDTO;
import de.adesso.projectboard.core.base.rest.user.application.persistence.ProjectApplication;
import de.adesso.projectboard.core.base.rest.user.application.persistence.ProjectApplicationRepository;
import de.adesso.projectboard.core.base.rest.user.persistence.User;
import de.adesso.projectboard.core.base.rest.user.persistence.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
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
     *          When the {@link Project} with the {@link ProjectApplicationRequestDTO#getProjectId() given id}
     *          is not found.
     *
     * @throws UserNotFoundException
     *          When no {@link User} with the given {@code userId} is found.
     *
     * @throws AlreadyAppliedException
     *          When the user has already applied for the {@link Project}.
     */
    @PreAuthorize("(hasPermissionToAccessUser(#userId) && hasPermissionToApply()) || hasRole('admin')")
    @PostMapping(path = "/{userId}/applications",
            consumes = "application/json",
            produces = "application/json"
    )
    public ProjectApplicationResponseDTO createApplicationForUser(@Valid @RequestBody ProjectApplicationRequestDTO projectApplicationClientDTO,
                                                                  @PathVariable("userId") String userId)
            throws ProjectNotFoundException, UserNotFoundException, AlreadyAppliedException {

        // get the project by the given id
        Optional<Project> projectOptional = projectRepo.findById(projectApplicationClientDTO.getProjectId());
        if(!projectOptional.isPresent()) {
            throw new ProjectNotFoundException();
        }

        // check if the user already applied for the project
        if(userService.userHasAppliedForProject(userId, projectOptional.get())) {
            throw new AlreadyAppliedException();
        }

        // create a new project application instance
        ProjectApplication application
                = new ProjectApplication(projectOptional.get(), projectApplicationClientDTO.getComment(), userService.getUserById(userId));

        // persist the application
        ProjectApplication savedApplication = applicationRepo.save(application);

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
