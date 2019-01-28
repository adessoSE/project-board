package de.adesso.projectboard.base.application.rest;

import de.adesso.projectboard.base.application.handler.ProjectApplicationHandler;
import de.adesso.projectboard.base.application.projection.ApplicationProjection;
import de.adesso.projectboard.base.application.projection.ApplicationProjectionFactory;
import de.adesso.projectboard.base.application.service.ApplicationService;
import de.adesso.projectboard.base.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class ApplicationController {

    private final UserService userService;

    private final ApplicationService applicationService;

    private final ProjectApplicationHandler applicationHandler;

    private final ApplicationProjectionFactory applicationProjectionFactory;

    @Autowired
    public ApplicationController(UserService userService,
                                 ApplicationService applicationService,
                                 ProjectApplicationHandler applicationHandler,
                                 ApplicationProjectionFactory applicationProjectionFactory) {
        this.userService = userService;
        this.applicationService = applicationService;
        this.applicationHandler = applicationHandler;
        this.applicationProjectionFactory = applicationProjectionFactory;
    }

    @PreAuthorize("(hasPermissionToAccessUser(#userId) && hasPermissionToApply()) || hasRole('admin')")
    @PostMapping(path = "/{userId}/applications")
    public ResponseEntity<?> createApplicationForUser(@RequestParam String projectId, @RequestParam String comment, @PathVariable String userId) {
        var user = userService.getUserById(userId);

        var application = applicationService.createApplicationForUser(user, projectId, comment);

        // call the handler method
        applicationHandler.onApplicationReceived(application);

        return ResponseEntity.ok(applicationProjectionFactory.createProjection(application, ApplicationProjection.class));
    }

    @PreAuthorize("hasPermissionToAccessUser(#userId) || hasRole('admin')")
    @GetMapping(path = "/{userId}/applications")
    public ResponseEntity<?> getApplicationsOfUser(@PathVariable String userId) {
        var user = userService.getUserById(userId);
        var staffApplications = applicationService.getApplicationsOfUser(user);

        return ResponseEntity.ok(applicationProjectionFactory.createProjections(staffApplications,
                ApplicationProjection.class));
    }

    @PreAuthorize("hasPermissionToAccessUser(#userId) || hasRole('admin')")
    @GetMapping(path = "/{userId}/staff/applications")
    public ResponseEntity<?> getApplicationsOfStaffMembers(@PathVariable String userId, @SortDefault(sort = "applicationDate", direction = Sort.Direction.DESC) Sort sort) {
        var user = userService.getUserById(userId);
        var staffMembers = userService.getStaffMembersOfUser(user);
        var applications =  applicationService.getApplicationsOfUsers(staffMembers, sort);

        return ResponseEntity.ok(applicationProjectionFactory.createProjections(applications, ApplicationProjection.class));
    }

}
