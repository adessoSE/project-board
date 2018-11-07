package de.adesso.projectboard.rest.security;

import de.adesso.projectboard.base.access.persistence.AccessInfo;
import de.adesso.projectboard.base.application.service.ApplicationService;
import de.adesso.projectboard.base.project.persistence.Project;
import de.adesso.projectboard.base.project.service.ProjectService;
import de.adesso.projectboard.base.security.ExpressionEvaluator;
import de.adesso.projectboard.base.user.persistence.User;
import de.adesso.projectboard.base.user.persistence.data.UserData;
import de.adesso.projectboard.base.user.service.UserProjectService;
import de.adesso.projectboard.base.user.service.UserService;
import de.adesso.projectboard.ldap.user.LdapUserService;
import de.adesso.projectboard.project.service.RepositoryProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

/**
 * A {@link ExpressionEvaluator} implementation that is used to authorize access
 * to the REST interface by using persisted user data.
 *
 * <p>
 *     Activated via the <i>user-access</i> spring profile.
 * </p>
 *
 * @see ExpressionEvaluator
 * @see LdapUserService
 * @see RepositoryProjectService
 */
@Profile("user-access")
@Service
public class UserAccessExpressionEvaluator implements ExpressionEvaluator {

    private final UserService userService;

    private final ProjectService projectService;

    private final UserProjectService userProjectService;

    private final ApplicationService applicationService;

    @Autowired
    public UserAccessExpressionEvaluator(UserService userService,
                                         ProjectService projectService,
                                         UserProjectService userProjectService,
                                         ApplicationService applicationService) {
        this.userService = userService;
        this.projectService = projectService;
        this.userProjectService = userProjectService;
        this.applicationService = applicationService;
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
     *          {@code true}, iff the {@code user}'s latest access info instance
     *          is active or the user is a manager.
     */
    @Override
    public boolean hasAccessToProjects(Authentication authentication, User user) {
        AccessInfo latestAccessInfo = user.getLatestAccessInfo();

        if(latestAccessInfo != null) {
            return latestAccessInfo.isCurrentlyActive();
        } else {
            return userService.userIsManager(user.getId());
        }
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
     *          {@code true}, iff the no project with the given {@code projectId} exists,
     *          {@link ApplicationService#userHasAppliedForProject(String, String)} returns {@code true},
     *          {@link UserProjectService#userOwnsProject(String, String)} returns {@code true} or
     *          {@link #hasAccessToProjects(Authentication, User)} returns {@code true}
     *          and at least one of the following conditions is true:
     *
     *          <ul>
     *              <li>
     *                  The user is a manager and the project's status is set to <i>offen</i> or <i>eskaliert</i>.
     *              </li>
     *
     *              <li>
     *                  The user is not a manager and the project's status is set to <i>eskaliert</i> or <i>offen</i>
     *                  and the project's LoB is equal to the user's LoB.
     *              </li>
     *          </ul>
     *
     */
    @Override
    public boolean hasAccessToProject(Authentication authentication, User user, String projectId) {
        // return true if the project does not exist, the user owns the project or
        // the user has applied for the project
        if(!projectService.projectExists(projectId) ||
                userProjectService.userOwnsProject(user.getId(), projectId) ||
                applicationService.userHasAppliedForProject(user.getId(), projectId)) {

            return true;
        }

        Project project = projectService.getProjectById(projectId);

        if(hasAccessToProjects(authentication, user)) {
            UserData userData = userService.getUserData(user.getId());

            boolean isManager = userService.userIsManager(user.getId());
            boolean isOpen = "offen".equalsIgnoreCase(project.getStatus());
            boolean isEscalated = "eskaliert".equalsIgnoreCase(project.getStatus());
            boolean sameLobAsUser = userData.getLob().equalsIgnoreCase(project.getLob());
            boolean noLob = project.getLob() == null;

            // escalated || isOpen <-> (sameLob || noLob)
            // equivalence because implication is not enough
            // when the status is neither "eskaliert" nor "offen"
            return isEscalated || (isOpen && isManager) || ((isOpen && (sameLobAsUser || noLob)) || (!isOpen && !(sameLobAsUser || noLob)));
        }

        return false;
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
     *          The id of the {@link User} the current user wants to access.
     *
     * @return
     *          {@code true}, when the currently authenticated user has the same {@link User#getId() id}
     *          or the result of {@link #hasElevatedAccessToUser(Authentication, User, String)}.
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
     *          {@code true}, if the given {@link User} is a manager,
     *          {@code false} otherwise.
     */
    @Override
    public boolean hasPermissionToCreateProjects(Authentication authentication, User user) {
        return userService.userIsManager(user.getId());
    }

    /**
     * A {@link User} has the permission to edit a {@link Project} when it is
     * present in the {@link User#ownedProjects owned projects} of the user.
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
     *          The result of {@link UserProjectService#userOwnsProject(String, String)}.
     */
    @Override
    public boolean hasPermissionToEditProject(Authentication authentication, User user, String projectId) {
        return userProjectService.userOwnsProject(user.getId(), projectId);
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
     *          The result of {@link UserService#userHasStaffMember(String, String)}.
     */
    @Override
    public boolean hasElevatedAccessToUser(Authentication authentication, User user, String userId) {
        return userService.userHasStaffMember(user.getId(), userId);
    }

}
