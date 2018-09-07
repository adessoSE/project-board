package de.adesso.projectboard.core.base.rest.security;

import org.springframework.security.core.Authentication;

/**
 * Autoconfigured implementation of the {@link ExpressionEvaluator}. Method implementations
 * always return <i>true</i>.
 *
 * @see ExpressionEvaluator
 */
public class AllowAccessExpressionEvaluator implements ExpressionEvaluator {

    /**
     *
     * @param authentication
     *          The {@link Authentication} object.
     *
     * @return
     *          <i>true</i>
     */
    @Override
    public boolean hasAccessToProjects(Authentication authentication) {
        return true;
    }

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
     *          <i>true</i>
     */
    @Override
    public boolean hasAccessToProject(Authentication authentication, long projectId) {
        return true;
    }

    /**
     *
     * @param authentication
     *          The {@link Authentication} object.
     *
     * @return
     *          <i>true</i>
     */
    @Override
    public boolean hasPermissionToApply(Authentication authentication) {
        return false;
    }

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
     *          <i>true</i>
     */
    @Override
    public boolean hasPermissionToAccessUser(Authentication authentication, String userId) {
        return false;
    }

}
