package de.adesso.projectboard.core.base.rest.project.service;

import de.adesso.projectboard.core.base.rest.exceptions.ProjectNotEditableException;
import de.adesso.projectboard.core.base.rest.exceptions.ProjectNotFoundException;
import de.adesso.projectboard.core.base.rest.exceptions.UserNotFoundException;
import de.adesso.projectboard.core.base.rest.project.dto.ProjectRequestDTO;
import de.adesso.projectboard.core.base.rest.project.persistence.Project;
import de.adesso.projectboard.core.base.rest.project.persistence.ProjectRepository;
import de.adesso.projectboard.core.base.rest.user.persistence.SuperUser;
import de.adesso.projectboard.core.base.rest.user.persistence.User;
import de.adesso.projectboard.core.base.rest.user.persistence.UserRepository;
import de.adesso.projectboard.core.base.rest.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

/**
 *
 * @see ProjectRepository
 * @see UserService
 */
@Service
public class ProjectService {

    private final ProjectRepository projectRepo;

    private final UserRepository userRepo;

    private final UserService userService;

    @Autowired
    public ProjectService(ProjectRepository projectRepo, UserRepository userRepo, UserService userService) {
        this.projectRepo = projectRepo;
        this.userRepo = userRepo;
        this.userService = userService;
    }

    /**
     *
     * @param projectId
     *          The id of the {@link Project}.
     *
     * @return
     *          The {@link Project} with the given {@code projectId}.
     *
     * @throws ProjectNotFoundException
     *          When no {@link Project} with the given {@code projectId} is
     *          found.
     */
    public Project getProjectById(String projectId) throws ProjectNotFoundException {
        Optional<Project> projectOptional = projectRepo.findById(projectId);

        if(projectOptional.isPresent()) {
            return projectOptional.get();
        } else {
            throw new ProjectNotFoundException();
        }
    }

    /**
     *
     * @param projectId
     *          The id of the {@link Project}.
     *
     * @return
     *          {@code true}, when a {@link Project} with the given {@code projectId}
     *          exists, {@code false} otherwise.
     *
     * @see ProjectRepository#existsById(Object)
     */
    public boolean projectExists(String projectId) {
        return projectRepo.existsById(projectId);
    }

    /**
     *
     * @param userId
     *          The id of the {@link User}
     *
     * @param projectId
     *          The id of the {@link Project}.
     *
     * @return
     *          {@code true}, when a {@link Project} with the given id is present
     *          in the {@link User#createdProjects created projects}
     *          of the {@link User} with the given {@code userId}, {@code false} otherwise.
     *
     * @see #projectExists(String)
     * @see #getProjectById(String)
     * @see UserRepository#existsByIdAndCreatedProjectsContaining(String, Project)
     */
    public boolean userHasProject(String userId, String projectId) {
        if(projectExists(projectId)) {
            Project project = getProjectById(projectId);

            return userRepo.existsByIdAndCreatedProjectsContaining(userId, project);
        } else {
            return false;
        }
    }

    /**
     * Gets all projects the user is authorized to see. {@link SuperUser} get all
     * {@link Project}s with the status <i>offen</i> and <i>eskaliert</i>. Normal
     * {@link User}s only see {@link Project}s with the status <i>eskaliert</i>
     * and <i>offen</i>, if the {@link Project#lob} of the project is the same
     * as the user's {@link User#lob}.
     *
     * @param user
     *          The user to get the set of {@link Project}s for.
     *
     * @return
     *          A {@link Set} of {@link Project}s.
     *
     * @see ProjectRepository#getAllForUserOfLob(String)
     * @see ProjectRepository#getAllForSuperUser()
     */
    public List<Project> getProjectsForUser(User user) {
        if(user instanceof SuperUser) {
            return projectRepo.getAllForSuperUser();
        } else {
            return projectRepo.getAllForUserOfLob(user.getLob());
        }
    }

    /**
     * Creates or updates a {@link Project} from a {@link ProjectRequestDTO}.
     *
     * @param projectDTO
     *          The {@link ProjectRequestDTO} to create the {@link Project from}.
     *
     * @param projectId
     *          The id of the {@link Project}.
     *
     * @return
     *          The saved {@link Project}.
     */
    private Project createOrUpdateProject(ProjectRequestDTO projectDTO, String projectId) {
        LocalDateTime createdUpdatedTime = LocalDateTime.now();

        Project project = Project.builder()
                .id(projectId)
                .status(projectDTO.getStatus())
                .issuetype(projectDTO.getIssuetype())
                .title(projectDTO.getTitle())
                .labels(projectDTO.getLabels())
                .job(projectDTO.getJob())
                .skills(projectDTO.getSkills())
                .description(projectDTO.getDescription())
                .lob(projectDTO.getLob())
                .customer(projectDTO.getCustomer())
                .location(projectDTO.getLocation())
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

        return projectRepo.save(project);
    }

    /**
     *
     * @param projectDTO
     *          The {@link ProjectRequestDTO} to update the {@link Project}.
     *
     * @param projectId
     *          The id of the existing {@link Project}.
     *
     * @return
     *          The updated {@link Project}.
     *
     * @throws ProjectNotFoundException
     *          When no {@link Project} with the given {@code projectId} is found.
     *
     * @throws ProjectNotEditableException
     *          When the exiting project with the given {@code projectId} is not {@link Project#editable}.
     */
    public Project updateProject(ProjectRequestDTO projectDTO, String projectId) throws ProjectNotFoundException, ProjectNotEditableException {
        Project existingProject = getProjectById(projectId);

        if(existingProject.isEditable()) {
            return createOrUpdateProject(projectDTO, projectId);
        } else {
            throw new ProjectNotEditableException();
        }
    }

    /**
     * Creates {@link Project} from a {@link ProjectRequestDTO}. The id is automatically
     * generated.
     *
     * @param projectDTO
     *          The {@link ProjectRequestDTO} to create the {@link Project from}.
     *
     * @param userId
     *          The id of the {@link User} that creates the project.
     *
     * @return
     *          The {@link Project} returned by {@link #createOrUpdateProject(ProjectRequestDTO, String)}
     *
     * @throws UserNotFoundException
     *          When no {@link User} with the given {@code userId} was found.
     *
     * @see #createOrUpdateProject(ProjectRequestDTO, String)
     */
    public Project createProject(ProjectRequestDTO projectDTO, String userId) throws UserNotFoundException {
        User creatingUser = userService.getUserById(userId);

        Project createdProject = createOrUpdateProject(projectDTO, null);

        creatingUser.addCreatedProject(createdProject);
        userService.save(creatingUser);

        return createdProject;
    }

    /**
     *
     * @param projectId
     *          The id of the {@link Project} to delete.
     *
     * @throws ProjectNotFoundException
     *          When no {@link Project} with the given {@code projectId} was found.
     *
     * @throws ProjectNotEditableException
     *          When the project with the given {@code projectId} is not {@link Project#editable}.
     */
    public void deleteProjectById(String projectId) throws ProjectNotFoundException, ProjectNotEditableException {
        Project existingProject = getProjectById(projectId);

        if(existingProject.isEditable()) {
            // remove it from the user's created projects
            Optional<User> optionalUser
                    = userRepo.findByCreatedProjectsContaining(existingProject);
            optionalUser.ifPresent(user -> user.removeCreatedProject(existingProject));

            // delete the project
            projectRepo.delete(existingProject);
        } else {
            throw new ProjectNotEditableException();
        }
    }

}
