package de.adesso.projectboard.adapter.jira.commenter;

import de.adesso.projectboard.adapter.jira.configuration.JiraConfigurationProperties;
import de.adesso.projectboard.adapter.velocity.VelocityTemplateService;
import de.adesso.projectboard.base.application.handler.ProjectApplicationOfferedEventHandler;
import de.adesso.projectboard.base.application.persistence.ProjectApplication;
import de.adesso.projectboard.base.configuration.ProjectBoardConfigurationProperties;
import de.adesso.projectboard.base.user.persistence.User;
import de.adesso.projectboard.base.user.service.UserService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
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

    private final ProjectBoardConfigurationProperties pbProperties;

    private final JiraConfigurationProperties properties;

    private final VelocityTemplateService velocityTemplateService;

    private final UserService userService;

    public JiraIssueCommenter(RestTemplateBuilder builder,
                              JiraConfigurationProperties properties,
                              ProjectBoardConfigurationProperties pbProperties,
                              VelocityTemplateService velocityTemplateService,
                              UserService userService) {
        this.pbProperties = pbProperties;
        this.userService = userService;
        this.restTemplate = builder
                .basicAuthentication(properties.getUsername(), properties.getPassword())
                .build();

        this.properties = properties;
        this.velocityTemplateService = velocityTemplateService;
    }

    @Override
    public void onApplicationOffered(User offeringUser, ProjectApplication projectApplication) {
        var projectKey = projectApplication.getProject().getId();
        var applicationPayload = getRequestPayload(projectApplication);

        restTemplate.postForLocation(properties.getCommenterUrl(), applicationPayload, projectKey);

        log.debug("Posted comment on Jira Issue {}", projectKey);
    }

    JiraCommentPayload getRequestPayload(ProjectApplication application) {
        var body = getCommentString(pbProperties.getUrl(), application);

        return new JiraCommentPayload(body);
    }

    String getCommentString(String projectboardUrl, ProjectApplication application) {
        var offeredUserdata = userService.getUserDataWithImage(application.getUser());

        var contextMap = Map.of(
                "projectboardUrl", projectboardUrl,
                "offeredUserData", offeredUserdata
        );

        return velocityTemplateService.mergeTemplate("/templates/commenter/JiraIssueComment.vm", contextMap)
                .trim();
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class JiraCommentPayload {

        private String body;

    }

}
