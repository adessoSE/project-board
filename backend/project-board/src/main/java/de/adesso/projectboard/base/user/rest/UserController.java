package de.adesso.projectboard.base.user.rest;

import de.adesso.projectboard.base.access.rest.UserAccessController;
import de.adesso.projectboard.base.application.rest.ApplicationController;
import de.adesso.projectboard.base.exceptions.UserNotFoundException;
import de.adesso.projectboard.base.project.persistence.Project;
import de.adesso.projectboard.base.project.rest.ProjectController;
import de.adesso.projectboard.base.user.dto.UserDtoFactory;
import de.adesso.projectboard.base.user.dto.UserResponseDTO;
import de.adesso.projectboard.base.user.persistence.User;
import de.adesso.projectboard.base.user.persistence.data.UserData;
import de.adesso.projectboard.base.user.service.UserService;
import de.adesso.projectboard.base.util.Sorting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

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

    private final UserDtoFactory userDtoFactory;

    @Autowired
    public UserController(UserService userService, UserDtoFactory userDtoFactory) {
        this.userService = userService;
        this.userDtoFactory = userDtoFactory;
    }

    @PreAuthorize("hasPermissionToAccessUser(#userId) || hasRole('admin')")
    @GetMapping(path = "/{userId}")
    public UserResponseDTO getUserById(@PathVariable("userId") String userId) throws UserNotFoundException {
        User user = userService.getUserById(userId);

        return userDtoFactory.createDto(user);
    }

    @PreAuthorize("hasPermissionToAccessUser(#userId) || hasRole('admin')")
    @GetMapping(path = "/{userId}/staff")
    public Iterable<UserResponseDTO> getStaffMembersOfUser(@PathVariable("userId") String userId, Sort sort) throws UserNotFoundException {
        return userService
                .getStaffMemberDataOfUser(userId, Sorting.fromSort(sort))
                .stream()
                .map(UserData::getUser)
                .map(userDtoFactory::createDto)
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasPermissionToAccessUser(#userId) || hasRole('admin')")
    @GetMapping(path = "/{userId}/projects")
    public Iterable<Project> getOwnedProjectsOfUser(@PathVariable("userId") String userId) throws UserNotFoundException {
        return userService.getUserById(userId).getOwnedProjects();
    }

}
