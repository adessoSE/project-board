package de.adesso.projectboard.rest.security;

import de.adesso.projectboard.base.access.service.UserAccessService;
import de.adesso.projectboard.base.application.service.ApplicationService;
import de.adesso.projectboard.base.project.persistence.Project;
import de.adesso.projectboard.base.project.service.ProjectService;
import de.adesso.projectboard.base.security.ExpressionEvaluator;
import de.adesso.projectboard.base.user.persistence.User;
import de.adesso.projectboard.base.user.service.UserService;
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
 */
@Profile("user-access")
@Service
public class UserAccessExpressionEvaluator implements ExpressionEvaluator {

    private final UserService userService;

    private final UserAccessService userAccessService;

    private final ProjectService projectService;

    private final ApplicationService applicationService;

    @Autowired
    public UserAccessExpressionEvaluator(UserService userService,
                                         UserAccessService userAccessService,
                                         ProjectService projectService,
                                         ApplicationService applicationService) {
        this.userService = userService;
        this.userAccessService = userAccessService;
        this.projectService = projectService;
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
     *          {@code true}, iff the user is a manager or has a active
     *          access interval instance.
     *
     * @see UserAccessService#userHasActiveAccessInterval(User)
     * @see UserService#userIsManager(User)
     */
    @Override
    public boolean hasAccessToProjects(Authentication authentication, User user) {
        return userAccessService.userHasActiveAccessInterval(user) || userService.userIsManager(user);
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
     *          The id of the {@link Project} the user wants to access.
     *
     * @return
     *          {@code true}, iff the at least one of the following conditions is {@code true}
     *
     *          <ul>
     *              <li>
     *                  The user has access to projects.
     *                  ({@link #hasAccessToProjects(Authentication, User)} returns {@code true})
     *              </li>
     *
     *              <li>
     *                  No {@link Project} with the given {@code projectId} exists.
     *                  ({@link ProjectService#projectExists(String)} returns {@code false})
     *              </li>
     *
     *              <li>
     *                  The user has applied for the {@link Project} with the given {@code projectId}.
     *                  ({@link ApplicationService#userHasAppliedForProject(User, Project)} returns {@code true})
     *              </li>
     *          </ul>
     */
    @Override
    public boolean hasAccessToProject(Authentication authentication, User user, String projectId) {
        return hasAccessToProjects(authentication, user) ||
                !projectService.projectExists(projectId) ||
                applicationService.userHasAppliedForProject(user, projectService.getProjectById(projectId));
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
     *          {@code false}
     */
    @Override
    public boolean hasPermissionToCreateProjects(Authentication authentication, User user) {
        return false;
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
     *          {@code false}
     */
    @Override
    public boolean hasPermissionToEditProject(Authentication authentication, User user, String projectId) {
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
     * @param userId
     *          The id of the {@link User}
     *          the current user wants to access.
     *
     * @return
     *          The result of {@link UserService#userHasStaffMember(User, User)} iff
     *          {@link UserService#userExists(String)} returns {@code true}, {@code true}
     *          otherwise.
     */
    @Override
    public boolean hasElevatedAccessToUser(Authentication authentication, User user, String userId) {
        if(userService.userExists(userId)) {
            return userService.userHasStaffMember(user, userService.getUserById(userId));
        }

        return true;
    }

}
