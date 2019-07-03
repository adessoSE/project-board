package de.adesso.projectboard.base.security;

import de.adesso.projectboard.base.project.persistence.Project;
import de.adesso.projectboard.base.user.persistence.User;
import org.springframework.security.core.Authentication;

/**
 * Auto-configured implementation of the {@link ExpressionEvaluator} interface. Method implementations
 * always return {@code true}.
 *
 * @see ExpressionEvaluator
 */
public class AllowAccessExpressionEvaluator implements ExpressionEvaluator {

    /**
     *
     * @param authentication
     *          The {@link Authentication} object.
     *
     * @param user
     *          The {@link User} object of the currently authenticated user.
     *
     * @return
     *          {@code true}
     */
    @Override
    public boolean hasAccessToProjects(Authentication authentication, User user) {
        return true;
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
     *          {@code true}
     */
    @Override
    public boolean hasAccessToProject(Authentication authentication, User user, String projectId) {
        return true;
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
     *          {@code true}
     */
    @Override
    public boolean hasPermissionToApplyToProject(Authentication authentication, User user, String projectId) {
        return true;
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
     *          {@code true}
     */
    @Override
    public boolean hasPermissionToAccessUser(Authentication authentication, User user, String userId) {
        return true;
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
     *          {@code true}
     */
    @Override
    public boolean hasElevatedAccessToUser(Authentication authentication, User user, String userId) {
        return true;
    }

}
