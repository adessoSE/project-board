package de.adesso.projectboard.application.service;

import de.adesso.projectboard.base.application.dto.ProjectApplicationRequestDTO;
import de.adesso.projectboard.base.application.persistence.ProjectApplication;
import de.adesso.projectboard.base.application.persistence.ProjectApplicationRepository;
import de.adesso.projectboard.base.application.service.ApplicationService;
import de.adesso.projectboard.base.exceptions.AlreadyAppliedException;
import de.adesso.projectboard.base.project.persistence.Project;
import de.adesso.projectboard.base.project.service.ProjectService;
import de.adesso.projectboard.base.user.persistence.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link ApplicationService} implementation that persists {@link ProjectApplication}s
 * in a repository.
 */
@Service
public class RepositoryApplicationService implements ApplicationService {

    private final ProjectService projectService;

    private final ProjectApplicationRepository applicationRepo;

    @Autowired
    public RepositoryApplicationService(ProjectService projectService,
                                        ProjectApplicationRepository applicationRepo) {
        this.projectService = projectService;
        this.applicationRepo = applicationRepo;
    }

    @Override
    public boolean userHasAppliedForProject(User user, Project project) {
        return applicationRepo.existsByUserAndProject(user, project);
    }

    @Override
    public ProjectApplication createApplicationForUser(User user, ProjectApplicationRequestDTO applicationDTO) throws AlreadyAppliedException {
        Project project = projectService.getProjectById(applicationDTO.getProjectId());

        if(userHasAppliedForProject(user, project)) {
            throw new AlreadyAppliedException();
        }

        ProjectApplication application = new ProjectApplication(project, applicationDTO.getComment(), user);
        return applicationRepo.save(application);
    }

    @Override
    public List<ProjectApplication> getApplicationsOfUser(User user) {
        return new ArrayList<>(user.getApplications());
    }

}
