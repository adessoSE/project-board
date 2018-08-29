package de.adesso.projectboard.core.base.rest.security;

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
 */
@Component
public class CustomMethodSecurityExpressionHandler extends DefaultMethodSecurityExpressionHandler {

    private final ExpressionEvaluator evaluator;

    @Autowired
    public CustomMethodSecurityExpressionHandler(ExpressionEvaluator evaluator) {
        this.evaluator = evaluator;
    }

    @Override
    protected MethodSecurityExpressionOperations createSecurityExpressionRoot(Authentication authentication, MethodInvocation invocation) {
        CustomMethodSecurityExpressionRoot root = new CustomMethodSecurityExpressionRoot(authentication, evaluator);

        root.setPermissionEvaluator(getPermissionEvaluator());
        root.setTrustResolver(getTrustResolver());
        root.setRoleHierarchy(getRoleHierarchy());

        return root;
    }

}
