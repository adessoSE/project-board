package de.adesso.projectboard.core.base.rest.application;

import de.adesso.projectboard.core.base.rest.application.persistence.ProjectApplication;
import de.adesso.projectboard.core.base.rest.application.persistence.ProjectApplicationRepository;
import de.adesso.projectboard.core.base.rest.security.AuthenticationInfo;
import de.adesso.projectboard.core.base.user.persistence.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collections;

@RestController
@RequestMapping("/applications")
public class ProjectApplicationController {

    private final ProjectApplicationHandler applicationHandler;

    private final ProjectApplicationRepository logRepository;

    private final UserRepository userRepository;

    private final AuthenticationInfo authInfo;

    @Autowired
    public ProjectApplicationController(ProjectApplicationHandler applicationHandler,
                                        ProjectApplicationRepository logRepository,
                                        UserRepository userRepository,
                                        AuthenticationInfo authInfo) {
        this.applicationHandler = applicationHandler;
        this.logRepository = logRepository;
        this.userRepository = userRepository;
        this.authInfo = authInfo;
    }

    @GetMapping(value = "/my/", produces = "application/json")
    public Iterable<ProjectApplication> getApplicationsForUser() {

        // TODO: return list of application from user(lazy initialization!)
        return Collections.emptyList();

    }

    @PreAuthorize("hasPermissionToApply()")
    @PostMapping(path = "/", consumes = "application/json", produces = "application/json")
    public ProjectApplication applyForProject(@Valid @RequestBody ProjectApplicationDTO projectApplicationDTO) {

        // TODO: implement
        // get user by userid (create it if necessary) and add the projectapplication to the users applications
        return null;

    }

}
