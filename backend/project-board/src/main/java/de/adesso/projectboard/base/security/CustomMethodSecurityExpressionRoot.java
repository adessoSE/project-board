package de.adesso.projectboard.base.security;

import de.adesso.projectboard.base.access.rest.UserAccessController;
import de.adesso.projectboard.base.project.persistence.Project;
import de.adesso.projectboard.base.project.rest.NonPageableProjectController;
import de.adesso.projectboard.base.user.persistence.User;
import de.adesso.projectboard.base.user.rest.UserController;
import de.adesso.projectboard.base.user.service.UserAuthService;
import de.adesso.projectboard.base.user.service.UserService;
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

    private final UserService userService;

    private final UserAuthService userAuthService;

    private Object filterObject;

    private Object returnObject;


    public CustomMethodSecurityExpressionRoot(Authentication authentication,
                                              ExpressionEvaluator evaluator,
                                              UserService userService,
                                              UserAuthService userAuthService) {
        super(authentication);

        this.evaluator = evaluator;
        this.userService = userService;
        this.userAuthService = userAuthService;
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
     * @see NonPageableProjectController
     */
    public boolean hasAccessToProjects() {
        // check if the user has a corresponding User object
        if(currentUserExists()) {
            return evaluator.hasAccessToProjects(getAuthentication(), userAuthService.getAuthenticatedUser());
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
     * @see NonPageableProjectController
     */
    public boolean hasAccessToProject(String projectId) {
        // check if the user has a corresponding User object
        if(currentUserExists()) {
            return evaluator.hasAccessToProject(getAuthentication(), userAuthService.getAuthenticatedUser(), projectId);
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
     * @see UserController
     */
    public boolean hasPermissionToApply() {
        // check if the user has a corresponding User object
        if(currentUserExists()) {
            return evaluator.hasPermissionToApply(getAuthentication(), userAuthService.getAuthenticatedUser());
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
     * @see UserController
     */
    public boolean hasPermissionToAccessUser(String userId) {
        // check if the user has a corresponding User object
        if(currentUserExists()) {
            return evaluator.hasPermissionToAccessUser(getAuthentication(), userAuthService.getAuthenticatedUser(), userId);
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
        if(currentUserExists()) {
            return evaluator.hasElevatedAccessToUser(getAuthentication(), userAuthService.getAuthenticatedUser(), userId);
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
     * @see NonPageableProjectController
     */
    public boolean hasPermissionToCreateProjects() {
        // check if the user has a corresponding User object
        if(currentUserExists()) {
            return evaluator.hasPermissionToCreateProjects(getAuthentication(), userAuthService.getAuthenticatedUser());
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
     * @see NonPageableProjectController
     */
    public boolean hasPermissionToEditProject(String projectId) {
        // check if the user has a corresponding User object
        if(currentUserExists()) {
            return evaluator.hasPermissionToEditProject(getAuthentication(), userAuthService.getAuthenticatedUser(), projectId);
        }

        return false;
    }

    /**
     *
     * @return
     *          The result of {@link UserService#userExists(String)} with the returned value
     *          of {@link UserAuthService#getAuthenticatedUserId()} as the argument.
     */
    private boolean currentUserExists() {
        return userService.userExists(userAuthService.getAuthenticatedUserId());
    }

}
