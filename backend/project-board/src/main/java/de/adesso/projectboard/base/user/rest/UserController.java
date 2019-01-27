package de.adesso.projectboard.base.user.rest;

import de.adesso.projectboard.base.access.rest.UserAccessController;
import de.adesso.projectboard.base.application.rest.ApplicationController;
import de.adesso.projectboard.base.exceptions.UserNotFoundException;
import de.adesso.projectboard.base.project.persistence.Project;
import de.adesso.projectboard.base.project.rest.NonPageableProjectController;
import de.adesso.projectboard.base.projection.ProjectionService;
import de.adesso.projectboard.base.projection.ProjectionTarget;
import de.adesso.projectboard.base.user.persistence.User;
import de.adesso.projectboard.base.user.persistence.data.UserData;
import de.adesso.projectboard.base.user.projection.ProjectionSource;
import de.adesso.projectboard.base.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

/**
 * {@link RestController REST Controller} to access {@link User}s.
 *
 * @see NonPageableProjectController
 * @see ApplicationController
 * @see BookmarkController
 * @see UserAccessController
 */
@RestController
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    private final ProjectionService projectionService;

    private final ProjectionFactory projectionFactory;

    @Autowired
    public UserController(UserService userService, ProjectionService projectionService, ProjectionFactory projectionFactory) {
        this.userService = userService;
        this.projectionService = projectionService;
        this.projectionFactory = projectionFactory;
    }

    @PreAuthorize("hasPermissionToAccessUser(#userId) || hasRole('admin')")
    @GetMapping(path = "/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable("userId") String userId, @RequestParam(required = false, defaultValue = "") String projection) {
        var user = userService.getUserById(userId);
        var projectionType = projectionService.getByNameOrDefault(projection, ProjectionTarget.USER);

        return ResponseEntity.ok(projectionFactory.createProjection(projectionType, user));
    }

    @PreAuthorize("hasPermissionToAccessUser(#userId) || hasRole('admin')")
    @GetMapping(path = "/{userId}/staff")
    public ResponseEntity<?> getStaffMembersOfUser(@PathVariable("userId") String userId, Sort sort, @RequestParam(required = false, defaultValue = "") String projection) {
        var projectionType = projectionService.getByNameOrDefault(projection, ProjectionTarget.USER);

        var user = userService.getUserById(userId);
        var staffData = userService.getStaffMemberUserDataOfUser(user, sort);
        var staff = staffData.parallelStream()
                .map(UserData::getUser)
                .collect(Collectors.toSet());
        var staffManagerMap = userService.usersAreManagers(staff);

        var projections = staffData.stream()
                .map(data -> {
                    var staffMember = data.getUser();
                    var manager = staffManagerMap.get(staffMember);

                    return new ProjectionSource(staffMember, data, manager);
                })
                .map(source -> projectionFactory.createProjection(projectionType, source))
                .collect(Collectors.toList());

        return ResponseEntity.ok(projections);
    }

    @PreAuthorize("hasPermissionToAccessUser(#userId) || hasRole('admin')")
    @GetMapping(path = "/{userId}/projects")
    public Iterable<Project> getOwnedProjectsOfUser(@PathVariable("userId") String userId) throws UserNotFoundException {
        return userService
                .getUserById(userId)
                .getOwnedProjects();
    }

}
