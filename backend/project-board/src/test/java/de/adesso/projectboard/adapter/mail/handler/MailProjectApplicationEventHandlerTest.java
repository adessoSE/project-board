package de.adesso.projectboard.adapter.mail.handler;

import de.adesso.projectboard.adapter.mail.MailSenderAdapter;
import de.adesso.projectboard.adapter.mail.VelocityMailTemplateService;
import de.adesso.projectboard.adapter.mail.persistence.SimpleMessage;
import de.adesso.projectboard.base.application.persistence.ProjectApplication;
import de.adesso.projectboard.base.project.persistence.Project;
import de.adesso.projectboard.base.user.persistence.User;
import de.adesso.projectboard.base.user.persistence.data.UserData;
import de.adesso.projectboard.base.user.service.UserService;
import de.adesso.projectboard.reader.JiraConfigurationProperties;
import org.apache.velocity.util.Pair;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Map;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class MailProjectApplicationEventHandlerTest {

    private static final String JIRA_ISSUE_URL = "https://jira.com/issues/";

    @Mock
    private MailSenderAdapter mailSenderAdapterMock;

    @Mock
    private UserService userServiceMock;

    @Mock
    private VelocityMailTemplateService velocityMailTemplateServiceMock;

    @Mock
    private JiraConfigurationProperties jiraConfigPropertiesMock;

    @Mock
    private ProjectApplication projectApplicationMock;

    @Mock
    private User applicantUserMock;

    @Mock
    private User managerUserMock;

    @Mock
    private UserData applicantDataMock;

    @Mock
    private UserData managerDataMock;

    @Mock
    private Project projectMock;

    private MailProjectApplicationEventHandler mailProjectApplicationEventHandler;

    @Before
    public void setUp() {
        given(jiraConfigPropertiesMock.getIssueUrl()).willReturn(JIRA_ISSUE_URL);

        this.mailProjectApplicationEventHandler =
                new MailProjectApplicationEventHandler(mailSenderAdapterMock, userServiceMock, velocityMailTemplateServiceMock, jiraConfigPropertiesMock);
    }

    @Test
    public void onApplicationReceivedQueuesExpectedMessage() {
        // given
        var projectId = "PB-2";
        var expectedTemplatePath = "/templates/mail/UserAppliedForProject.vm";
        var expectedSubject = "This is a subject :O";
        var expectedText = "Cool text :)";
        var subjectTextPair = new Pair<>(expectedSubject, expectedText);
        var expectedMessage = new SimpleMessage(applicantUserMock, managerUserMock, expectedSubject, expectedText);
        var jiraIssueLink = JIRA_ISSUE_URL + projectId;

        var expectedContextMap = Map.of(
                "projectApplication", projectApplicationMock,
                "applicantData", applicantDataMock,
                "managerData", managerDataMock,
                "jiraIssueLink", jiraIssueLink
        );

        given(projectApplicationMock.getUser()).willReturn(applicantUserMock);
        given(projectApplicationMock.getProject()).willReturn(projectMock);

        given(projectMock.getId()).willReturn(projectId);

        given(userServiceMock.getManagerOfUser(applicantUserMock)).willReturn(managerUserMock);
        given(userServiceMock.getUserData(applicantUserMock)).willReturn(applicantDataMock);
        given(userServiceMock.getUserData(managerUserMock)).willReturn(managerDataMock);

        given(velocityMailTemplateServiceMock.getSubjectAndText(expectedTemplatePath, expectedContextMap))
            .willReturn(subjectTextPair);

        // when
        mailProjectApplicationEventHandler.onApplicationReceived(projectApplicationMock);

        // then
        verify(mailSenderAdapterMock).queueMessage(expectedMessage);
    }

}
