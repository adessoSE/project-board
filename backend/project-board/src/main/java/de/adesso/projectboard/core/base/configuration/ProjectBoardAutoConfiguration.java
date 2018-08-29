package de.adesso.projectboard.core.base.configuration;

import de.adesso.projectboard.core.base.rest.security.AllowAccessExpressionEvaluator;
import de.adesso.projectboard.core.base.rest.security.ExpressionEvaluator;
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

}
