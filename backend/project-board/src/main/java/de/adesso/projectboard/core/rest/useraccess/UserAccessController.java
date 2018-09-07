package de.adesso.projectboard.core.rest.useraccess;

import de.adesso.projectboard.core.base.rest.exceptions.UserNotFoundException;
import de.adesso.projectboard.core.base.rest.user.UserService;
import de.adesso.projectboard.core.base.rest.user.persistence.User;
import de.adesso.projectboard.core.rest.useraccess.persistence.UserAccessInfo;
import de.adesso.projectboard.core.rest.useraccess.persistence.UserAccessInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@RestController
public class UserAccessController {

    private final UserAccessInfoRepository accessInfoRepo;

    private final UserService userService;

    @Autowired
    public UserAccessController(UserAccessInfoRepository accessInfoRepo, UserService userService) {
        this.accessInfoRepo = accessInfoRepo;
        this.userService = userService;
    }

    @GetMapping(path = "/",
            produces = "application/json"
    )
    public UserAccessInfo getAccessForCurrentUser() {
        Optional<UserAccessInfo> accessInfoOptional
                = accessInfoRepo.getLatestAccessInfo(userService.getCurrentUser());

        return null;
    }

    @PreAuthorize("hasRole('admin') || hasPermissionToAccessUser(#infoDTO.userId)")
    @PostMapping(path = "/projects/access",
            consumes = "application/json",
            produces = "application/json"
    )
    public UserAccessInfo createAccess(@Valid @RequestBody UserAccessInfoClientDTO infoDTO) throws UserNotFoundException {
        User userToGiveAccess = userService.getUserById(infoDTO.getUserId());

        return accessInfoRepo.save(new UserAccessInfo(userToGiveAccess, infoDTO.getAccessEnd()));
    }

    @PreAuthorize("hasRole('admin') || hasPermissionToAccessUser(#userId)")
    @GetMapping(path = "/users/{userId}/access",
            produces = "application/json"
    )
    public UserAccessInfoServerDTO getAccessForUser(@PathVariable("userId") String userId) {
        return null;
    }

}
