package de.adesso.projectboard.ad.application.service;

import de.adesso.projectboard.base.application.handler.ProjectApplicationEventHandler;
import de.adesso.projectboard.base.application.persistence.ProjectApplication;
import de.adesso.projectboard.base.application.persistence.ProjectApplicationRepository;
import de.adesso.projectboard.base.application.service.ApplicationService;
import de.adesso.projectboard.base.configuration.ProjectBoardConfigurationProperties;
import de.adesso.projectboard.base.exceptions.AlreadyAppliedException;
import de.adesso.projectboard.base.exceptions.ApplicationNotFoundException;
import de.adesso.projectboard.base.exceptions.ProjectStatusPreventsApplicationException;
import de.adesso.projectboard.base.project.persistence.Project;
import de.adesso.projectboard.base.project.service.ProjectService;
import de.adesso.projectboard.base.user.persistence.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * {@link ApplicationService} implementation that persists {@link ProjectApplication}s
 * in a repository.
 */
@Service
@Transactional(readOnly = true)
@Slf4j
public class RepositoryApplicationService implements ApplicationService {

    private final ProjectService projectService;

    private final ProjectApplicationRepository applicationRepo;

    private final ProjectApplicationEventHandler applicationEventHandler;

    private final Clock clock;

    private final Set<String> applicationsForbiddenStatus;

    @Autowired
    public RepositoryApplicationService(ProjectService projectService,
                                        ProjectApplicationRepository applicationRepo,
                                        ProjectApplicationEventHandler applicationEventHandler,
                                        Clock clock,
                                        ProjectBoardConfigurationProperties properties) {
        this.projectService = projectService;
        this.applicationRepo = applicationRepo;
        this.applicationEventHandler = applicationEventHandler;
        this.clock = clock;

        this.applicationsForbiddenStatus = new HashSet<>(properties.getApplicationsForbiddenStatus());
    }

    @Override
    public boolean userHasAppliedForProject(User user, Project project) {
        return applicationRepo.existsByUserAndProject(user, project);
    }

    @Override
    @Transactional
    public ProjectApplication createApplicationForUser(User user, String projectId, String comment) {
        var project = projectService.getProjectById(projectId);
        var projectStatus = project.getStatus().toLowerCase();

        if(userHasAppliedForProject(user, project)) {
            throw new AlreadyAppliedException();
        }

        if(applicationsForbiddenStatus.contains(projectStatus)) {
            throw new ProjectStatusPreventsApplicationException();
        }

        // use a clock for testing
        var applicationDate = LocalDateTime.now(clock);
        var application = new ProjectApplication(project, comment, user, applicationDate);

        var savedApplication = applicationRepo.save(application);
        applicationEventHandler.onApplicationReceived(savedApplication);

        return savedApplication;
    }

    @Override
    public List<ProjectApplication> getApplicationsOfUser(User user, Sort sort) {
        return applicationRepo.findAllByUser(user, sort);
    }

    @Override
    public List<ProjectApplication> getApplicationsOfUsers(Collection<User> users, Sort sort) {
        return applicationRepo.findAllByUserIn(users, sort);
    }

    @Transactional
    @Override
    public ProjectApplication deleteApplication(User user, long applicationId) throws ApplicationNotFoundException {
        var application = applicationRepo.findByUserAndId(user, applicationId).orElseThrow(ApplicationNotFoundException::new);
        applicationRepo.delete(application);

        log.debug(String.format("Application with id '%d' of user '%s' was deleted", applicationId, user.getId()));
        return application;
    }

}
