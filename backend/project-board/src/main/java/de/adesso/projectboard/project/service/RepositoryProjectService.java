package de.adesso.projectboard.project.service;

import de.adesso.projectboard.base.application.persistence.ProjectApplication;
import de.adesso.projectboard.base.application.persistence.ProjectApplicationRepository;
import de.adesso.projectboard.base.exceptions.ProjectNotEditableException;
import de.adesso.projectboard.base.exceptions.ProjectNotFoundException;
import de.adesso.projectboard.base.project.dto.ProjectDtoMapper;
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
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class RepositoryProjectService implements ProjectService, PageableUserProjectService {

    private final ProjectRepository projectRepo;

    private final ProjectApplicationRepository applicationRepo;

    private final UserRepository userRepo;

    private final UserService userService;

    private final Clock clock;

    @Autowired
    public RepositoryProjectService(ProjectRepository projectRepo,
                                    ProjectApplicationRepository applicationRepo,
                                    UserRepository userRepo,
                                    UserService userService) {
        this.projectRepo = projectRepo;
        this.applicationRepo = applicationRepo;
        this.userRepo = userRepo;
        this.userService = userService;

        this.clock = Clock.systemDefaultZone();
    }

    /**
     * Package private default constructor for testing purposes.
     *
     * @param projectRepo
     *          The {@link ProjectRepository}.
     *
     * @param applicationRepo
     *          The {@link ProjectApplicationRepository}.
     *
     * @param userRepo
     *          The {@link UserRepository}.
     *
     * @param userService
     *          The {@link UserService}.
     *
     * @param clock
     *          The {@link Clock} to use to generate the
     *          current time with.
     */
    RepositoryProjectService(ProjectRepository projectRepo,
                             ProjectApplicationRepository applicationRepo,
                             UserRepository userRepo,
                             UserService userService,
                             Clock clock) {
        this.projectRepo = projectRepo;
        this.applicationRepo = applicationRepo;
        this.userRepo = userRepo;
        this.userService = userService;
        this.clock = clock;
    }

    @Override
    @Transactional(readOnly = true)
    public Project getProjectById(String projectId) throws ProjectNotFoundException {
        Optional<Project> projectOptional = projectRepo.findById(projectId);

        return projectOptional.orElseThrow(ProjectNotFoundException::new);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean projectExists(String projectId) {
        return projectRepo.existsById(projectId);
    }

    /**
     *
     * @param project
     *          The {@link Project} to update the exising
     *          project from
     *
     * @param projectId
     *          The {@link Project#id ID} of the {@link Project}
     *          to update.
     *
     * @return
     *          The updated {@link Project}.
     *
     * @throws ProjectNotEditableException
     *          When the {@link Project}'s {@link Project#origin origin} is not set to
     *          {@link ProjectOrigin#CUSTOM}.
     */
    @Override
    @Transactional
    public Project updateProject(Project project, String projectId) throws ProjectNotEditableException {
        Project existingProject = getProjectById(projectId);

        if(ProjectOrigin.CUSTOM.equals(existingProject.getOrigin())) {
            return createOrUpdateProject(project, projectId);
        } else {
            throw new ProjectNotEditableException();
        }
    }

    @Override
    public void deleteProject(Project project) {
        deleteProjectById(project.getId());
    }

    @Override
    public Project createProject(Project project) {
        return createOrUpdateProject(project, null);
    }

    @Override
    @Transactional
    public void deleteProjectById(String projectId) throws ProjectNotEditableException {
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
        applicationRepo.deleteAll(applications);

        projectRepo.delete(existingProject);
    }

    /**
     *
     * @param project
     *          The {@link Project} to create/update a project from.
     *
     * @param projectId
     *          The {@link Project#id ID} of the {@link Project}
     *          to update. If {@code null} or no {@link Project}
     *          with the given {@code projectId} exists, a new
     *          one is created.
     *
     * @return
     *          The <b>persisted</b> updated/created {@link Project}.
     *          
     * @see ProjectDtoMapper#toProject(ProjectRequestDTO)
     */
    @Transactional
    Project createOrUpdateProject(Project project, String projectId) {
        Optional<Project> existingProjectOptional = projectId != null ? projectRepo.findById(projectId) : Optional.empty();

        LocalDateTime updatedTime = LocalDateTime.now(clock);

        project.setOrigin(ProjectOrigin.CUSTOM);
        project.setUpdated(updatedTime);

        if(existingProjectOptional.isPresent()) {
            Project existingProject = existingProjectOptional.get();

            project.setId(existingProject.getId());
            project.setCreated(existingProject.getCreated());
        } else {
            project.setId(null);
            project.setCreated(updatedTime);
        }

        return projectRepo.save(project);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Project> getProjectsForUser(User user, Sort sort) {
        if(userService.userIsManager(user)) {
            return projectRepo.findAllForManager(sort);
        } else {
            String lob = userService.getUserData(user).getLob();

            return projectRepo.findAllForUser(lob, sort);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Project> searchProjectsForUser(User user, String keyword, Sort sort) {
        if(userService.userIsManager(user)) {
            return projectRepo.findAllForManagerByKeyword(keyword, sort);
        } else {
            String lob = userService.getUserData(user).getLob();

            return projectRepo.findAllForUserByKeyword(lob, keyword, sort);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean userOwnsProject(User user, Project project) {
        return userRepo.existsByIdAndOwnedProjectsContaining(user.getId(), project);
    }

    @Override
    @Transactional
    public Project createProjectForUser(Project project, User user) {
        Project createdProject = createProject(project);

        user.addOwnedProject(createdProject);
        userService.save(user);

        return createdProject;
    }

    @Override
    @Transactional
    public Project addProjectToUser(User user, Project project) {
        user.addOwnedProject(project);
        userService.save(user);

        return project;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Project> getProjectsForUserPaginated(User user, Pageable pageable) {
        if(userService.userIsManager(user)) {
            return projectRepo.findAllForManagerPageable(pageable);
        } else {
            String lob = userService.getUserData(user).getLob();

            return projectRepo.findAllForUserPageable(lob, pageable);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Project> searchProjectsForUserPaginated(String keyword, User user, Pageable pageable) {
        if(userService.userIsManager(user)) {
            return projectRepo.findAllForManagerByKeywordPageable(keyword, pageable);
        } else {
            String lob = userService.getUserData(user).getLob();

            return projectRepo.findAllForUserByKeywordPageable(lob, keyword, pageable);
        }
    }

}
