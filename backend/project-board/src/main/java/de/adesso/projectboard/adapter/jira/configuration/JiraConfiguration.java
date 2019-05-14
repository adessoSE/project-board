package de.adesso.projectboard.adapter.jira.configuration;

import de.adesso.projectboard.adapter.jira.commenter.JiraIssueCommenter;
import de.adesso.projectboard.adapter.jira.reader.JiraProjectReader;
import de.adesso.projectboard.adapter.velocity.VelocityTemplateService;
import de.adesso.projectboard.base.reader.ProjectReader;
import de.adesso.projectboard.base.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@ConditionalOnProperty(
        prefix = "projectboard.jira",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true
)
@Configuration
@EnableConfigurationProperties(JiraConfigurationProperties.class)
class JiraConfiguration {

    @Autowired
    @Bean
    public ProjectReader projectReader(RestTemplateBuilder builder, JiraConfigurationProperties properties) {
        return new JiraProjectReader(builder, properties);
    }

    @Profile("prod")
    @Autowired
    @Bean
    public JiraIssueCommenter jiraIssueCommenter(RestTemplateBuilder builder,
                                                 JiraConfigurationProperties properties,
                                                 VelocityTemplateService velocityTemplateService,
                                                 UserService userService) {
        return new JiraIssueCommenter(builder, properties, velocityTemplateService, userService);
    }

}
