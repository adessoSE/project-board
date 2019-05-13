package de.adesso.projectboard.ad.application.service;

import de.adesso.projectboard.base.application.handler.ProjectApplicationOfferedEventHandler;
import de.adesso.projectboard.base.application.handler.ProjectApplicationReceivedEventHandler;
import de.adesso.projectboard.base.application.persistence.ProjectApplication;
import de.adesso.projectboard.base.application.persistence.ProjectApplicationRepository;
import de.adesso.projectboard.base.application.service.ApplicationService;
import de.adesso.projectboard.base.exceptions.AlreadyAppliedException;
import de.adesso.projectboard.base.exceptions.ApplicationAlreadyOfferedException;
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
import java.util.Collection;
import java.util.List;

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

    private final ProjectApplicationReceivedEventHandler applicationEventHandler;

    private final ProjectApplicationOfferedEventHandler applicationOfferedEventHandler;

    private final Clock clock;

    @Autowired
    public RepositoryApplicationService(ProjectService projectService,
                                        ProjectApplicationRepository applicationRepo,
                                        ProjectApplicationReceivedEventHandler applicationEventHandler,
                                        ProjectApplicationOfferedEventHandler applicationOfferedEventHandler,
                                        Clock clock) {
        this.projectService = projectService;
        this.applicationRepo = applicationRepo;
        this.applicationEventHandler = applicationEventHandler;
        this.applicationOfferedEventHandler = applicationOfferedEventHandler;
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
        ProjectApplication application = new ProjectApplication(project, comment, user, applicationDate);

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

    @Transactional
    @Override
    public ProjectApplication offerApplication(User offeringUser, User offeredUser, long applicationId) {
        var application = applicationRepo.findByUserAndId(offeredUser, applicationId)
                .orElseThrow(ApplicationNotFoundException::new);

        if(application.isOffered()) {
            throw new ApplicationAlreadyOfferedException();
        } else {
            application.setOffered(true);
            applicationRepo.save(application);
            log.debug("Application {} of {} was offered by {}", applicationId, offeredUser.getId(), offeringUser.getId());

            applicationOfferedEventHandler.onApplicationOffered(offeringUser, application);

            return application;
        }
    }

}
