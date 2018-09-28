package de.adesso.projectboard.core.base.rest.security;

import de.adesso.projectboard.core.base.rest.user.persistence.UserService;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/**
 * {@link DefaultMethodSecurityExpressionHandler} to add methods to secure
 * method invocations (e.g. via {@link org.springframework.security.access.prepost.PreAuthorize})
 *
 * @see ExpressionEvaluator
 * @see UserService
 */
@Component
public class CustomMethodSecurityExpressionHandler extends DefaultMethodSecurityExpressionHandler {

    private final ExpressionEvaluator evaluator;

    private final UserService userService;

    @Autowired
    public CustomMethodSecurityExpressionHandler(ExpressionEvaluator evaluator, UserService userService) {
        this.evaluator = evaluator;
        this.userService = userService;
    }

    @Override
    protected MethodSecurityExpressionOperations createSecurityExpressionRoot(Authentication authentication, MethodInvocation invocation) {
        CustomMethodSecurityExpressionRoot root = new CustomMethodSecurityExpressionRoot(authentication, evaluator, userService);

        root.setPermissionEvaluator(getPermissionEvaluator());
        root.setTrustResolver(getTrustResolver());
        root.setRoleHierarchy(getRoleHierarchy());

        return root;
    }

}
