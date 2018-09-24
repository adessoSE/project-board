package de.adesso.projectboard.core.base.rest.project;

import de.adesso.projectboard.core.base.configuration.ProjectBoardConfigurationProperties;
import de.adesso.projectboard.core.base.rest.exceptions.ProjectNotEditableException;
import de.adesso.projectboard.core.base.rest.exceptions.ProjectNotFoundException;
import de.adesso.projectboard.core.base.rest.project.dto.ProjectRequestDTO;
import de.adesso.projectboard.core.base.rest.project.persistence.Project;
import de.adesso.projectboard.core.base.rest.project.persistence.ProjectRepository;
import de.adesso.projectboard.core.base.rest.user.UserService;
import de.adesso.projectboard.core.base.rest.user.persistence.SuperUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/projects")
public class ProjectController {

    private final ProjectRepository projectRepository;

    private final ProjectBoardConfigurationProperties properties;

    private final UserService userService;

    @Autowired
    public ProjectController(ProjectRepository projectRepository,
                             ProjectBoardConfigurationProperties properties,
                             UserService userService) {
        this.projectRepository = projectRepository;
        this.properties = properties;
        this.userService = userService;
    }


    @PreAuthorize("hasAccessToProject(#projectId) || hasRole('admin')")
    @GetMapping(value = "/{projectId}",
            produces = "application/json"
    )
    public Project getById(@PathVariable String projectId) {
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

        return StreamSupport.stream(projectRepository.findAll().spliterator(), true)
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

    @PreAuthorize("hasPermissionToCreateProjects() || hasRole('admin')")
    @PostMapping(consumes = "application/json",
            produces = "application/json"
    )
    public Project createProject(@Valid @RequestBody ProjectRequestDTO projectDTO) {
        LocalDateTime createdUpdatedTime = LocalDateTime.now();

        Project project = Project.builder()
                .status(projectDTO.getStatus())
                .issuetype(projectDTO.getIssuetype())
                .title(projectDTO.getTitle())
                .labels(projectDTO.getLabels())
                .job(projectDTO.getJob())
                .skills(projectDTO.getSkills())
                .description(projectDTO.getDescription())
                .lob(projectDTO.getLob())
                .customer(projectDTO.getCustomer())
                .lob(projectDTO.getLocation())
                .operationStart(projectDTO.getOperationStart())
                .operationEnd(projectDTO.getOperationEnd())
                .effort(projectDTO.getEffort())
                .created(createdUpdatedTime)
                .updated(createdUpdatedTime)
                .freelancer(projectDTO.getFreelancer())
                .elongation(projectDTO.getElongation())
                .other(projectDTO.getOther())
                .editable(true)
                .build();

        return projectRepository.save(project);
    }

    @PreAuthorize("hasPermissionToUpdateProject(#projectId) || hasRole('admin')")
    @PutMapping(
            path = "/{projectId}",
            consumes = "application/json",
            produces = "application/json"
    )
    public Project updateProject(@PathVariable String projectId, @Valid @RequestBody ProjectRequestDTO projectDTO)
            throws ProjectNotFoundException, ProjectNotEditableException {

        Optional<Project> optionalProject = projectRepository.findById(projectId);
        if(!optionalProject.isPresent()) {
            throw new ProjectNotFoundException();
        }

        Project existingProject = optionalProject.get();
        if(!existingProject.isEditable()) {
            throw new ProjectNotEditableException();
        }

        Project project = Project.builder()
                .id(existingProject.getId())
                .status(projectDTO.getStatus())
                .issuetype(projectDTO.getIssuetype())
                .title(projectDTO.getTitle())
                .labels(projectDTO.getLabels())
                .job(projectDTO.getJob())
                .skills(projectDTO.getSkills())
                .description(projectDTO.getDescription())
                .lob(projectDTO.getLob())
                .customer(projectDTO.getCustomer())
                .lob(projectDTO.getLocation())
                .operationStart(projectDTO.getOperationStart())
                .operationEnd(projectDTO.getOperationEnd())
                .effort(projectDTO.getEffort())
                .created(existingProject.getCreated())
                .updated(LocalDateTime.now())
                .freelancer(projectDTO.getFreelancer())
                .elongation(projectDTO.getElongation())
                .other(projectDTO.getOther())
                .editable(true)
                .build();

        return projectRepository.save(project);
    }

    @PreAuthorize("hasPermissionToUpdateProject(#projectId) || hasRole('admin')")
    @DeleteMapping(
            path = "/{projectId}"
    )
    public void deleteProject(@PathVariable String projectId) throws ProjectNotEditableException {
        Optional<Project> optionalProject = projectRepository.findById(projectId);
        if(!optionalProject.isPresent()) {
            throw new ProjectNotFoundException();
        }

        Project project = optionalProject.get();
        if(project.isEditable()) {
            projectRepository.delete(project);
        } else {
            throw new ProjectNotEditableException();
        }
    }

}
