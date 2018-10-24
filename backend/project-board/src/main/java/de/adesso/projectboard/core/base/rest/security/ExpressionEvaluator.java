package de.adesso.projectboard.core.base.rest.security;

import de.adesso.projectboard.core.base.rest.project.persistence.Project;
import de.adesso.projectboard.core.base.rest.user.persistence.User;
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
     * @return
     *          {@code true}, if the user is permitted to apply
     *          for projects, {@code false} otherwise.
     *
     * @see CustomMethodSecurityExpressionRoot#hasPermissionToApply()
     */
    boolean hasPermissionToApply(Authentication authentication, User user);

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
     * @return
     *          {@code true}, if the user is permitted to create projects,
     *          {@code false} otherwise.
     *
     * @see CustomMethodSecurityExpressionRoot#hasPermissionToAccessUser(String)
     */
    boolean hasPermissionToCreateProjects(Authentication authentication, User user);

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
     *          {@code true}, if the user is permitted to update the project,
     *          {@code false} otherwise.
     */
    boolean hasPermissionToEditProject(Authentication authentication, User user, String projectId);

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
