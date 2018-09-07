package de.adesso.projectboard.core.base.rest.user;

import de.adesso.projectboard.core.base.rest.user.application.persistence.ProjectApplication;
import de.adesso.projectboard.core.base.rest.exceptions.UserNotFoundException;
import de.adesso.projectboard.core.base.rest.project.persistence.AbstractProject;
import de.adesso.projectboard.core.base.rest.project.persistence.ProjectRepository;
import de.adesso.projectboard.core.base.rest.user.persistence.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {


    private final ProjectRepository projectRepo;

    private final UserService userService;

    @Autowired
    public UserController(ProjectRepository projectRepo,
                          UserService userService) {
        this.projectRepo = projectRepo;
        this.userService = userService;
    }

    @PreAuthorize("hasRole('admin') || hasPermissionToAccessUser(#userId)")
    @GetMapping(path = "/{userId}",
            produces = "application/json"
    )
    public UserDTO getUserById(@PathVariable("userId") String userId) throws UserNotFoundException {
        return userToUserDTO(userService.getUserById(userId));
    }

    @PreAuthorize("hasRole('admin') || hasPermissionToAccessUser(#userId)")
    @DeleteMapping(value = "/{userId}/bookmarks/{projectId}",
            produces = "application/json"
    )
    public void deleteBookmarkOfUser(@PathVariable("userId") String userId, @PathVariable("projectId") long projectId) {
        userService.removeBookmarkFromUser(userId, projectId);
    }

    @PreAuthorize("hasRole('admin') || hasPermissionToAccessUser(#userId)")
    @GetMapping(path = "/{userId}/bookmarks",
            produces = "application/json"
    )
    public Iterable<AbstractProject> getBookmarksOfUser(@PathVariable("userId") String userId) throws UserNotFoundException {
        return userService.getUserById(userId).getBookmarks();
    }

    @PreAuthorize("hasRole('admin') || hasPermissionToAccessUser(#userId)")
    @GetMapping(path = "/{userId}/applications",
            produces = "application/json"
    )
    public Iterable<ProjectApplication> getApplicationsOfUser(@PathVariable("userId") String userId) {
        return userService.getUserById(userId).getApplications();
    }

    private UserDTO userToUserDTO(User user) {

        // create new applications link to break circular reference to applications
        // user -> application -> user -> application -> ....
        UserDTO.PropertyLink applicationsLink = new UserDTO.PropertyLink();
        applicationsLink.setCount(user.getApplications().size());
        applicationsLink.setPath(String.format("/users/%s/applications", user.getId()));

        // create new bookmarks link to break circular reference to bookmarks
        // user -> bookmark -> user -> bookmark -> ....
        UserDTO.PropertyLink bookmarksLink = new UserDTO.PropertyLink();
        bookmarksLink.setCount(user.getBookmarks().size());
        bookmarksLink.setPath(String.format("/users/%s/bookmarks", user.getId()));

        // create new UserDTO object to return
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setApplications(applicationsLink);
        userDTO.setBookmarks(bookmarksLink);

        return userDTO;
    }

}
