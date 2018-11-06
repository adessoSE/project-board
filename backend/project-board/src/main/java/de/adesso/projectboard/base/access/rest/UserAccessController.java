package de.adesso.projectboard.base.access.rest;

import de.adesso.projectboard.base.access.dto.UserAccessInfoRequestDTO;
import de.adesso.projectboard.base.access.handler.UserAccessHandler;
import de.adesso.projectboard.base.access.service.UserAccessService;
import de.adesso.projectboard.base.application.rest.ApplicationController;
import de.adesso.projectboard.base.exceptions.UserNotFoundException;
import de.adesso.projectboard.base.project.rest.ProjectController;
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

    private final UserAccessService userAccessService;

    private final UserService userService;

    private final UserAccessHandler userAccessHandler;

    @Autowired
    public UserAccessController(UserAccessService userAccessService,
                                UserService userService,
                                UserAccessHandler userAccessHandler) {
        this.userAccessService = userAccessService;
        this.userService = userService;
        this.userAccessHandler = userAccessHandler;
    }

    /**
     *
     * @param infoDTO
     *          The {@link UserAccessInfoRequestDTO} supplied by the client.
     *
     * @param userId
     *          The id of the {@link User} to grant access.
     *
     * @return
     *          The {@link UserResponseDTO} representing the user.
     *
     * @throws UserNotFoundException
     *          When no {@link User} with the given {@code userId} was found.
     *
     */
    @PreAuthorize("hasElevatedAccessToUser(#userId) || hasRole('admin')")
    @PostMapping(path = "/{userId}/access")
    public UserResponseDTO createAccessForUser(@Valid @RequestBody UserAccessInfoRequestDTO infoDTO, @PathVariable("userId") String userId)
            throws UserNotFoundException {
        User updatedUser = userAccessService.giveUserAccessUntil(userId, infoDTO.getAccessEnd());

        // call handler method
        userAccessHandler.onAccessGranted(updatedUser);

        return UserResponseDTO.fromUserData(userService.getUserData(userId), userService.isManager(userId));
    }

    /**
     *
     * @param userId
     *          The id of the {@link User} to revoke access from.
     *
     * @return
     *          The {@link UserResponseDTO} representing the user.
     *
     * @throws UserNotFoundException
     *          When no {@link User} with the given {@code userId} was found.
     *
     */
    @PreAuthorize("hasElevatedAccessToUser(#userId) || hasRole('admin')")
    @DeleteMapping(path = "/{userId}/access")
    public UserResponseDTO deleteAccessForUser(@PathVariable("userId") String userId) throws UserNotFoundException {
        userAccessService.removeAccessFromUser(userId);

        return UserResponseDTO.fromUserData(userService.getUserData(userId), userService.isManager(userId));
    }

}
