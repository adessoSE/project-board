package de.adesso.projectboard.core.base.rest.security;

import de.adesso.projectboard.core.base.rest.project.persistence.Project;
import de.adesso.projectboard.core.base.rest.user.UserAccessController;
import de.adesso.projectboard.core.base.rest.user.persistence.UserService;
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
     *
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
     *          The result of {@link ExpressionEvaluator#hasAccessToProjects(Authentication, User)}
     *          when the user is authenticated (a {@link User} object is present), {@code false} otherwise.
     *
     * @see ExpressionEvaluator#hasAccessToProjects(Authentication, User)
     * @see de.adesso.projectboard.core.base.rest.project.ProjectController
     */
    public boolean hasAccessToProjects() {
        // check if the user has a corresponding User object
        if(userService.userExists(userService.getCurrentUserId())) {
            return evaluator.hasAccessToProjects(getAuthentication(), userService.getCurrentUser());
        }

        return false;
    }

    /**
     *
     * @param projectId
     *          The id of the {@link Project}
     *          the user wants to access.
     *
     * @return
     *          The result of {@link ExpressionEvaluator#hasAccessToProject(Authentication, User, String)}
                when the user is authenticated (a {@link User} object is present), {@code false} otherwise.
     *
     * @see ExpressionEvaluator#hasAccessToProject(Authentication, User, String)
     * @see de.adesso.projectboard.core.base.rest.project.ProjectController
     */
    public boolean hasAccessToProject(String projectId) {
        // check if the user has a corresponding User object
        if(userService.userExists(userService.getCurrentUserId())) {
            return evaluator.hasAccessToProject(getAuthentication(), userService.getCurrentUser(), projectId);
        }

        return false;
    }

    /**
     *
     * @return
     *          The result of {@link ExpressionEvaluator#hasPermissionToApply(Authentication, User)}
     *          when the user is authenticated (a {@link User} object is present), {@code false} otherwise.
     *
     * @see ExpressionEvaluator#hasPermissionToApply(Authentication, User)
     * @see de.adesso.projectboard.core.base.rest.user.UserController
     */
    public boolean hasPermissionToApply() {
        // check if the user has a corresponding User object
        if(userService.userExists(userService.getCurrentUserId())) {
            return evaluator.hasPermissionToApply(getAuthentication(), userService.getCurrentUser());
        }

        return false;
    }

    /**
     *
     * @param userId
     *          The id of the {@link User}
     *          the current user wants to access.
     *
     * @return
     *          The result of {@link ExpressionEvaluator#hasPermissionToAccessUser(Authentication, User, String)}
     *          when the user is authenticated (a {@link User} object is present), {@code false} otherwise.
     *
     * @see ExpressionEvaluator#hasPermissionToAccessUser(Authentication, User, String)
     * @see de.adesso.projectboard.core.base.rest.user.UserController
     */
    public boolean hasPermissionToAccessUser(String userId) {
        // check if the user has a corresponding User object
        if(userService.userExists(userService.getCurrentUserId())) {
            return evaluator.hasPermissionToAccessUser(getAuthentication(), userService.getCurrentUser(), userId);
        }

        return false;
    }

    /**
     *
     * @param userId
     *          The id of the {@link User} the current user wants to access.
     *
     * @return
     *          The result of {@link ExpressionEvaluator#hasElevatedAccessToUser(Authentication, User, String)}
     *          when the user is authenticated (a {@link User} object is present), {@code false} otherwise.
     *
     * @see ExpressionEvaluator#hasElevatedAccessToUser(Authentication, User, String)
     * @see UserAccessController
     */
    public boolean hasElevatedAccessToUser(String userId) {
        // check if the user has a corresponding User object
        if(userService.userExists(userService.getCurrentUserId())) {
            return evaluator.hasElevatedAccessToUser(getAuthentication(), userService.getCurrentUser(), userId);
        }

        return false;
    }

    /**
     *
     * @return
     *          The result of {@link ExpressionEvaluator#hasPermissionToCreateProjects(Authentication, User)}
     *          when the user is authenticated (a {@link User} object is present), {@code false} otherwise.
     *
     * @see ExpressionEvaluator#hasPermissionToCreateProjects(Authentication, User)
     * @see de.adesso.projectboard.core.base.rest.project.ProjectController
     */
    public boolean hasPermissionToCreateProjects() {
        // check if the user has a corresponding User object
        if(userService.userExists(userService.getCurrentUserId())) {
            return evaluator.hasPermissionToCreateProjects(getAuthentication(), userService.getCurrentUser());
        }

        return false;
    }

    /**
     *
     * @param projectId
     *          The id of the {@link Project} the user wants to access.
     *
     * @return
     *          The result of {@link ExpressionEvaluator#hasPermissionToEditProject(Authentication, User, String)}
     *          when the user is authenticated (a {@link User} object is present), {@code false} otherwise.
     *
     * @see ExpressionEvaluator#hasPermissionToEditProject(Authentication, User, String)
     * @see de.adesso.projectboard.core.base.rest.project.ProjectController
     */
    public boolean hasPermissionToEditProject(String projectId) {
        // check if the user has a corresponding User object
        if(userService.userExists(userService.getCurrentUserId())) {
            return evaluator.hasPermissionToEditProject(getAuthentication(), userService.getCurrentUser(), projectId);
        }

        return false;
    }

}
