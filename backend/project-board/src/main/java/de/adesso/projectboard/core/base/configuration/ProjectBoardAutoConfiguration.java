package de.adesso.projectboard.core.base.configuration;

import de.adesso.projectboard.core.base.reader.NoActionProjectReader;
import de.adesso.projectboard.core.base.reader.ProjectReader;
import de.adesso.projectboard.core.base.rest.security.AllowAccessExpressionEvaluator;
import de.adesso.projectboard.core.base.rest.security.AuthenticationInfo;
import de.adesso.projectboard.core.base.rest.security.DefaultAuthenticationInfo;
import de.adesso.projectboard.core.base.rest.security.ExpressionEvaluator;
import de.adesso.projectboard.core.base.rest.user.application.NoActionApplicationHandler;
import de.adesso.projectboard.core.base.rest.user.application.ProjectApplicationHandler;
import de.adesso.projectboard.core.base.rest.user.useraccess.NoActionAccessHandler;
import de.adesso.projectboard.core.base.rest.user.useraccess.UserAccessHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProjectBoardAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(ExpressionEvaluator.class)
    public ExpressionEvaluator allowAccessExpressionEvaluator() {
        return new AllowAccessExpressionEvaluator();
    }

    @Bean
    @ConditionalOnMissingBean(ProjectApplicationHandler.class)
    public ProjectApplicationHandler noActionApplicationHandler() {
        return new NoActionApplicationHandler();
    }

    @Bean
    @ConditionalOnMissingBean(UserAccessHandler.class)
    public UserAccessHandler noActionAccessHandler() {
        return new NoActionAccessHandler();
    }

    @Bean
    @ConditionalOnMissingBean(ProjectReader.class)
    public ProjectReader noActionProjectReader() {
        return new NoActionProjectReader();
    }

    @Bean
    @ConditionalOnMissingBean(AuthenticationInfo.class)
    public AuthenticationInfo defaultAuthenticationInfo() {
        return new DefaultAuthenticationInfo();
    }

}
