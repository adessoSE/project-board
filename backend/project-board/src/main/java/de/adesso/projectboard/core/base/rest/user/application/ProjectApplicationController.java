package de.adesso.projectboard.core.base.rest.user.application;

import de.adesso.projectboard.core.base.rest.exceptions.ProjectNotFoundException;
import de.adesso.projectboard.core.base.rest.project.persistence.AbstractProject;
import de.adesso.projectboard.core.base.rest.project.persistence.ProjectRepository;
import de.adesso.projectboard.core.base.rest.user.UserService;
import de.adesso.projectboard.core.base.rest.user.application.persistence.ProjectApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

/**
 * {@link RestController} for project applications.
 */
@RestController
@RequestMapping("/projects/apply")
public class ProjectApplicationController {

    private final ProjectApplicationHandler applicationHandler;

    private final ProjectRepository projectRepo;

    private final UserService userService;

    @Autowired
    public ProjectApplicationController(ProjectApplicationHandler applicationHandler,
                                        ProjectRepository projectRepo,
                                        UserService userService) {
        this.applicationHandler = applicationHandler;
        this.projectRepo = projectRepo;
        this.userService = userService;
    }

    @PreAuthorize("hasRole('admin') || hasPermissionToApply()")
    @PostMapping(path = "/",
            consumes = "application/json",
            produces = "application/json"
    )
    public ProjectApplication createApplication(@Valid @RequestBody ProjectApplicationClientDTO projectApplicationClientDTO) {

        // get the project by the given id
        Optional<AbstractProject> projectOptional = projectRepo.findById(projectApplicationClientDTO.getProjectId());
        if(!projectOptional.isPresent()) {
            throw new ProjectNotFoundException();
        }

        // create a new project application instance
        ProjectApplication application
                = new ProjectApplication(projectOptional.get(), projectApplicationClientDTO.getComment(), userService.getCurrentUser());

        // call the handler method
        applicationHandler.onApplicationReceived(application);

        return userService.addApplicationToUser(userService.getCurrentUserId(), application);
    }

}
