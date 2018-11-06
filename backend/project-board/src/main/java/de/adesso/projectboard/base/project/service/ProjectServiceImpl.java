package de.adesso.projectboard.base.project.service;

import de.adesso.projectboard.base.application.persistence.ProjectApplication;
import de.adesso.projectboard.base.application.persistence.ProjectApplicationRepository;
import de.adesso.projectboard.base.exceptions.ProjectNotEditableException;
import de.adesso.projectboard.base.exceptions.ProjectNotFoundException;
import de.adesso.projectboard.base.exceptions.UserNotFoundException;
import de.adesso.projectboard.base.project.dto.ProjectRequestDTO;
import de.adesso.projectboard.base.project.persistence.Project;
import de.adesso.projectboard.base.project.persistence.ProjectOrigin;
import de.adesso.projectboard.base.project.persistence.ProjectRepository;
import de.adesso.projectboard.base.user.persistence.User;
import de.adesso.projectboard.base.user.persistence.UserRepository;
import de.adesso.projectboard.ldap.user.LdapUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * {@link Service} to provide functionality to manage {@link Project}s.
 *
 * @see ProjectRepository
 * @see LdapUserService
 */
@Service
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepo;

    private final ProjectApplicationRepository applicationRepo;

    private final UserRepository userRepo;

    private final LdapUserService userService;

    @Autowired
    public ProjectServiceImpl(ProjectRepository projectRepo,
                              ProjectApplicationRepository applicationRepo,
                              UserRepository userRepo,
                              LdapUserService userService) {
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
     *          When no {@link Project} with the given {@code projectId} was
     *          found.
     *
     * @see ProjectRepository#findById(Object)
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
     *          The id of the {@link User}.
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
     * @see UserRepository#existsByIdAndOwnedProjectsContaining(String, Project)
     */
    public boolean userHasProject(String userId, String projectId) {
        if(projectExists(projectId)) {
            Project project = getProjectById(projectId);

            return userRepo.existsByIdAndOwnedProjectsContaining(userId, project);
        } else {
            return false;
        }
    }

    /**
     * Gets all projects the user is authorized to see. {@link SuperUser}s get all
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
     *          The {@link List} of all {@link Project}s containing the keyword in the
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
     * Creates a new or updates a existing {@link Project} from a {@link ProjectRequestDTO}.
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
                .setOrigin(ProjectOrigin.CUSTOM);

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
     *          When no {@link Project} with the given {@code projectId} was found.
     *
     * @throws ProjectNotEditableException
     *          When the {@link Project} exists but it's {@link Project#getOrigin() origin}
     *          is not equal to {@link ProjectOrigin#CUSTOM}.
     */
    public Project updateProject(ProjectRequestDTO projectDTO, String projectId) throws ProjectNotFoundException, ProjectNotEditableException {
        Project existingProject = getProjectById(projectId);

        if(ProjectOrigin.CUSTOM.equals(existingProject.getOrigin())) {
            return createOrUpdateProject(projectDTO, projectId);
        } else {
            throw new ProjectNotEditableException();
        }
    }

    /**
     * Creates {@link Project} from a {@link ProjectRequestDTO}. The ID is automatically
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

        creatingUser.addOwnedProject(createdProject);
        userService.save(creatingUser);

        return createdProject;
    }

    /**
     * Deletes a {@link Project} by its ID. Removes it from all {@link User#createdProjects created projects},
     * {@link User#bookmarks bookmarks} and removes all {@link ProjectApplication}s referring to the project.
     *
     * @param projectId
     *          The id of the {@link Project} to delete.
     *
     * @throws ProjectNotFoundException
     *          When no {@link Project} with the given {@code projectId} was found.
     *
     * @throws ProjectNotEditableException
     *          When the project's {@link Project#origin} with the given {@code projectId} is
     *          not equal to {@link ProjectOrigin#CUSTOM} (it's not editable).
     */
    public void deleteProjectById(String projectId) throws ProjectNotFoundException, ProjectNotEditableException {
        Project existingProject = getProjectById(projectId);

        if(ProjectOrigin.CUSTOM.equals(existingProject.getOrigin())) {
            // remove it from the user's created projects
            List<User> creators
                    = userRepo.findAllByOwnedProjectsContaining(existingProject);
            creators.forEach(user -> {
                user.removeOwnedProject(existingProject);

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
