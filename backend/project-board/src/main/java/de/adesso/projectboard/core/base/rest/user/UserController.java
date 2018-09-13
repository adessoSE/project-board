package de.adesso.projectboard.core.base.rest.user;

import de.adesso.projectboard.core.base.rest.exceptions.BookmarkNotFoundException;
import de.adesso.projectboard.core.base.rest.exceptions.ProjectNotFoundException;
import de.adesso.projectboard.core.base.rest.exceptions.UserNotFoundException;
import de.adesso.projectboard.core.base.rest.project.persistence.AbstractProject;
import de.adesso.projectboard.core.base.rest.project.persistence.ProjectRepository;
import de.adesso.projectboard.core.base.rest.user.application.ProjectApplicationHandler;
import de.adesso.projectboard.core.base.rest.user.application.dto.ProjectApplicationRequestDTO;
import de.adesso.projectboard.core.base.rest.user.application.dto.ProjectApplicationResponseDTO;
import de.adesso.projectboard.core.base.rest.user.application.persistence.ProjectApplication;
import de.adesso.projectboard.core.base.rest.user.bookmark.dto.BookmarkRequestDTO;
import de.adesso.projectboard.core.base.rest.user.dto.UserResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * {@link RestController} for user related data.
 */
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    private final ProjectRepository projectRepo;

    private final ProjectApplicationHandler applicationHandler;

    @Autowired
    public UserController(UserService userService,
                          ProjectRepository projectRepo,
                          ProjectApplicationHandler applicationHandler) {
        this.userService = userService;
        this.projectRepo = projectRepo;
        this.applicationHandler = applicationHandler;
    }

    @PreAuthorize("hasPermissionToAccessUser(#userId) || hasRole('admin')")
    @GetMapping(path = "/{userId}",
            produces = "application/json"
    )
    public UserResponseDTO getUserById(@PathVariable("userId") String userId) throws UserNotFoundException {
        return UserResponseDTO.fromUser(userService.getUserById(userId));
    }

    @PreAuthorize("hasPermissionToAccessUser(#userId) || hasRole('admin')")
    @DeleteMapping(value = "/{userId}/bookmarks/{projectId}",
            produces = "application/json"
    )
    public void deleteBookmarkOfUser(@PathVariable("userId") String userId, @PathVariable("projectId") long projectId)
            throws UserNotFoundException, ProjectNotFoundException, BookmarkNotFoundException {
        userService.removeBookmarkFromUser(userId, projectId);
    }

    /**
     *
     * @param bookmarkClientDTO
     *          The {@link BookmarkRequestDTO} sent by the user.
     *
     * @return
     *          The {@link AbstractProject} bookmarked.
     *
     * @throws ProjectNotFoundException
     *          When no {@link AbstractProject project} is found for the
     *          given {@link BookmarkRequestDTO#getProjectId() id}.
     */
    @PreAuthorize("(hasPermissionToAccessUser(#userId) && hasAccessToProjects()) || hasRole('admin')")
    @PostMapping(value = "/{userId}/bookmarks",
            consumes = "application/json",
            produces = "application/json"
    )
    public AbstractProject createBookmarkForUser(@Valid @RequestBody BookmarkRequestDTO bookmarkClientDTO, @PathVariable("userId") String userId)
            throws ProjectNotFoundException {
        return userService.addBookmarkToUser(userId, bookmarkClientDTO.getProjectId());
    }

    @PreAuthorize("hasPermissionToAccessUser(#userId) || hasRole('admin')")
    @GetMapping(path = "/{userId}/bookmarks",
            produces = "application/json"
    )
    public Iterable<AbstractProject> getBookmarksOfUser(@PathVariable("userId") String userId) throws UserNotFoundException {
        return userService.getUserById(userId).getBookmarks();
    }

    /**
     *
     * @param projectApplicationClientDTO
     *          The {@link ProjectApplicationRequestDTO} send by the user.
     *
     * @return
     *          The {@link ProjectApplicationResponseDTO} of the created {@link ProjectApplication}.
     *
     * @throws ProjectNotFoundException
     *          When the {@link AbstractProject} with the {@link ProjectApplicationRequestDTO#getProjectId() given id}
     *          is not found.
     */
    @PreAuthorize("hasPermissionToApply() || hasRole('admin')")
    @PostMapping(path = "/{userId}/applications",
            consumes = "application/json",
            produces = "application/json"
    )
    public ProjectApplicationResponseDTO createApplicationForUser(@Valid @RequestBody ProjectApplicationRequestDTO projectApplicationClientDTO, @PathVariable("userId") String userId)
            throws ProjectNotFoundException, UserNotFoundException {

        // get the project by the given id
        Optional<AbstractProject> projectOptional = projectRepo.findById(projectApplicationClientDTO.getProjectId());
        if(!projectOptional.isPresent()) {
            throw new ProjectNotFoundException();
        }

        // create a new project application instance
        ProjectApplication application
                = new ProjectApplication(projectOptional.get(), projectApplicationClientDTO.getComment(), userService.getCurrentUser());

        // call the handler method
        applicationHandler.onApplicationReceived(application);

        ProjectApplication savedApplication
                = userService.addApplicationToUser(userId, application);

        return ProjectApplicationResponseDTO.fromApplication(savedApplication);
    }

    @PreAuthorize("hasPermissionToAccessUser(#userId) || hasRole('admin')")
    @GetMapping(path = "/{userId}/applications",
            produces = "application/json"
    )
    public Iterable<ProjectApplicationResponseDTO> getApplicationsOfUser(@PathVariable("userId") String userId) throws UserNotFoundException {
        return userService.getUserById(userId).getApplications().stream()
                .map(ProjectApplicationResponseDTO::fromApplication)
                .collect(Collectors.toList());
    }

}
