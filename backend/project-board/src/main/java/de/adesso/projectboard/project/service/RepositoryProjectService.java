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
import de.adesso.projectboard.base.user.service.PageableUserProjectService;
import de.adesso.projectboard.base.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class RepositoryProjectService implements ProjectService, PageableUserProjectService {

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
        // removed by orphan removal
        List<ProjectApplication> applications =
                applicationRepo.findAllByProjectEquals(existingProject);
        applications.forEach(application -> {
            User user = application.getUser();
            user.removeApplication(application);

            userService.save(user);
        });

        projectRepo.delete(existingProject);
    }

    Project createOrUpdateProject(ProjectRequestDTO projectDTO, String projectId) {
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
    public List<Project> getProjectsForUser(User user, Sort sort) {
        if(userService.userIsManager(user)) {
            return projectRepo.findAllForManager(sort);
        } else {
            String lob = userService.getUserData(user).getLob();

            return projectRepo.findAllForUser(lob, sort);
        }
    }

    @Override
    public List<Project> searchProjectsForUser(User user, String keyword, Sort sort) {
        if(userService.userIsManager(user)) {
            return projectRepo.findAllForManagerByKeyword(keyword, sort);
        } else {
            String lob = userService.getUserData(user).getLob();

            return projectRepo.findAllForUserByKeyword(lob, keyword, sort);
        }
    }

    @Override
    public boolean userOwnsProject(User user, Project project) {
        return userRepo.existsByIdAndOwnedProjectsContaining(user.getId(), project);
    }

    @Override
    public Project createProjectForUser(ProjectRequestDTO projectDTO, User user) {
        // check if a valid user instance was passed
        userService.validateExistence(user);

        Project createdProject = createProject(projectDTO);

        user.addOwnedProject(createdProject);
        userService.save(user);

        return createdProject;
    }

    @Override
    public Project addProjectToUser(User user, Project project) {
        user.addOwnedProject(project);
        userService.save(user);

        return project;
    }

    @Override
    public Page<Project> getProjectsForUserPaginated(User user, Pageable pageable) {
        if(userService.userIsManager(user)) {
            return projectRepo.findAllForManagerPageable(pageable);
        } else {
            String lob = userService.getUserData(user).getLob();

            return projectRepo.findAllForUserPageable(lob, pageable);
        }
    }

    @Override
    public Page<Project> searchProjectsForUserPaginated(String keyword, User user, Pageable pageable) {
        if(userService.userIsManager(user)) {
            return projectRepo.findAllForManagerByKeywordPageable(keyword, pageable);
        } else {
            String lob = userService.getUserData(user).getLob();

            return projectRepo.findAllForUserByKeywordPageable(lob, keyword, pageable);
        }
    }

}
