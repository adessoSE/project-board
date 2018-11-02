package de.adesso.projectboard.service;

import de.adesso.projectboard.base.application.dto.ProjectApplicationRequestDTO;
import de.adesso.projectboard.base.application.persistence.ProjectApplication;
import de.adesso.projectboard.base.application.persistence.ProjectApplicationRepository;
import de.adesso.projectboard.base.exceptions.AlreadyAppliedException;
import de.adesso.projectboard.base.exceptions.ProjectNotFoundException;
import de.adesso.projectboard.base.exceptions.UserNotFoundException;
import de.adesso.projectboard.base.project.persistence.Project;
import de.adesso.projectboard.base.project.service.ProjectServiceImpl;
import de.adesso.projectboard.base.user.persistence.User;
import de.adesso.projectboard.ldap.user.LdapUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * {@link Service} to to provide functionality to manage {@link ProjectApplication}s.
 *
 * @see LdapUserService
 * @see ProjectServiceImpl
 */
@Service
public class ApplicationServiceImpl {

    private final LdapUserService userService;

    private final ProjectApplicationRepository applicationRepo;

    private final ProjectServiceImpl projectService;

    @Autowired
    public ApplicationServiceImpl(LdapUserService userService, ProjectApplicationRepository applicationRepo, ProjectServiceImpl projectService) {
        this.userService = userService;
        this.applicationRepo = applicationRepo;
        this.projectService = projectService;
    }

    /**
     *
     * @param userId
     *          The id of the {@link User}.
     *
     * @param project
     *          The {@link Project}.
     *
     * @return
     *          {@code true}, when the user with the given {@code userId}
     *          has a {@link ProjectApplication} that {@link ProjectApplication#getProject() references}
     *          the {@code project}.
     *
     * @throws UserNotFoundException
     *          When no user with the given {@code userId} is found.
     *
     */
    public boolean userHasAppliedForProject(String userId, Project project) throws UserNotFoundException {
        User user = userService.getUserById(userId);

        return applicationRepo.existsByUserAndProject(user, project);
    }

    /**
     *
     * @param requestDTO
     *          The {@link ProjectApplicationRequestDTO} object.
     *
     * @param userId
     *          The id of the {@link User} the {@link ProjectApplication} is created for.
     *
     * @return
     *          The saved {@link ProjectApplication}.
     *
     * @throws UserNotFoundException
     *          When no {@link User} with the given {@code userId} was found.
     *
     * @throws AlreadyAppliedException
     *          When the {@link User} with the given {@code userId} has already applied for that
     *          {@link Project}.
     *
     * @throws ProjectNotFoundException
     *          When no {@link Project} with the given {@link ProjectApplicationRequestDTO#getProjectId() projectId}
     *          is found.
     */
    public ProjectApplication createApplicationForUser(ProjectApplicationRequestDTO requestDTO, String userId) throws UserNotFoundException, AlreadyAppliedException, ProjectNotFoundException {
        Project project =  projectService.getProjectById(requestDTO.getProjectId());
        User user = userService.getUserById(userId);

        if(userHasAppliedForProject(userId, project)) {
            throw new AlreadyAppliedException();
        }

        ProjectApplication application = new ProjectApplication(project, requestDTO.getComment(), user);

        return applicationRepo.save(application);
    }

    /**
     *
     * @param userId
     *          The id of the {@link User} to get the {@link ProjectApplication}s from.
     *
     * @return
     *          The {@link ProjectApplication}s of the {@link User}.
     *
     * @throws UserNotFoundException
     *          When no {@link User} with the given {@code userId} was found.
     *
     * @see LdapUserService#getUserById(String)
     */
    public Set<ProjectApplication> getApplicationsOfUser(String userId) throws UserNotFoundException {
        return userService.getUserById(userId).getApplications();
    }

}
