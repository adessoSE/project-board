package de.adesso.projectboard.core.rest.useraccess;

import de.adesso.projectboard.core.base.rest.exceptions.UserNotFoundException;
import de.adesso.projectboard.core.base.rest.user.UserService;
import de.adesso.projectboard.core.base.rest.user.persistence.User;
import de.adesso.projectboard.core.rest.useraccess.dto.UserAccessInfoRequestDTO;
import de.adesso.projectboard.core.rest.useraccess.dto.UserAccessInfoResponseDTO;
import de.adesso.projectboard.core.rest.useraccess.persistence.UserAccessInfo;
import de.adesso.projectboard.core.rest.useraccess.persistence.UserAccessInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

/**
 * {@link RestController} to give users access.
 */
@RestController
@RequestMapping("/users")
public class UserAccessController {

    private final UserAccessInfoRepository accessInfoRepo;

    private final UserService userService;

    @Autowired
    public UserAccessController(UserAccessInfoRepository accessInfoRepo, UserService userService) {
        this.accessInfoRepo = accessInfoRepo;
        this.userService = userService;
    }

    @PreAuthorize("hasRole('admin') || hasElevatedAccessToUser(#userId)")
    @PostMapping(path = "/{userId}/access",
            consumes = "application/json",
            produces = "application/json"
    )
    public UserAccessInfoResponseDTO createAccessForUser(@Valid @RequestBody UserAccessInfoRequestDTO infoDTO, @PathVariable("userId") String userId)
            throws UserNotFoundException {
        User userToGiveAccess = userService.getUserById(userId);

        UserAccessInfo accessInfo = accessInfoRepo.save(new UserAccessInfo(userToGiveAccess, infoDTO.getAccessEnd()));

        return UserAccessInfoResponseDTO.fromAccessInfo(accessInfo);
    }

    @PreAuthorize("hasRole('admin') || hasPermissionToAccessUser(#userId)")
    @GetMapping(path = "/{userId}/access",
            produces = "application/json"
    )
    public UserAccessInfoResponseDTO getAccessForUser(@PathVariable("userId") String userId) throws UserNotFoundException {
        User user = userService.getUserById(userId);

        Optional<UserAccessInfo> accessInfoOptional = accessInfoRepo.getLatestAccessInfo(user);

        // return a DTO
        if(accessInfoOptional.isPresent()) {
            return UserAccessInfoResponseDTO.fromAccessInfo(accessInfoOptional.get());
        } else {
            return UserAccessInfoResponseDTO.noAccess(user);
        }
    }

}
