package de.adesso.projectboard.core.base.rest.project.service;

import de.adesso.projectboard.core.base.rest.exceptions.ProjectNotEditableException;
import de.adesso.projectboard.core.base.rest.exceptions.ProjectNotFoundException;
import de.adesso.projectboard.core.base.rest.exceptions.UserNotFoundException;
import de.adesso.projectboard.core.base.rest.project.dto.ProjectRequestDTO;
import de.adesso.projectboard.core.base.rest.project.persistence.Project;
import de.adesso.projectboard.core.base.rest.project.persistence.ProjectRepository;
import de.adesso.projectboard.core.base.rest.user.application.persistence.ProjectApplication;
import de.adesso.projectboard.core.base.rest.user.application.persistence.ProjectApplicationRepository;
import de.adesso.projectboard.core.base.rest.user.persistence.SuperUser;
import de.adesso.projectboard.core.base.rest.user.persistence.User;
import de.adesso.projectboard.core.base.rest.user.persistence.UserRepository;
import de.adesso.projectboard.core.base.rest.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 *
 * @see ProjectRepository
 * @see UserService
 */
@Service
public class ProjectService {

    private final ProjectRepository projectRepo;

    private final ProjectApplicationRepository applicationRepo;

    private final UserRepository userRepo;

    private final UserService userService;

    @Autowired
    public ProjectService(ProjectRepository projectRepo,
                          ProjectApplicationRepository applicationRepo,
                          UserRepository userRepo,
                          UserService userService) {
        this.projectRepo = projectRepo;
        this.applicationRepo = applicationRepo;
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
     * @param sort
     *          The {@link Sort} to apply.
     *
     * @return
     *          A {@link Set} of {@link Project}s.
     *
     * @see ProjectRepository#findAllByStatusEscalatedOrOpen(Sort)
     * @see ProjectRepository#findAllByStatusEscalatedOrOpenOrSameLob(String, Sort)
     */
    public List<Project> getProjectsForUser(User user, Sort sort) {
        if(user instanceof SuperUser) {
            return projectRepo.findAllByStatusEscalatedOrOpen(sort);
        } else {
            return projectRepo.findAllByStatusEscalatedOrOpenOrSameLob(user.getLob(), sort);
        }
    }

    /**
     *
     * @param user
     *          The {@link User} to get the projects containing the {@code keyword}
     *          for.
     *
     * @param keyword
     *          The keyword to search for.
     *
     * @param sort
     *          The {@link Sort} to apply.
     *
     * @return
     *          The {@link List} of all {@link Project}s contatining the keyword in the
     *          {@link Project#title}, {@link Project#description}, {@link Project#job}
     *          or {@link Project#skills} field matching the same authorization as described
     *          in the {{@link #getProjectsForUser(User, Sort)}} documentation.
     *
     */
    public List<Project> getProjectsForUserContainingKeyword(User user, String keyword, Sort sort) {
        if(user instanceof SuperUser) {
            return projectRepo.findAllByStatusEscalatedOrOpenContainsKeyword(keyword, sort);
        } else {
            return projectRepo.findAllByStatusEscalatedOrOpenOrSameLobContainsKeyword(user.getLob(), keyword, sort);
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

        Project project = new Project()
                .setId(projectId)
                .setStatus(projectDTO.getStatus())
                .setIssuetype(projectDTO.getIssuetype())
                .setTitle(projectDTO.getTitle())
                .setLabels(projectDTO.getLabels())
                .setJob(projectDTO.getJob())
                .setSkills(projectDTO.getSkills())
                .setDescription(projectDTO.getDescription())
                .setLob(projectDTO.getLob())
                .setCustomer(projectDTO.getCustomer())
                .setLocation(projectDTO.getLocation())
                .setOperationStart(projectDTO.getOperationStart())
                .setOperationEnd(projectDTO.getOperationEnd())
                .setEffort(projectDTO.getEffort())
                .setCreated(createdUpdatedTime)
                .setUpdated(createdUpdatedTime)
                .setFreelancer(projectDTO.getFreelancer())
                .setElongation(projectDTO.getElongation())
                .setOther(projectDTO.getOther())
                .setEditable(true);

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
            List<User> creators
                    = userRepo.findAllByCreatedProjectsContaining(existingProject);
            creators.forEach(user -> {
                user.removeCreatedProject(existingProject);

                userService.save(user);
            });

            // remove it from the user's bookmarks
            List<User> bookmarkers
                    = userRepo.findAllByBookmarksContaining(existingProject);
            bookmarkers.forEach(user -> {
                user.removeBookmark(existingProject);

                userService.save(user);
            });

            // remove applications referring to this project
            List<ProjectApplication> applications =
                    applicationRepo.findAllByProjectEquals(existingProject);
            applications.forEach(applicationRepo::delete);

            // delete the project
            projectRepo.delete(existingProject);
        } else {
            throw new ProjectNotEditableException();
        }
    }

}
