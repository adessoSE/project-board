package de.adesso.projectboard.base.user.rest;

import de.adesso.projectboard.base.access.rest.UserAccessController;
import de.adesso.projectboard.base.application.rest.ApplicationController;
import de.adesso.projectboard.base.exceptions.UserNotFoundException;
import de.adesso.projectboard.base.project.persistence.Project;
import de.adesso.projectboard.base.project.rest.ProjectController;
import de.adesso.projectboard.base.user.dto.UserResponseDTO;
import de.adesso.projectboard.base.user.persistence.User;
import de.adesso.projectboard.base.user.persistence.data.UserData;
import de.adesso.projectboard.base.user.service.UserService;
import de.adesso.projectboard.ldap.user.LdapUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

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

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
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
     * @see LdapUserService#getUserById(String)
     */
    @PreAuthorize("hasPermissionToAccessUser(#userId) || hasRole('admin')")
    @GetMapping(path = "/{userId}")
    public UserResponseDTO getUserById(@PathVariable("userId") String userId) throws UserNotFoundException {
        UserData userData = userService.getUserData(userId);
        boolean isBoss = userService.isManager(userId);

        return UserResponseDTO.fromUser(userData, isBoss);
    }

    @PreAuthorize("hasPermissionToAccessUser(#userId) || hasRole('admin')")
    @GetMapping(path = "/{userId}/staff")
    public Iterable<UserResponseDTO> getStaffMembersOfUser(@PathVariable("userId") String userId, @SortDefault(value = "lastName") Sort sort) throws UserNotFoundException {
        // TODO: implement
        return Collections.emptyList();
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
     * @see LdapUserService#getUserById(String)
     */
    @PreAuthorize("hasPermissionToAccessUser(#userId) || hasRole('admin')")
    @GetMapping(path = "/{userId}/projects")
    public Iterable<Project> getCreatedProjectsOfUser(@PathVariable("userId") String userId) throws UserNotFoundException {
        return userService.getUserById(userId).getCreatedProjects();
    }

}
