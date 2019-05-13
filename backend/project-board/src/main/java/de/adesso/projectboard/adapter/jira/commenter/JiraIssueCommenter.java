package de.adesso.projectboard.adapter.jira.commenter;

import de.adesso.projectboard.adapter.jira.configuration.JiraConfigurationProperties;
import de.adesso.projectboard.adapter.velocity.VelocityTemplateService;
import de.adesso.projectboard.base.application.handler.ProjectApplicationOfferedEventHandler;
import de.adesso.projectboard.base.application.persistence.ProjectApplication;
import de.adesso.projectboard.base.user.persistence.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * {@link ProjectApplicationOfferedEventHandler} implementation adding a comment
 * to the jira issue the project of the application originates from.
 */
@Slf4j
public class JiraIssueCommenter implements ProjectApplicationOfferedEventHandler {

    private final RestTemplate restTemplate;

    private final JiraConfigurationProperties properties;

    private final VelocityTemplateService velocityTemplateService;

    public JiraIssueCommenter(RestTemplateBuilder builder,
                              JiraConfigurationProperties properties,
                              VelocityTemplateService velocityTemplateService) {
        this.restTemplate = builder
                .basicAuthentication(properties.getUsername(), properties.getPassword())
                .build();

        this.properties = properties;
        this.velocityTemplateService = velocityTemplateService;
    }

    @Override
    public void onApplicationOffered(User offeringUser, ProjectApplication projectApplication) {
        var projectKey = projectApplication.getProject().getId();
        var applicationPayload = getPayload(offeringUser, projectApplication);

        restTemplate.postForLocation(properties.getCommenterUrl(), applicationPayload, projectKey);

        log.debug("Posted comment on Jira Issue {}", projectKey);
    }

    JiraCommentPayload getPayload(User offeringUser, ProjectApplication application) {
        var body = getCommentString(offeringUser, application);

        return new JiraCommentPayload(body);
    }

    String getCommentString(User offeringUser, ProjectApplication application) {
        var contextMap = Map.of(
            "offeringUser", offeringUser,
            "application", application
        );

        return velocityTemplateService.mergeTemplate("/templates/commenter/JiraIssueComment.vm", contextMap)
                .trim();
    }

    @Data
    @AllArgsConstructor
    static class JiraCommentPayload {

        private String body;

    }

}
