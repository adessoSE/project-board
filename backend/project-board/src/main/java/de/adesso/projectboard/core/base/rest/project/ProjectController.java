package de.adesso.projectboard.core.base.rest.project;

import de.adesso.projectboard.core.base.configuration.ProjectBoardConfigurationProperties;
import de.adesso.projectboard.core.base.rest.exceptions.ProjectNotFoundException;
import de.adesso.projectboard.core.base.rest.project.persistence.Project;
import de.adesso.projectboard.core.base.rest.project.persistence.ProjectRepository;
import de.adesso.projectboard.core.base.rest.user.UserService;
import de.adesso.projectboard.core.base.rest.user.persistence.SuperUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityManager;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectRepository projectRepository;

    private final ProjectBoardConfigurationProperties properties;

    private final EntityManager entityManager;

    private final UserService userService;

    @Autowired
    public ProjectController(ProjectRepository projectRepository,
                             ProjectBoardConfigurationProperties properties,
                             EntityManager entityManager, UserService userService) {
        this.projectRepository = projectRepository;
        this.properties = properties;
        this.entityManager = entityManager;
        this.userService = userService;
    }


    @PreAuthorize("hasAccessToProject(#projectId) || hasRole('admin')")
    @GetMapping(value = "/{projectId}",
            produces = "application/json"
    )
    public Project getById(@PathVariable long projectId) {
        Optional<Project> projectOptional = projectRepository.findById(projectId);

        if(projectOptional.isPresent()) {
            return projectOptional.get();
        } else {
            throw new ProjectNotFoundException();
        }
    }

    @PreAuthorize("hasRole('admin')")
    @GetMapping(path = "/all",
            produces = "application/json"
    )
    public Iterable<? extends Project> getAll() {
        return projectRepository.findAll();
    }

    @PreAuthorize("hasAccessToProjects() || hasRole('admin')")
    @GetMapping(produces = "application/json")
    public Iterable<? extends Project> getAllForUser() {
        final String userLob = userService.getCurrentUser().getLob();
        final boolean isSuperUser = userService.getCurrentUser() instanceof SuperUser;

        return StreamSupport.stream(projectRepository.findAll().spliterator(), false)
                .filter(project -> {

                    String projectLob = project.getLob();
                    final boolean projectHasLob = projectLob != null;
                    boolean projectIsEscalated = "eskaliert".equalsIgnoreCase(project.getStatus());
                    boolean projectIsOpen = "offen".equalsIgnoreCase(project.getStatus());

                    // superusers have access to all open and escalated projects
                    if(isSuperUser && (projectIsOpen || projectIsEscalated)) {
                        return true;
                    }

                    // exclude projects with a different status than "Offen" or "eskaliert"
                    if(projectIsEscalated || (!projectHasLob && projectIsOpen)) {
                        return true;
                    }

                    if(projectHasLob && projectIsOpen) {
                        return userLob.equalsIgnoreCase(projectLob);
                    }

                    return false;
                })
                .collect(Collectors.toList());
    }

}
