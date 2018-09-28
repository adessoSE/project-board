package de.adesso.projectboard.core.base.rest.user;

import de.adesso.projectboard.core.base.rest.exceptions.UserNotFoundException;
import de.adesso.projectboard.core.base.rest.project.persistence.Project;
import de.adesso.projectboard.core.base.rest.user.dto.UserResponseDTO;
import de.adesso.projectboard.core.base.rest.user.persistence.SuperUser;
import de.adesso.projectboard.core.base.rest.user.persistence.User;
import de.adesso.projectboard.core.base.rest.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        daniel.setEmail("Daniel.Meier@adesso.de");
        daniel.setLob("Cross Industries");
        userService.save(tom);

        // creating mock-users for test-reasons
        SuperUser jacobs = new SuperUser("jacobs");
        jacobs.setFirstName("Lottie");
        jacobs.setLastName("Jacobs");
        jacobs.setEmail("lottie.jacobs@adesso.de");
        jacobs.setLob("LOB Banking");
        userService.save(jacobs);

        User good = new User("good", jacobs);
        good.setFirstName("Knapp");
        good.setLastName("Good");
        good.setEmail("knapp.good@adesso.de");
        good.setLob("LOB Banking");
        userService.save(good);

        User holt = new User("holt", jacobs);
        holt.setFirstName("Willie");
        holt.setLastName("Holt");
        holt.setEmail("willie.holt@adesso.de");
        holt.setLob("LOB Banking");
        userService.save(holt);

        User roy = new User("roy", jacobs);
        roy.setFirstName("Viola");
        roy.setLastName("Roy");
        roy.setEmail("viola.roy@adesso.de");
        roy.setLob("LOB Banking");
        userService.save(roy);

        User lambert = new User("lambert", jacobs);
        lambert.setFirstName("Leta");
        lambert.setLastName("Lambert");
        lambert.setEmail("leta.lambert@adesso.de");
        lambert.setLob("LOB Banking");
        userService.save(lambert);

        User hoffman = new User("hoffman", jacobs);
        hoffman.setFirstName("Shannon");
        hoffman.setLastName("Hoffman");
        hoffman.setEmail("shannon.hoffman@adesso.de");
        hoffman.setLob("LOB Banking");
        userService.save(hoffman);

        User key = new User("key", jacobs);
        key.setFirstName("Rosanna");
        key.setLastName("Key");
        key.setEmail("rosanna.key@adesso.de");
        key.setLob("LOB Banking");
        userService.save(key);

        User wiley = new User("wiley", jacobs);
        wiley.setFirstName("Jimenez");
        wiley.setLastName("Wiley");
        wiley.setEmail("jimenez.wiley@adesso.de");
        wiley.setLob("LOB Banking");
        userService.save(wiley);

        User clements = new User("clements", jacobs);
        clements.setFirstName("Carmela");
        clements.setLastName("Clements");
        clements.setEmail("carmela.clements@adesso.de");
        clements.setLob("LOB Banking");
        userService.save(clements);

        User cox = new User("cox", jacobs);
        cox.setFirstName("Alexandria");
        cox.setLastName("Cox");
        cox.setEmail("alexandria.cox@adesso.de");
        cox.setLob("LOB Banking");
        userService.save(cox);

        userService.delete(cox);
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

    /**
     *
     * @param userId
     *          The if of the {@link User}.
     *
     * @return
     *          A {@link Iterable} of all {@link Project}s the user with the given {@code userId}
     *          created.
     *
     * @throws UserNotFoundException
     *          When no {@link User} with the given {@code userId} was found.
     */
    @PreAuthorize("hasPermissionToAccessUser(#userId) || hasRole('admin')")
    @GetMapping(path = "/{userId}/staff",
            produces = "application/json"
    )
    public Iterable<UserResponseDTO> getStaffMembersOfUser(@PathVariable("userId") String userId) throws UserNotFoundException {
        User user = userService.getUserById(userId);

        return user.getStaffMembers().stream()
                .map(UserResponseDTO::fromUser)
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasPermissionToAccessUser(#userId) || hasRole('admin')")
    @GetMapping(
            path = "/{userId}/projects",
            produces = "application/json"
    )
    public Iterable<Project> getCreatedProjectsOfUser(@PathVariable("userId") String userId) throws UserNotFoundException {
        return userService.getUserById(userId).getCreatedProjects();
    }

    /**
     *
     * @return
     *          A {@link List} of all {@link User}s.
     */
    @PreAuthorize("hasRole('admin')")
    @GetMapping
    public Iterable<UserResponseDTO> getAllUsers() {
        return StreamSupport.stream(userService.getAllUsers().spliterator(), true)
                .map(UserResponseDTO::fromUser)
                .collect(Collectors.toList());
    }

}
