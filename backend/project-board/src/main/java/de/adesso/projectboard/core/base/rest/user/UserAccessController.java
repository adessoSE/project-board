package de.adesso.projectboard.core.base.rest.user;

import de.adesso.projectboard.core.base.rest.exceptions.UserNotFoundException;
import de.adesso.projectboard.core.base.rest.user.service.UserAccessService;
import de.adesso.projectboard.core.base.rest.user.service.UserService;
import de.adesso.projectboard.core.base.rest.user.useraccess.UserAccessHandler;
import de.adesso.projectboard.core.base.rest.user.useraccess.dto.UserAccessInfoRequestDTO;
import de.adesso.projectboard.core.base.rest.user.dto.UserResponseDTO;
import de.adesso.projectboard.core.base.rest.user.persistence.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.nio.file.attribute.UserDefinedFileAttributeView;

/**
 * {@link RestController} to give users access.
 */
@RestController
@RequestMapping("/users")
public class UserAccessController {

    private final UserAccessService userAccessService;

    private final UserAccessHandler userAccessHandler;

    @Autowired
    public UserAccessController(UserAccessService userAccessService, UserAccessHandler userAccessHandler) {
        this.userAccessService = userAccessService;
        this.userAccessHandler = userAccessHandler;
    }

    @PreAuthorize("hasElevatedAccessToUser(#userId) || hasRole('admin')")
    @PostMapping(path = "/{userId}/access",
            consumes = "application/json",
            produces = "application/json"
    )
    public UserResponseDTO createAccessForUser(@Valid @RequestBody UserAccessInfoRequestDTO infoDTO, @PathVariable("userId") String userId)
            throws UserNotFoundException {
        User updatedUser = userAccessService.createAccessForUser(infoDTO, userId);

        // call handler method
        userAccessHandler.onAccessGranted(updatedUser);

        return UserResponseDTO.fromUser(updatedUser);
    }

    @PreAuthorize("hasElevatedAccessToUser(#userId) || hasRole('admin')")
    @DeleteMapping(path = "/{userId}/access")
    public UserResponseDTO deleteAccessForUser(@PathVariable("userId") String userId) throws UserNotFoundException {
        return UserResponseDTO.fromUser(userAccessService.deleteAccessForUser(userId));
    }

}
