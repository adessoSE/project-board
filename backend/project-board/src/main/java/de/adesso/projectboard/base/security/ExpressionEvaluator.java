package de.adesso.projectboard.base.security;

import de.adesso.projectboard.base.project.persistence.Project;
import de.adesso.projectboard.base.user.persistence.User;
import org.springframework.security.core.Authentication;

/**
 * Interface used in {@link CustomMethodSecurityExpressionRoot} to evaluate custom expressions.
 *
 * <p>
 *     <b>Note:</b> Method implementations have to be thread safe!
 * </p>
 *
 * @see CustomMethodSecurityExpressionRoot
 * @see AllowAccessExpressionEvaluator
 */
public interface ExpressionEvaluator {

    /**
     *
     * @param authentication
     *          The {@link Authentication} object.
     *
     * @param user
     *          The {@link User} object of the currently authenticated user.
     *
     * @return
     *          {@code true}, if the user is permitted to view projects,
     *          {@code false} otherwise.
     *
     * @see CustomMethodSecurityExpressionRoot#hasAccessToProjects()
     */
    boolean hasAccessToProjects(Authentication authentication, User user);

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
     *          {@code true}, if the user is permitted to access the project,
     *          {@code false} otherwise.
     *
     * @see CustomMethodSecurityExpressionRoot#hasAccessToProject(String)
     */
    boolean hasAccessToProject(Authentication authentication, User user, String projectId);

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
     *          A boolean indicating whether or not the given {@code user}
     *          is allowed to apply to the project with the given {@code projectId}.
     *
     * @see CustomMethodSecurityExpressionRoot#hasPermissionToApplyToProject(String)
     */
    boolean hasPermissionToApplyToProject(Authentication authentication, User user, String projectId);

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
     *          {@code true}, if the user is permitted to access the user,
     *          {@code false} otherwise.
     *
     * @see CustomMethodSecurityExpressionRoot#hasPermissionToAccessUser(String)
     */
    boolean hasPermissionToAccessUser(Authentication authentication, User user, String userId);

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
     *          {@code true}, if the user has elevated access to the user,
     *          {@code false} otherwise.
     *
     * @see CustomMethodSecurityExpressionRoot#hasElevatedAccessToUser(String)
     */
    default boolean hasElevatedAccessToUser(Authentication authentication, User user, String userId) {
        return true;
    }

}
