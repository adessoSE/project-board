package de.adesso.projectboard.base.user.rest;

import de.adesso.projectboard.base.access.rest.UserAccessController;
import de.adesso.projectboard.base.application.rest.ApplicationController;
import de.adesso.projectboard.base.exceptions.UserNotFoundException;
import de.adesso.projectboard.base.project.persistence.Project;
import de.adesso.projectboard.base.project.rest.ProjectController;
import de.adesso.projectboard.base.user.dto.UserResponseDTO;
import de.adesso.projectboard.base.user.persistence.User;
import de.adesso.projectboard.base.user.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * {@link RestController REST Controller} to access {@link User}s.
 *
 * @see ProjectController
 * @see ApplicationController
 * @see BookmarkController
 * @see UserAccessController
 */
@RestController
@RequestMapping(path = "/users")
public class UserController {

    private final UserServiceImpl userService;

    @Autowired
    public UserController(UserServiceImpl userService) {
        this.userService = userService;
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
     *
     * @see UserServiceImpl#getUserById(String)
     */
    @PreAuthorize("hasPermissionToAccessUser(#userId) || hasRole('admin')")
    @GetMapping(path = "/{userId}")
    public UserResponseDTO getUserById(@PathVariable("userId") String userId) throws UserNotFoundException {
        return UserResponseDTO.fromUser(userService.getUserById(userId));
    }

    /**
     *
     * @param userId
     *          The if of the {@link User}.
     *
     * @param sort
     *          The {@link Sort} to apply. Sorted in ascending order
     *          by {@link User#lastName} by default.
     *
     * @return
     *          A {@link Iterable} of all {@link User staff members} of the {@link User}
     *          with the given {@code userId}.
     *
     * @throws UserNotFoundException
     *          When no {@link User} with the given {@code userId} was found.
     *
     * @see UserServiceImpl#getStaffMembersOfUser(User, Sort)
     */
    @PreAuthorize("hasPermissionToAccessUser(#userId) || hasRole('admin')")
    @GetMapping(path = "/{userId}/staff")
    public Iterable<UserResponseDTO> getStaffMembersOfUser(@PathVariable("userId") String userId, @SortDefault(value = "lastName") Sort sort) throws UserNotFoundException {
        User user = userService.getUserById(userId);

        return userService.getStaffMembersOfUser(user, sort).stream()
                .map(UserResponseDTO::fromUser)
                .collect(Collectors.toList());
    }

    /**
     *
     * @param userId
     *          The id of the {@link User} to get the created {@link Project}s from.
     *
     * @return
     *          A {@link Iterable} of all {@link Project}s the user created.
     *
     * @throws UserNotFoundException
     *          When no {@link User} with the given {@code userId} was found.
     *
     * @see UserServiceImpl#getUserById(String)
     */
    @PreAuthorize("hasPermissionToAccessUser(#userId) || hasRole('admin')")
    @GetMapping(path = "/{userId}/projects")
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
