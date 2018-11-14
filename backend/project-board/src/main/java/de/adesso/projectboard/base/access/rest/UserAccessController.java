package de.adesso.projectboard.base.access.rest;

import de.adesso.projectboard.base.access.dto.AccessInfoRequestDTO;
import de.adesso.projectboard.base.access.handler.UserAccessHandler;
import de.adesso.projectboard.base.access.service.UserAccessService;
import de.adesso.projectboard.base.application.rest.ApplicationController;
import de.adesso.projectboard.base.project.rest.ProjectController;
import de.adesso.projectboard.base.user.dto.UserDtoFactory;
import de.adesso.projectboard.base.user.dto.UserResponseDTO;
import de.adesso.projectboard.base.user.persistence.User;
import de.adesso.projectboard.base.user.rest.BookmarkController;
import de.adesso.projectboard.base.user.rest.UserController;
import de.adesso.projectboard.base.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * {@link RestController REST Controller} to grant access to {@link User}s.
 *
 * @see ProjectController
 * @see ApplicationController
 * @see BookmarkController
 * @see UserController
 */
@RestController
@RequestMapping("/users")
public class UserAccessController {

    private final UserService userService;

    private final UserAccessService userAccessService;

    private final UserAccessHandler userAccessHandler;

    private final UserDtoFactory userDtoFactory;

    @Autowired
    public UserAccessController(UserService userService,
                                UserAccessService userAccessService,
                                UserAccessHandler userAccessHandler,
                                UserDtoFactory userDtoFactory) {
        this.userService = userService;
        this.userAccessService = userAccessService;
        this.userAccessHandler = userAccessHandler;
        this.userDtoFactory = userDtoFactory;
    }

    @PreAuthorize("hasElevatedAccessToUser(#userId) || hasRole('admin')")
    @PostMapping(path = "/{userId}/access")
    public UserResponseDTO createAccessForUser(@Valid @RequestBody AccessInfoRequestDTO infoDTO, @PathVariable("userId") String userId) {
        User user = userService.getUserById(userId);

        User updatedUser = userAccessService.giveUserAccessUntil(user, infoDTO.getAccessEnd());

        // call handler method
        userAccessHandler.onAccessGranted(updatedUser);

        return userDtoFactory.createDto(updatedUser);
    }

    @PreAuthorize("hasElevatedAccessToUser(#userId) || hasRole('admin')")
    @DeleteMapping(path = "/{userId}/access")
    public UserResponseDTO deleteAccessForUser(@PathVariable("userId") String userId) {
        User user = userService.getUserById(userId);

        return userDtoFactory.createDto(userAccessService.removeAccessFromUser(user));
    }

}
