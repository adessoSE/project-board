package de.adesso.projectboard.adapter.jira.commenter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.adesso.projectboard.adapter.jira.configuration.JiraConfigurationProperties;
import de.adesso.projectboard.adapter.velocity.VelocityTemplateService;
import de.adesso.projectboard.adapter.velocity.configuration.VelocityConfiguration;
import de.adesso.projectboard.base.application.persistence.ProjectApplication;
import de.adesso.projectboard.base.project.persistence.Project;
import de.adesso.projectboard.base.user.persistence.User;
import de.adesso.projectboard.base.user.persistence.data.UserData;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = VelocityConfiguration.class)
public class JiraIssueCommenterIntegrationTest {

    private final String COMMENTER_URL = "rest/api/2/issue/{issueKey}/comment";

    @MockBean
    private JiraConfigurationProperties properties;

    @MockBean
    private RestTemplateBuilder builder;

    @Autowired
    private VelocityTemplateService velocityTemplateService;

    private MockRestServiceServer server;

    private JiraIssueCommenter commenter;

    @Before
    public void setUp() {
        // set up properties mock
        given(properties.getCommenterUrl()).willReturn(COMMENTER_URL);
        given(properties.getUsername()).willReturn("");
        given(properties.getPassword()).willReturn("");

        // spring's RestClientTest annotation causes problems
        // because the username/password for basic authentication can't
        // be null since spring 5.1, but the properties mock bean injected returns
        // null when retrieving the username/password
        // -> configure manually

        // initialize a new rest template that should be used by the
        // reader
        var restTemplate = new RestTemplate();

        // configure the builder to return the template
        given(builder.basicAuthentication(anyString(), anyString())).willReturn(builder);
        given(builder.build()).willReturn(restTemplate);

        // bind the server to the given rest template
        this.server = MockRestServiceServer.bindTo(restTemplate)
                .build();

        this.commenter = new JiraIssueCommenter(builder, properties, velocityTemplateService);
    }

    @Test
    public void onApplicationOfferedPostsExpectedBody() throws JsonProcessingException {
        // given
        var offeringUserFirstName = "John";
        var offeringUserLastName = "Doe";
        var offeringUser = new User("offering-user");
        var offeringUserData = new UserData(offeringUser, offeringUserFirstName, offeringUserLastName, "mail", "LoB");
        offeringUser.setUserData(offeringUserData);

        var offeredUserFirstName = "Jane";
        var offeredUserLastName = "Doe";
        var offeredUser = new User("offered-user");
        var offeredUserData = new UserData(offeredUser, offeredUserFirstName, offeredUserLastName, "mail", "LoB");
        offeredUser.setUserData(offeredUserData);

        var projectId = "PROJ-1";
        var project = new Project().setId(projectId);
        var application = new ProjectApplication(project, "Comment", offeredUser, LocalDateTime.now());

        var expectedText = String.format("%s %s hat seinen Mitarbeiter %s %s angeboten.", offeringUserFirstName, offeringUserLastName,
                offeredUserFirstName, offeredUserLastName);
        var expectedBody = new JiraIssueCommenter.JiraCommentPayload(expectedText);
        var expectedBodyJson = new ObjectMapper().writer().writeValueAsString(expectedBody);

        server.expect(requestTo("/rest/api/2/issue/PROJ-1/comment"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().string(expectedBodyJson))
                .andRespond(withStatus(HttpStatus.CREATED));

        // when
        commenter.onApplicationOffered(offeringUser, application);

        // then
        server.verify();
    }

}
