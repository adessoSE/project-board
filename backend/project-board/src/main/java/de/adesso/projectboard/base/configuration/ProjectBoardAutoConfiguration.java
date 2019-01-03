package de.adesso.projectboard.base.configuration;

import de.adesso.projectboard.base.access.handler.NoActionAccessHandler;
import de.adesso.projectboard.base.access.handler.UserAccessHandler;
import de.adesso.projectboard.base.application.handler.NoActionApplicationHandler;
import de.adesso.projectboard.base.application.handler.ProjectApplicationHandler;
import de.adesso.projectboard.base.reader.NoActionProjectReader;
import de.adesso.projectboard.base.reader.ProjectReader;
import de.adesso.projectboard.base.security.AllowAccessExpressionEvaluator;
import de.adesso.projectboard.base.security.AuthenticationInfoRetriever;
import de.adesso.projectboard.base.security.DefaultAuthenticationInfoRetriever;
import de.adesso.projectboard.base.security.ExpressionEvaluator;
import de.adesso.projectboard.base.user.service.DefaultUserAuthService;
import de.adesso.projectboard.base.user.service.UserAuthService;
import de.adesso.projectboard.base.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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
    @ConditionalOnMissingBean(AuthenticationInfoRetriever.class)
    public AuthenticationInfoRetriever defaultAuthenticationInfo() {
        return new DefaultAuthenticationInfoRetriever();
    }

    @Bean
    @ConditionalOnMissingBean(UserAuthService.class)
    @Autowired
    public UserAuthService defaultUserAuthService(UserService userService, AuthenticationInfoRetriever retriever) {
        return new DefaultUserAuthService(userService, retriever);
    }

}
