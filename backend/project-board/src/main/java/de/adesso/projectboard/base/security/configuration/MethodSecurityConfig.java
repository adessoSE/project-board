package de.adesso.projectboard.base.security.configuration;

import de.adesso.projectboard.base.security.CustomMethodSecurityExpressionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;

/**
 * A {@link GlobalMethodSecurityConfiguration} to enable custom spring security expressions.
 *
 * @see CustomMethodSecurityExpressionHandler
 */
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class MethodSecurityConfig extends GlobalMethodSecurityConfiguration {

    private final CustomMethodSecurityExpressionHandler handler;

    @Autowired
    public MethodSecurityConfig(CustomMethodSecurityExpressionHandler handler) {
        this.handler = handler;
    }

    @Override
    protected MethodSecurityExpressionHandler createExpressionHandler() {
        return handler;
    }

}
