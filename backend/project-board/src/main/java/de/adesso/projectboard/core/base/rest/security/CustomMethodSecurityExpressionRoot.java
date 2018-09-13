package de.adesso.projectboard.core.base.rest.security;

import de.adesso.projectboard.core.base.rest.user.UserService;
import de.adesso.projectboard.core.base.rest.user.persistence.User;
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
 * @see UserService
 * @see CustomMethodSecurityExpressionHandler
 */
public class CustomMethodSecurityExpressionRoot extends SecurityExpressionRoot implements MethodSecurityExpressionOperations {

    private final ExpressionEvaluator evaluator;

    private final UserService userService;

    private Object filterObject;

    private Object returnObject;

    /**
     * Creates a new instance.
     *  @param authentication
     *          The {@link Authentication} to use. Cannot be null.
     *
     * @param evaluator
     *          The {@link ExpressionEvaluator} to use to evaluate the expression.
     *
     * @param userService
     *          The {@link UserService} to manage users.
     */
    public CustomMethodSecurityExpressionRoot(Authentication authentication, ExpressionEvaluator evaluator, UserService userService) {
        super(authentication);

        this.evaluator = evaluator;
        this.userService = userService;
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
     *          <i>true</i>, if the authenticated user is authorized to
     *          view projects, <i>false</i> otherwise
     *
     * @see ExpressionEvaluator#hasAccessToProjects(Authentication, User)
     * @see de.adesso.projectboard.core.base.rest.project.ProjectController
     */
    public boolean hasAccessToProjects() {
        return evaluator.hasAccessToProjects(getAuthentication(), userService.getCurrentUser());
    }

    /**
     *
     * @param projectId
     *          The id of the {@link de.adesso.projectboard.core.base.rest.project.persistence.AbstractProject}
     *          the user wants to access.
     *
     * @return
     *          <i>true</i>, if the authenticated user is authorized to
     *          view the project, <i>false</i> otherwise.
     *
     * @see ExpressionEvaluator#hasAccessToProject(Authentication, User, long)
     * @see de.adesso.projectboard.core.base.rest.project.ProjectController
     */
    public boolean hasAccessToProject(long projectId) {
        return evaluator.hasAccessToProject(getAuthentication(), userService.getCurrentUser(), projectId);
    }

    /**
     *
     * @return
     *          <i>true</i>, if the authenticated user is authorized to
     *          apply for projects, <i>false</i> otherwise.
     *
     * @see ExpressionEvaluator#hasPermissionToApply(Authentication, User)
     * @see de.adesso.projectboard.core.base.rest.user.UserController
     */
    public boolean hasPermissionToApply() {
        return evaluator.hasPermissionToApply(getAuthentication(), userService.getCurrentUser());
    }

    /**
     *
     * @param userId
     *          The id of the {@link User}
     *          the current user wants to access.
     *
     * @return
     *          <i>true</i>, if the authenticated user is authorized to access
     *          the user, <i>false</i> otherwise.
     *
     * @see ExpressionEvaluator#hasPermissionToAccessUser(Authentication, User, String)
     * @see de.adesso.projectboard.core.base.rest.user.UserController
     */
    public boolean hasPermissionToAccessUser(String userId) {
        return evaluator.hasPermissionToAccessUser(getAuthentication(), userService.getCurrentUser(), userId);
    }

    /**
     *
     * @param userId
     *          The id of the {@link User}
     *          the current user wants to access.
     *
     * @return
     *          <i>true</i>, if the authenticated user is authorized to access the
     *          user with elevated access permissions, <i>false</i> otherwise
     *
     * @see ExpressionEvaluator#hasElevatedAccessToUser(Authentication, User, String)
     * @see de.adesso.projectboard.core.rest.useraccess.UserAccessController
     */
    public boolean hasElevatedAccessToUser(String userId) {
        return evaluator.hasElevatedAccessToUser(getAuthentication(), userService.getCurrentUser(), userId);
    }

}
