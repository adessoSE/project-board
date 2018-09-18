package de.adesso.projectboard.core.base.rest.user;

import de.adesso.projectboard.core.base.rest.exceptions.UserNotFoundException;
import de.adesso.projectboard.core.base.rest.user.useraccess.UserAccessHandler;
import de.adesso.projectboard.core.base.rest.user.useraccess.dto.UserAccessInfoRequestDTO;
import de.adesso.projectboard.core.base.rest.user.dto.UserResponseDTO;
import de.adesso.projectboard.core.base.rest.user.persistence.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * {@link RestController} to give users access.
 */
@RestController
@RequestMapping("/users")
public class UserAccessController {

    private final UserService userService;

    private final UserAccessHandler userAccessHandler;

    @Autowired
    public UserAccessController(UserService userService, UserAccessHandler userAccessHandler) {
        this.userService = userService;
        this.userAccessHandler = userAccessHandler;
    }

    @PreAuthorize("hasElevatedAccessToUser(#userId) || hasRole('admin')")
    @PostMapping(path = "/{userId}/access",
            consumes = "application/json",
            produces = "application/json"
    )
    public UserResponseDTO createAccessForUser(@Valid @RequestBody UserAccessInfoRequestDTO infoDTO, @PathVariable("userId") String userId)
            throws UserNotFoundException {
        User userToGiveAccess = userService.getUserById(userId);
        userToGiveAccess.giveAccessUntil(infoDTO.getAccessEnd());

        // save the updated user
        userToGiveAccess = userService.save(userToGiveAccess);

        // call handler method
        userAccessHandler.onAccessGranted(userToGiveAccess);

        return UserResponseDTO.fromUser(userToGiveAccess);
    }

    @PreAuthorize("hasElevatedAccessToUser(#userId) || hasRole('admin')")
    @DeleteMapping(path = "/{userId}/access")
    public UserResponseDTO deleteAccessForUser(@PathVariable("userId") String userId) throws UserNotFoundException {
        User user = userService.getUserById(userId);

        user.removeAccess();
        userService.save(user);

        return UserResponseDTO.fromUser(user);
    }

}
