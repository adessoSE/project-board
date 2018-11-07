package de.adesso.projectboard.project.service;

import de.adesso.projectboard.base.application.persistence.ProjectApplication;
import de.adesso.projectboard.base.application.persistence.ProjectApplicationRepository;
import de.adesso.projectboard.base.exceptions.ProjectNotEditableException;
import de.adesso.projectboard.base.exceptions.ProjectNotFoundException;
import de.adesso.projectboard.base.exceptions.UserNotFoundException;
import de.adesso.projectboard.base.project.dto.ProjectRequestDTO;
import de.adesso.projectboard.base.project.persistence.Project;
import de.adesso.projectboard.base.project.persistence.ProjectOrigin;
import de.adesso.projectboard.base.project.persistence.ProjectRepository;
import de.adesso.projectboard.base.project.service.ProjectService;
import de.adesso.projectboard.base.user.persistence.User;
import de.adesso.projectboard.base.user.persistence.UserRepository;
import de.adesso.projectboard.base.user.service.UserProjectService;
import de.adesso.projectboard.base.user.service.UserService;
import de.adesso.projectboard.base.util.Sorting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class RepositoryProjectService implements ProjectService, UserProjectService {

    private final ProjectRepository projectRepo;

    private final ProjectApplicationRepository applicationRepo;

    private final UserRepository userRepo;

    private final UserService userService;

    @Autowired
    public RepositoryProjectService(ProjectRepository projectRepo,
                                    ProjectApplicationRepository applicationRepo,
                                    UserRepository userRepo,
                                    UserService userService) {
        this.projectRepo = projectRepo;
        this.applicationRepo = applicationRepo;
        this.userRepo = userRepo;
        this.userService = userService;
    }

    @Override
    public Project getProjectById(String projectId) throws ProjectNotFoundException {
        Optional<Project> projectOptional = projectRepo.findById(projectId);

        return projectOptional.orElseThrow(ProjectNotFoundException::new);
    }

    @Override
    public boolean projectExists(String projectId) {
        return projectRepo.existsById(projectId);
    }

    @Override
    public Project updateProject(ProjectRequestDTO projectDTO, String projectId) throws ProjectNotFoundException, ProjectNotEditableException {
        Project existingProject = getProjectById(projectId);

        if(ProjectOrigin.CUSTOM.equals(existingProject.getOrigin())) {
            return createOrUpdateProject(projectDTO, projectId);
        } else {
            throw new ProjectNotEditableException();
        }
    }

    @Override
    public void deleteProject(Project project) {
        deleteProjectById(project.getId());
    }

    @Override
    public Project createProject(ProjectRequestDTO projectDTO) throws UserNotFoundException {
        return createOrUpdateProject(projectDTO, null);
    }

    @Override
    public void deleteProjectById(String projectId) throws ProjectNotFoundException, ProjectNotEditableException {
        Project existingProject = getProjectById(projectId);

        if(ProjectOrigin.JIRA.equals(existingProject.getOrigin())) {
            throw new ProjectNotEditableException();
        }

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

        projectRepo.delete(existingProject);
    }

    private Project createOrUpdateProject(ProjectRequestDTO projectDTO, String projectId) {
        LocalDateTime updateTime = LocalDateTime.now();
        LocalDateTime createTime = LocalDateTime.now();

        if(projectId != null && projectExists(projectId)) {
            Project existingProject = getProjectById(projectId);

            createTime = existingProject.getCreated();
        }

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
                .setCreated(createTime)
                .setUpdated(updateTime)
                .setFreelancer(projectDTO.getFreelancer())
                .setElongation(projectDTO.getElongation())
                .setOther(projectDTO.getOther())
                .setOrigin(ProjectOrigin.CUSTOM);

        return projectRepo.save(project);
    }

    @Override
    public List<Project> getProjectsForUser(String userId, Sorting sorting) throws UserNotFoundException {
        if(userService.userIsManager(userId)) {
            return projectRepo.findAllByStatusEscalatedOrOpen(sorting.toSort());
        } else {
            String lob = userService.getUserData(userId).getLob();

            return projectRepo.findAllByStatusEscalatedOrOpenOrSameLob(lob, sorting.toSort());
        }
    }

    @Override
    public List<Project> searchProjectsForUser(String userId, String keyword, Sorting sorting) throws UserNotFoundException {
        // TODO: implement
        return Collections.emptyList();
    }

    @Override
    public boolean userOwnsProject(String userId, String projectId) throws UserNotFoundException, ProjectNotFoundException {
        return userRepo.existsByIdAndOwnedProjectsContaining(userId, getProjectById(projectId));
    }

    @Override
    public Project createProjectForUser(ProjectRequestDTO projectDTO, String userId) throws UserNotFoundException {
        User user = userService.getUserById(userId);
        Project createdProject = createProject(projectDTO);

        user.addOwnedProject(createdProject);
        userService.save(user);

        return createdProject;
    }

    @Override
    public Project addProjectToUser(String userId, String projectId) throws UserNotFoundException, ProjectNotFoundException {
        User user = userService.getUserById(userId);
        Project project = getProjectById(projectId);

        user.addOwnedProject(project);

        return project;
    }

}
