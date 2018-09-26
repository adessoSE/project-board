package de.adesso.projectboard.core.base.rest.security;

import de.adesso.projectboard.core.base.rest.project.persistence.Project;
import de.adesso.projectboard.core.base.rest.user.persistence.User;
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
     * @return
     *          {@code true}
     */
    @Override
    public boolean hasPermissionToApply(Authentication authentication, User user) {
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
     * @return
     *          {@code true}
     */
    @Override
    public boolean hasPermissionToCreateProjects(Authentication authentication, User user) {
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
     *          The id of the {@link Project} the user wants to update.
     *
     * @return
     *          {@code true}
     */
    @Override
    public boolean hasPermissionToEditProject(Authentication authentication, User user, String projectId) {
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
