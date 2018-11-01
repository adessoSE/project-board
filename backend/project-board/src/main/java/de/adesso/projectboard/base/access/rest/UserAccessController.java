package de.adesso.projectboard.base.access.rest;

import de.adesso.projectboard.base.access.dto.UserAccessInfoRequestDTO;
import de.adesso.projectboard.base.access.handler.UserAccessHandler;
import de.adesso.projectboard.base.application.rest.ApplicationController;
import de.adesso.projectboard.base.exceptions.UserNotFoundException;
import de.adesso.projectboard.base.project.rest.ProjectController;
import de.adesso.projectboard.base.user.dto.UserResponseDTO;
import de.adesso.projectboard.base.user.persistence.User;
import de.adesso.projectboard.base.user.rest.BookmarkController;
import de.adesso.projectboard.base.user.rest.UserController;
import de.adesso.projectboard.user.service.UserAccessServiceImpl;
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

    private final UserAccessServiceImpl userAccessService;

    private final UserAccessHandler userAccessHandler;

    @Autowired
    public UserAccessController(UserAccessServiceImpl userAccessService, UserAccessHandler userAccessHandler) {
        this.userAccessService = userAccessService;
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
     * @see UserAccessServiceImpl#createAccessForUser(UserAccessInfoRequestDTO, String)
     */
    @PreAuthorize("hasElevatedAccessToUser(#userId) || hasRole('admin')")
    @PostMapping(path = "/{userId}/access")
    public UserResponseDTO createAccessForUser(@Valid @RequestBody UserAccessInfoRequestDTO infoDTO, @PathVariable("userId") String userId)
            throws UserNotFoundException {
        User updatedUser = userAccessService.createAccessForUser(infoDTO, userId);

        // call handler method
        userAccessHandler.onAccessGranted(updatedUser);

        return UserResponseDTO.fromUser(updatedUser);
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
     * @see UserAccessServiceImpl#deleteAccessForUser(String)
     */
    @PreAuthorize("hasElevatedAccessToUser(#userId) || hasRole('admin')")
    @DeleteMapping(path = "/{userId}/access")
    public UserResponseDTO deleteAccessForUser(@PathVariable("userId") String userId) throws UserNotFoundException {
        return UserResponseDTO.fromUser(userAccessService.deleteAccessForUser(userId));
    }

}
