package de.adesso.projectboard.base.security;

import de.adesso.projectboard.base.user.service.UserServiceImpl;
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
 * @see UserServiceImpl
 */
@Component
public class CustomMethodSecurityExpressionHandler extends DefaultMethodSecurityExpressionHandler {

    private final ExpressionEvaluator evaluator;

    private final UserServiceImpl userService;

    @Autowired
    public CustomMethodSecurityExpressionHandler(ExpressionEvaluator evaluator, UserServiceImpl userService) {
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
