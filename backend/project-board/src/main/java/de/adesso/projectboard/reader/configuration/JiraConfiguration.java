package de.adesso.projectboard.reader.configuration;

import de.adesso.projectboard.base.reader.ProjectReader;
import de.adesso.projectboard.reader.JiraProjectReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
    public ProjectReader projectReader(RestTemplateBuilder restTemplateBuilder, JiraConfigurationProperties jiraConfigurationProperties) {
        return new JiraProjectReader(restTemplateBuilder, jiraConfigurationProperties);
    }

}
