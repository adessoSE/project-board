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

}