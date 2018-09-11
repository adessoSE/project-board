package de.adesso.projectboard.core.base.rest.security;

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
     * @return
     *          <i>true</i>, if the user is permitted to view projects,
     *          <i>false</i> otherwise.
     *
     * @see CustomMethodSecurityExpressionRoot#hasAccessToProjects()
     */
    boolean hasAccessToProjects(Authentication authentication);

    /**
     *
     * @param authentication
     *          The {@link Authentication} object.
     *
     * @param projectId
     *          The id of the {@link de.adesso.projectboard.core.base.rest.project.persistence.AbstractProject}
     *          the user wants to access.
     *
     * @return
     *          <i>true</i>, if the user is permitted to access the project,
     *          <i>false</i> otherwise.
     *
     * @see CustomMethodSecurityExpressionRoot#hasAccessToProject(long)
     */
    boolean hasAccessToProject(Authentication authentication, long projectId);

    /**
     *
     * @param authentication
     *          The {@link Authentication} object.
     *
     * @return
     *          <i>true</i>, if the user is permitted to apply
     *          for projects, <i>false</i> otherwise.
     *
     * @see CustomMethodSecurityExpressionRoot#hasPermissionToApply()
     */
    boolean hasPermissionToApply(Authentication authentication);

    /**
     *
     * @param authentication
     *          The {@link Authentication} object.
     *
     * @param userId
     *          The id of the {@link de.adesso.projectboard.core.base.rest.user.persistence.User}
     *          the current user wants to access.
     *
     * @return
     *          <i>true</i>, if the user is permitted to access the user,
     *          <i>false</i> otherwise.
     *
     * @see CustomMethodSecurityExpressionRoot#hasPermissionToAccessUser(String)
     */
    boolean hasPermissionToAccessUser(Authentication authentication, String userId);

    /**
     *
     * @param authentication
     *          The {@link Authentication} object.
     *
     * @param userId
     *          The id of the {@link de.adesso.projectboard.core.base.rest.user.persistence.User}
     *          the current user wants to access.
     *
     * @return
     *          <i>true</i>, if the user has elevated access to the user,
     *          <i>false</i> otherwise.
     *
     * @see CustomMethodSecurityExpressionRoot#hasElevatedAccessToUser(String)
     */
    default boolean hasElevatedAccessToUser(Authentication authentication, String userId) {
        return true;
    }

}
