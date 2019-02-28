package de.adesso.projectboard.ad.application.service;

import de.adesso.projectboard.base.application.handler.ProjectApplicationEventHandler;
import de.adesso.projectboard.base.application.persistence.ProjectApplication;
import de.adesso.projectboard.base.application.persistence.ProjectApplicationRepository;
import de.adesso.projectboard.base.application.service.ApplicationService;
import de.adesso.projectboard.base.exceptions.AlreadyAppliedException;
import de.adesso.projectboard.base.exceptions.ApplicationNotFoundException;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * {@link ApplicationService} implementation that persists {@link ProjectApplication}s
 * in a repository.
 */
@Slf4j
@Service
@Transactional(readOnly = true)
public class RepositoryApplicationService implements ApplicationService {

    private final ProjectService projectService;

    private final ProjectApplicationRepository applicationRepo;

    private final ProjectApplicationEventHandler applicationEventHandler;

    private final Clock clock;

    @Autowired
    public RepositoryApplicationService(ProjectService projectService,
                                        ProjectApplicationRepository applicationRepo,
                                        ProjectApplicationEventHandler applicationEventHandler,
                                        Clock clock) {
        this.projectService = projectService;
        this.applicationRepo = applicationRepo;
        this.applicationEventHandler = applicationEventHandler;
        this.clock = clock;
    }

    @Override
    public boolean userHasAppliedForProject(User user, Project project) {
        return applicationRepo.existsByUserAndProject(user, project);
    }

    @Override
    @Transactional
    public ProjectApplication createApplicationForUser(User user, String projectId, String comment) throws AlreadyAppliedException {
        Project project = projectService.getProjectById(projectId);

        if(userHasAppliedForProject(user, project)) {
            throw new AlreadyAppliedException();
        }

        // use a clock for testing
        LocalDateTime applicationDate = LocalDateTime.now(clock);
        ProjectApplication application = new ProjectApplication(project, comment, user, applicationDate, false);

        var savedApplication = applicationRepo.save(application);
        applicationEventHandler.onApplicationReceived(savedApplication);

        return savedApplication;
    }

    @Override
    public List<ProjectApplication> getApplicationsOfUser(User user) {
        return new ArrayList<>(user.getApplications());
    }

    @Override
    public List<ProjectApplication> getApplicationsOfUsers(Collection<User> users, Sort sort) {
        return applicationRepo.findAllByUserIn(users, sort);
    }

    @Override
    public ProjectApplication markApplicationAsRead(Long applicationId) throws ApplicationNotFoundException {
        var application = applicationRepo.findById(applicationId).get();
        application.setReadByBoss(true);
        return applicationRepo.save(application);
    }

}
