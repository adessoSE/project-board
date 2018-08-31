package de.adesso.projectboard.core.base.rest.security;

import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;

/**
 * {@link SecurityExpressionRoot} with default implementation for security expressions provided
 * by spring security (like <i>hasRole</i>) that provides custom security expressions used to
 * secure REST interface method invocations.
 *
 * <p>
 *     Expressions are evaluated by a utilizing a {@link ExpressionEvaluator}.
 * </p>
 *
 * @see ExpressionEvaluator
 * @see CustomMethodSecurityExpressionHandler
 */
public class CustomMethodSecurityExpressionRoot extends SecurityExpressionRoot implements MethodSecurityExpressionOperations {

    private final ExpressionEvaluator evaluator;

    private Object filterObject;

    private Object returnObject;

    /**
     * Creates a new instance.
     *
     * @param authentication
     *              The {@link Authentication} to use. Cannot be null.
     *
     * @param evaluator
     *              The {@link ExpressionEvaluator} to use to evaluate the expression.
     */
    public CustomMethodSecurityExpressionRoot(Authentication authentication, ExpressionEvaluator evaluator) {
        super(authentication);

        this.evaluator = evaluator;
    }

    @Override
    public Object getFilterObject() {
        return this.filterObject;
    }

    @Override
    public Object getReturnObject() {
        return this.returnObject;
    }

    @Override
    public Object getThis() {
        return this;
    }

    @Override
    public void setFilterObject(Object obj) {
        this.filterObject = obj;
    }

    @Override
    public void setReturnObject(Object obj) {
        this.returnObject = obj;
    }

    /**
     *
     * @return
     *          <i>true</i>, when the authenticated user is authorized to
     *          view projects, <i>false</i> otherwise
     *
     * @see ExpressionEvaluator#hasAccessToProjects(Authentication)
     * @see de.adesso.projectboard.core.base.rest.project.ProjectController
     */
    public boolean hasAccessToProjects() {
        return evaluator.hasAccessToProjects(getAuthentication());
    }

    /**
     *
     * @return
     *          <i>true</i>, when the authenticated user is authorized to
     *          apply for projects, <i>false</i> otherwise.
     *
     * @see ExpressionEvaluator#hasPermissionToApply(Authentication)
     * @see de.adesso.projectboard.core.base.rest.application.ProjectApplicationController
     */
    public boolean hasPermissionToApply() {
        return evaluator.hasPermissionToApply(getAuthentication());
    }

}
