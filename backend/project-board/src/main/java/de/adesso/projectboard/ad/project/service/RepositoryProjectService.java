package de.adesso.projectboard.ad.project.service;

import de.adesso.projectboard.base.application.persistence.ProjectApplication;
import de.adesso.projectboard.base.application.persistence.ProjectApplicationRepository;
import de.adesso.projectboard.base.exceptions.ProjectNotEditableException;
import de.adesso.projectboard.base.exceptions.ProjectNotFoundException;
import de.adesso.projectboard.base.project.dto.ProjectDtoMapper;
import de.adesso.projectboard.base.project.dto.ProjectRequestDTO;
import de.adesso.projectboard.base.project.persistence.Project;
import de.adesso.projectboard.base.project.persistence.ProjectRepository;
import de.adesso.projectboard.base.project.service.ProjectService;
import de.adesso.projectboard.base.user.persistence.User;
import de.adesso.projectboard.base.user.persistence.UserRepository;
import de.adesso.projectboard.base.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class RepositoryProjectService implements ProjectService {

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

    @Override
    public Project updateProject(Project project, String projectId) throws ProjectNotEditableException {
        Project existingProject = getProjectById(projectId);

        if(Project.Origin.CUSTOM.equals(existingProject.getOrigin())) {
            return createOrUpdateProject(project, projectId);
        } else {
            throw new ProjectNotEditableException();
        }
    }

    @Override
    public Project save(Project project) {
        return projectRepo.save(project);
    }

    @Override
    public List<Project> saveAll(List<Project> projects) {
        return projectRepo.saveAll(projects);
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
    public void deleteProjectById(String projectId) throws ProjectNotEditableException {
        Project existingProject = getProjectById(projectId);

        if(Project.Origin.JIRA.equals(existingProject.getOrigin())) {
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
    public Project createOrUpdateProject(Project project, String projectId) {
        Optional<Project> existingProjectOptional = projectId != null ? projectRepo.findById(projectId) : Optional.empty();

        LocalDateTime updatedTime = LocalDateTime.now(clock);

        project.setOrigin(Project.Origin.CUSTOM);
        project.setUpdated(updatedTime);

        if(existingProjectOptional.isPresent()) {
            Project existingProject = existingProjectOptional.get();

            project.setId(existingProject.getId());
            project.setCreated(existingProject.getCreated());
        } else {
            project.setId(null);
            project.setCreated(updatedTime);
        }

        return this.save(project);
    }

}
