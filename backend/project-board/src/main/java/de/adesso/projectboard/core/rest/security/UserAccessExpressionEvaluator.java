package de.adesso.projectboard.core.rest.security;

import de.adesso.projectboard.core.base.rest.project.persistence.Project;
import de.adesso.projectboard.core.base.rest.security.ExpressionEvaluator;
import de.adesso.projectboard.core.base.rest.user.UserService;
import de.adesso.projectboard.core.base.rest.user.persistence.SuperUser;
import de.adesso.projectboard.core.base.rest.user.persistence.User;
import de.adesso.projectboard.core.base.rest.user.useraccess.persistence.UserAccessInfo;
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
 * @see UserService
 */
@Profile("user-access")
@Service
public class UserAccessExpressionEvaluator implements ExpressionEvaluator {

    /**
     * Gets the currently authenticated user from the {@link UserService}
     * and retrieves the latest {@link UserAccessInfo} object for that user.
     * When the {@link UserAccessInfo#getAccessEnd() access end date} is
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
     *          The result of {@link #hasAccessToProjects(Authentication, User)}.
     *
     * @see #hasAccessToProjects(Authentication, User)
     */
    @Override
    public boolean hasAccessToProject(Authentication authentication, User user, String projectId) {
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
     *          {@code true}, if the given {@link User} is a {@link SuperUser},
     *          {@code false} otherwise.
     */
    @Override
    public boolean hasPermissionToEditProject(Authentication authentication, User user, String projectId) {
        return user instanceof SuperUser;
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
     *          {@code true}, when a user with the given {@code userId} is included
     *          in the {@link Set} of the {@link User#getStaffMembers() user's staff members},
     *          {@code false} otherwise.
     *
     * @see SuperUser
     */
    @Override
    public boolean hasElevatedAccessToUser(Authentication authentication, User user, String userId) {
        return user.getStaffMembers().stream()
                .anyMatch(staffMember -> staffMember.getId().equals(userId));
    }

}
