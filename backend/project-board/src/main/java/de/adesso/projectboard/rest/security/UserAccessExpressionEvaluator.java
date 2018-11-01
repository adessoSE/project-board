package de.adesso.projectboard.rest.security;

import de.adesso.projectboard.base.access.persistence.AccessInfo;
import de.adesso.projectboard.base.project.persistence.Project;
import de.adesso.projectboard.base.project.service.ProjectService;
import de.adesso.projectboard.base.security.ExpressionEvaluator;
import de.adesso.projectboard.base.user.persistence.User;
import de.adesso.projectboard.base.user.service.UserServiceImpl;
import de.adesso.projectboard.service.ApplicationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * A {@link ExpressionEvaluator} implementation that is used to authorize access
 * to the REST interface by using persisted user data.
 *
 * <p>
 *     Activated via the <i>user-access</i> spring profile.
 * </p>
 *
 * @see ExpressionEvaluator
 * @see UserServiceImpl
 * @see ProjectService
 */
@Profile("user-access")
@Service
public class UserAccessExpressionEvaluator implements ExpressionEvaluator {

    private final UserServiceImpl userService;

    private final ProjectService projectService;

    private final ApplicationServiceImpl applicationService;

    @Autowired
    public UserAccessExpressionEvaluator(UserServiceImpl userService,
                                         ProjectService projectService,
                                         ApplicationServiceImpl applicationService) {
        this.userService = userService;
        this.projectService = projectService;
        this.applicationService = applicationService;
    }

    /**
     * Gets the currently authenticated user from the {@link UserServiceImpl}
     * and retrieves the latest {@link AccessInfo} object for that user.
     * When the {@link AccessInfo#getAccessEnd() access end date} is
     * <b>after</b> the {@link LocalDateTime#now() current} date the user has access.
     *
     * @param authentication
     *          The {@link Authentication} object.
     *
     * @param user
     *          The {@link User} object of the currently authenticated user.
     *
     * @return
     *          The result of {@link User#hasAccess()}.
     *
     * @see User#hasAccess()
     */
    @Override
    public boolean hasAccessToProjects(Authentication authentication, User user) {
        return user.hasAccess() || user instanceof SuperUser;
    }

    /**
     *
     * @param authentication
     *          The {@link Authentication} object.
     *
     * @param user
     *          The {@link User} object of the currently authenticated user.
     *
     * @param projectId
     *          The id of the {@link Project}
     *          the user wants to access.
     *
     * @return
     *          {@code true}, when the given {@code user}
     *          {@link ApplicationServiceImpl#userHasAppliedForProject(String, Project) applied}
     *          for the project, {@link ProjectService#userHasProject(String, String) created}
     *          it or {@link #hasAccessToProjects(Authentication, User) has access} to projects
     *          and the project's {@link Project#status status} is set to "<i>eskaliert</i>".
     *          Also returns {@code true}, when the {@code user} is a {@link SuperUser} and the
     *          project's {@link Project#status status} is set to "<i>offen</i>" or the
     *          project's {@link Project#status status} is set to "<i>offen</i>" and the project
     *          is of the {@link Project#lob LOB} as the user or has no lob set ({@code null}).
     *          {@code false} otherwise.
     */
    @Override
    public boolean hasAccessToProject(Authentication authentication, User user, String projectId) {
        if(!projectService.projectExists(projectId)) {
            return true;
        }

        Project project = projectService.getProjectById(projectId);

        if(hasAccessToProjects(authentication, user)) {
            boolean isSuperUser = user instanceof SuperUser;
            boolean isOpen = "offen".equalsIgnoreCase(project.getStatus());
            boolean isEscalated = "eskaliert".equalsIgnoreCase(project.getStatus());
            boolean sameLobAsUser = user.getLob().equalsIgnoreCase(project.getLob());
            boolean noLob = project.getLob() == null;

            // escalated || isOpen <-> (sameLob || noLob)
            // equivalence because implication is not enough
            // when the status is neither "eskaliert" nor "offen"
            return isEscalated || (isOpen && isSuperUser) || ((isOpen && (sameLobAsUser || noLob)) || (!isOpen && !(sameLobAsUser || noLob)));
        }

        boolean hasAppliedForProject = applicationService.userHasAppliedForProject(user.getId(), project);
        if(hasAppliedForProject) {
            return true;
        }

        boolean hasProject = projectService.userHasProject(user.getId(), projectId);
        return hasProject;
    }

    /**
     *
     * @param authentication
     *          The {@link Authentication} object.
     *
     * @param user
     *          The {@link User} object of the currently authenticated user.
     *
     * @return
     *          The result of {@link #hasAccessToProjects(Authentication, User)}
     *
     * @see #hasAccessToProjects(Authentication, User)
     */
    @Override
    public boolean hasPermissionToApply(Authentication authentication, User user) {
        return hasAccessToProjects(authentication, user);
    }

    /**
     *
     * @param authentication
     *          The {@link Authentication} object.
     *
     * @param user
     *          The {@link User} object of the currently authenticated user.
     *
     * @param userId
     *          The id of the {@link User}
     *          the current user wants to access.
     *
     * @return
     *          {@code true}, when the currently authenticated user has the same {@link User#getId() id}
     *          or the result of {@link #hasElevatedAccessToUser(Authentication, User, String)}.
     *
     * @see SuperUser#getStaffMembers()
     */
    @Override
    public boolean hasPermissionToAccessUser(Authentication authentication, User user, String userId) {
        return user.getId().equals(userId) || hasElevatedAccessToUser(authentication, user, userId);
    }

    /**
     *
     * @param authentication
     *          The {@link Authentication} object.
     *
     * @param user
     *          The {@link User} object of the currently authenticated user.
     *
     * @return
     *          {@code true}, if the given {@link User} is a {@link SuperUser},
     *          {@code false} otherwise.
     */
    @Override
    public boolean hasPermissionToCreateProjects(Authentication authentication, User user) {
        return user instanceof SuperUser;
    }

    /**
     * A {@link User} has the permission to edit a {@link Project} when it is
     * present in the {@link User#createdProjects created projects} of the user.
     *
     * @param authentication
     *          The {@link Authentication} object.
     *
     * @param user
     *          The {@link User} object of the currently authenticated user.
     *
     * @param projectId
     *          The id of the {@link Project} the user wants to update.
     *
     * @return
     *          The result of {@link ProjectService#userHasProject(String, String)}.
     *
     * @see ProjectService#userHasProject(String, String)
     */
    @Override
    public boolean hasPermissionToEditProject(Authentication authentication, User user, String projectId) {
        return projectService.userHasProject(user.getId(), projectId);
    }

    /**
     *
     * @param authentication
     *          The {@link Authentication} object.
     *
     * @param user
     *          The {@link User} object of the currently authenticated user.
     *
     * @param userId
     *          The id of the {@link User}
     *          the current user wants to access.
     *
     * @return
     *          {@code true}, when the {@code user} is a {@link SuperUser} and a user with the given
     *          {@code userId} is included in the {@link Set} of the
     *          {@link SuperUser#getStaffMembers() user's staff members}, {@code false} otherwise.
     *
     * @see SuperUser
     */
    @Override
    public boolean hasElevatedAccessToUser(Authentication authentication, User user, String userId) {
        if(user instanceof SuperUser) {
            return userService.userHasStaffMember((SuperUser) user, userId);
        } else {
            return false;
        }
    }

}
