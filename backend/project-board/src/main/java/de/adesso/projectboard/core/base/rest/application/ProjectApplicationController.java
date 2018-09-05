package de.adesso.projectboard.core.base.rest.application;

import de.adesso.projectboard.core.base.rest.application.persistence.ProjectApplication;
import de.adesso.projectboard.core.base.rest.exceptions.UserNotFoundException;
import de.adesso.projectboard.core.base.rest.user.UserService;
import de.adesso.projectboard.core.base.rest.user.persistence.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.persistence.PostRemove;
import javax.validation.Valid;
import java.util.Optional;

/**
 * {@link RestController} for project applications.
 */
@RestController
@RequestMapping("/applications")
public class ProjectApplicationController {

    private final ProjectApplicationHandler applicationHandler;

    private final UserService userService;

    @Autowired
    public ProjectApplicationController(ProjectApplicationHandler applicationHandler, UserService userService) {
        this.applicationHandler = applicationHandler;
        this.userService = userService;
    }

    @GetMapping(value = "/", produces = "application/json")
    public Iterable<ProjectApplication> getApplicationsForCurrentUser() {
        return userService.getCurrentUser().getApplications();
    }

    @PreAuthorize("hasRole('admin') || hasPermissionToAccessUser(#userId)")
    @GetMapping(value = "/{userId}")
    public Iterable<ProjectApplication> getApplicationsForUser(@PathVariable("userId") String userId) {
        Optional<User> userOptional = userService.getUserById(userId);

        if(userOptional.isPresent()) {
            return userOptional.get().getApplications();
        } else {
            throw new UserNotFoundException();
        }
    }

    @PreAuthorize("hasRole('admin') || hasPermissionToApply()")
    @PostMapping(path = "/", consumes = "application/json", produces = "application/json")
    public ProjectApplication createApplication(@Valid @RequestBody ProjectApplicationDTO projectApplicationDTO) {
        ProjectApplication application
                = applicationHandler.onApplicationReceived(projectApplicationDTO);

        return userService.addApplicationToUser(userService.getCurrentUser(), application);
    }

}
