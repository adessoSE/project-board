package de.adesso.projectboard.core.base.rest.user;

import de.adesso.projectboard.core.base.rest.exceptions.UserNotFoundException;
import de.adesso.projectboard.core.base.rest.user.dto.UserResponseDTO;
import de.adesso.projectboard.core.base.rest.user.persistence.SuperUser;
import de.adesso.projectboard.core.base.rest.user.persistence.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * {@link RestController} for user related data.
 */
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;

        SuperUser tom = new SuperUser("tom");
        tom.setFullName("Tom", "Hombergs");
        tom.setEmail("daniel.meier@adesso.de");
        tom.setLob("Cross Industries");

        User daniel = new User("daniel", tom);
        daniel.setFullName("Daniel", "Meier");
        daniel.setEmail("dadadadadadwdawdwa");
        daniel.setLob("Cross Industries");

        userService.save(tom);
    }

    /**
     *
     * @param userId
     *          The if of the {@link User}.
     *
     * @return
     *          A {@link UserResponseDTO} of the user.
     *
     * @throws UserNotFoundException
     *          When no user is found with the given {@code userId}.
     */
    @PreAuthorize("hasPermissionToAccessUser(#userId) || hasRole('admin')")
    @GetMapping(path = "/{userId}",
            produces = "application/json"
    )
    public UserResponseDTO getUserById(@PathVariable("userId") String userId) throws UserNotFoundException {
        return UserResponseDTO.fromUser(userService.getUserById(userId));
    }

    @PreAuthorize("hasPermissionToAccessUser(#userId) || hasRole('admin')")
    @GetMapping(path = "/{userId}/staff",
            produces = "application/json"
    )
    public List<UserResponseDTO> getStaffMembersOfUser(@PathVariable("userId") String userId) throws UserNotFoundException {
        User user = userService.getUserById(userId);

        if(user instanceof SuperUser) {
            return ((SuperUser) user).getStaffMembers().stream()
                    .map(UserResponseDTO::fromUser)
                    .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }

    /**
     *
     * @return
     *          A {@link List} of all {@link User}s.
     */
    @PreAuthorize("hasRole('admin')")
    @GetMapping
    public List<UserResponseDTO> getAllUsers() {
        return StreamSupport.stream(userService.getAllUsers().spliterator(), true)
                .map(UserResponseDTO::fromUser)
                .collect(Collectors.toList());
    }

}
