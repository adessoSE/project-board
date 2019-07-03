package de.adesso.projectboard.rest.security;

import de.adesso.projectboard.base.access.service.UserAccessService;
import de.adesso.projectboard.base.application.service.ApplicationService;
import de.adesso.projectboard.base.configuration.ProjectBoardConfigurationProperties;
import de.adesso.projectboard.base.project.persistence.Project;
import de.adesso.projectboard.base.project.service.ProjectService;
import de.adesso.projectboard.base.security.ExpressionEvaluator;
import de.adesso.projectboard.base.user.persistence.User;
import de.adesso.projectboard.base.user.service.BookmarkService;
import de.adesso.projectboard.base.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

/**
 * A {@link ExpressionEvaluator} implementation that is used to authorize access
 * to the REST interface by using persisted user data.
 *
 * @see ExpressionEvaluator
 */
@Service
public class UserAccessExpressionEvaluator implements ExpressionEvaluator {

    private final UserService userService;

    private final UserAccessService userAccessService;

    private final ProjectService projectService;

    private final ApplicationService applicationService;

    private final BookmarkService bookmarkService;

    private final Set<String> lobDependentStatus;

    private final Set<String> lobIndependentStatus;

    @Autowired
    public UserAccessExpressionEvaluator(UserService userService,
                                         UserAccessService userAccessService,
                                         ProjectService projectService,
                                         ApplicationService applicationService,
                                         BookmarkService bookmarkService,
                                         ProjectBoardConfigurationProperties properties) {
        this.userService = userService;
        this.userAccessService = userAccessService;
        this.projectService = projectService;
        this.applicationService = applicationService;
        this.bookmarkService = bookmarkService;

        this.lobDependentStatus = new HashSet<>(properties.getLobDependentStatus());
        this.lobIndependentStatus = new HashSet<>(properties.getLobIndependentStatus());
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
     *          {@code true}, iff the at least one of the following conditions is satisfied
     *
     *          <ul>
     *              <li>
     *                  No project with the given {@code projectId} exists.
     *              </li>
     *
     *              <li>
     *                  The user has bookmarked the project.
     *              </li>
     *
     *              <li>
     *                  The status of the project is LoB independent.
     *              </li>
     *
     *              <li>
     *                  The status is LoB dependent and the user is a manager.
     *              </li>
     *
     *              <li>
     *                  The status is LoB dependent and the LoB of the project is {@code null} or
     *                  the LoB is the same as the user's LoB.
     *              </li>
     *
     *              <li>
     *                  The user has applied for the project.
     *              </li>
     *
     *          </ul>
     */
    @Override
    public boolean hasAccessToProject(Authentication authentication, User user, String projectId) {
        if(!projectService.projectExists(projectId)) {
            return true;
        }

        var project = projectService.getProjectById(projectId);

        var projectStatus = project.getStatus() == null ? null : project.getStatus().toLowerCase();
        if(this.lobIndependentStatus.contains(projectStatus)) {
            return true;
        }

        if(this.lobDependentStatus.contains(projectStatus)) {
            if(userService.userIsManager(user)) {
                return true;
            }

            if(!userAccessService.userHasActiveAccessInterval(user)) {
                return applicationService.userHasAppliedForProject(user, project) ||
                        bookmarkService.userHasBookmark(user, project);
            }

            var projectLob = project.getLob();
            var userLob = userService.getUserData(user).getLob();
            var projectLobNullOrEqual = projectLob == null || projectLob.equalsIgnoreCase(userLob);

            if(projectLobNullOrEqual) {
                return true;
            }
        }

        return applicationService.userHasAppliedForProject(user, project) ||
                bookmarkService.userHasBookmark(user, project);
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
     *          The ID of the project the given {@code user} want to apply
     *          to, not {@code null}.
     *
     * @return
     *          The result of {@link #hasAccessToProject(Authentication, User, String)}
     */
    @Override
    public boolean hasPermissionToApplyToProject(Authentication authentication, User user, String projectId) {
        return hasAccessToProject(authentication, user, projectId);
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
