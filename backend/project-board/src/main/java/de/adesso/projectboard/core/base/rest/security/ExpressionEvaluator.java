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
 */
public interface ExpressionEvaluator {

    /**
     *
     * @param authentication
     *          The {@link Authentication} object.
     *
     * @return
     *          <i>true</i>, if the user has access to view projects,
     *          <i>false</i> otherwise.
     */
    boolean hasAccessToProjects(Authentication authentication);

}
