package de.adesso.projectboard.ad.application.service;

import de.adesso.projectboard.base.application.dto.ProjectApplicationRequestDTO;
import de.adesso.projectboard.base.application.persistence.ProjectApplication;
import de.adesso.projectboard.base.application.persistence.ProjectApplicationRepository;
import de.adesso.projectboard.base.application.service.ApplicationService;
import de.adesso.projectboard.base.exceptions.AlreadyAppliedException;
import de.adesso.projectboard.base.project.persistence.Project;
import de.adesso.projectboard.base.project.service.ProjectService;
import de.adesso.projectboard.base.user.persistence.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * {@link ApplicationService} implementation that persists {@link ProjectApplication}s
 * in a repository.
 */
@Service
@Transactional(readOnly = true)
public class RepositoryApplicationService implements ApplicationService {

    private final ProjectService projectService;

    private final ProjectApplicationRepository applicationRepo;

    private final Clock clock;

    @Autowired
    public RepositoryApplicationService(ProjectService projectService,
                                        ProjectApplicationRepository applicationRepo,
                                        Clock clock) {
        this.projectService = projectService;
        this.applicationRepo = applicationRepo;
        this.clock = clock;
    }

    @Override
    public boolean userHasAppliedForProject(User user, Project project) {
        return applicationRepo.existsByUserAndProject(user, project);
    }

    @Override
    @Transactional
    public ProjectApplication createApplicationForUser(User user, ProjectApplicationRequestDTO applicationDTO) throws AlreadyAppliedException {
        Project project = projectService.getProjectById(applicationDTO.getProjectId());

        if(userHasAppliedForProject(user, project)) {
            throw new AlreadyAppliedException();
        }

        // use a clock for testing
        LocalDateTime applicationDate = LocalDateTime.now(clock);
        ProjectApplication application = new ProjectApplication(project, applicationDTO.getComment(), user, applicationDate);

        return applicationRepo.save(application);
    }

    @Override
    public List<ProjectApplication> getApplicationsOfUser(User user) {
        return new ArrayList<>(user.getApplications());
    }

    @Override
    public List<ProjectApplication> getApplicationsOfUsers(Collection<User> users, Sort sort) {
        return applicationRepo.findAllByUserIn(users, sort);
    }

}
