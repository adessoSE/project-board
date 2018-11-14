package de.adesso.projectboard.application.service;

import de.adesso.projectboard.base.application.dto.ProjectApplicationRequestDTO;
import de.adesso.projectboard.base.application.persistence.ProjectApplication;
import de.adesso.projectboard.base.application.persistence.ProjectApplicationRepository;
import de.adesso.projectboard.base.application.service.ApplicationService;
import de.adesso.projectboard.base.exceptions.AlreadyAppliedException;
import de.adesso.projectboard.base.exceptions.ProjectNotFoundException;
import de.adesso.projectboard.base.exceptions.UserNotFoundException;
import de.adesso.projectboard.base.project.persistence.Project;
import de.adesso.projectboard.base.project.service.ProjectService;
import de.adesso.projectboard.base.user.persistence.User;
import de.adesso.projectboard.base.user.service.UserService;
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

    private final UserService userService;

    private final ProjectApplicationRepository applicationRepo;

    @Autowired
    public RepositoryApplicationService(ProjectService projectService,
                                        UserService userService,
                                        ProjectApplicationRepository applicationRepo) {
        this.projectService = projectService;
        this.userService = userService;
        this.applicationRepo = applicationRepo;
    }

    @Override
    public boolean userHasAppliedForProject(User user, Project project) throws UserNotFoundException, ProjectNotFoundException {
        return applicationRepo.existsByUserAndProject(user, project);
    }

    @Override
    public ProjectApplication createApplicationForUser(User user, ProjectApplicationRequestDTO applicationDTO) throws AlreadyAppliedException {
        // check if a valid user instance was passed
        userService.validateExistence(user);

        Project project = projectService.getProjectById(applicationDTO.getProjectId());

        if(userHasAppliedForProject(user, project)) {
            throw new AlreadyAppliedException();
        }

        ProjectApplication application = new ProjectApplication(project, applicationDTO.getComment(), user);

        userService.save(user);
        return applicationRepo.save(application);
    }

    @Override
    public List<ProjectApplication> getApplicationsOfUser(User user) throws UserNotFoundException {
        return new ArrayList<>(user.getApplications());
    }

}
