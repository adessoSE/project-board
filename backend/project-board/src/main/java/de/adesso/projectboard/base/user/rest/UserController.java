package de.adesso.projectboard.base.user.rest;

import de.adesso.projectboard.base.projection.ProjectionType;
import de.adesso.projectboard.base.user.projection.UserProjectionFactory;
import de.adesso.projectboard.base.user.projection.UserProjectionSource;
import de.adesso.projectboard.base.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    private final UserProjectionFactory userProjectionFactory;

    @Autowired
    public UserController(UserService userService, UserProjectionFactory userProjectionFactory) {
        this.userService = userService;
        this.userProjectionFactory = userProjectionFactory;
    }

    @PreAuthorize("hasPermissionToAccessUser(#userId) || hasRole('admin')")
    @GetMapping(path = "/{userId}")
    public ResponseEntity<?> getUserById(@PathVariable("userId") String userId, @ProjectionType(UserProjectionSource.class) Class<?> projectionType) {
        var user = userService.getUserById(userId);

        return ResponseEntity.ok(userProjectionFactory.createProjection(user, projectionType));
    }

    @PreAuthorize("hasPermissionToAccessUser(#userId) || hasRole('admin')")
    @GetMapping(path = "/{userId}/staff")
    public ResponseEntity<?> getStaffMembersOfUser(@PathVariable("userId") String userId, Sort sort, @ProjectionType(UserProjectionSource.class) Class<?> projectionType) {
        var user = userService.getUserById(userId);
        var staffData = userService.getStaffMemberUserDataOfUser(user, sort);

        return ResponseEntity.ok(userProjectionFactory.createProjections(staffData, projectionType));
    }

}
