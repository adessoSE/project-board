package de.adesso.projectboard.adapter.mail.handler;

import de.adesso.projectboard.adapter.mail.MailSenderService;
import de.adesso.projectboard.adapter.mail.VelocityMailTemplateService;
import de.adesso.projectboard.adapter.mail.persistence.SimpleMessage;
import de.adesso.projectboard.base.application.persistence.ProjectApplication;
import de.adesso.projectboard.base.user.persistence.User;
import de.adesso.projectboard.base.user.persistence.data.UserData;
import de.adesso.projectboard.base.user.service.UserService;
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

    @Mock
    private MailSenderService mailSenderServiceMock;

    @Mock
    private UserService userServiceMock;

    @Mock
    private VelocityMailTemplateService velocityMailTemplateServiceMock;

    @Mock
    private ProjectApplication projectApplicationMock;

    @Mock
    private User applicantUserMock;

    @Mock
    private User managerUserMock;

    @Mock
    private UserData applicantDataMock;

    private MailProjectApplicationEventHandler mailProjectApplicationEventHandler;

    @Before
    public void setUp() {
        this.mailProjectApplicationEventHandler =
                new MailProjectApplicationEventHandler(mailSenderServiceMock, userServiceMock, velocityMailTemplateServiceMock);
    }

    @Test
    public void onApplicationReceivedQueuesExpectedMessage() {
        // given
        var expectedTemplatePath = "/templates/mail/UserAppliedForProject.vm";
        var expectedSubject = "This is a subject :O";
        var expectedText = "Cool text :)";
        var subjectTextPair = new Pair<>(expectedSubject, expectedText);
        var expectedMessage = new SimpleMessage(applicantUserMock, managerUserMock, expectedSubject, expectedText);
        var expectedContextMap = Map.of(
                "projectApplication", projectApplicationMock,
                "applicantData", applicantDataMock
        );

        given(projectApplicationMock.getUser()).willReturn(applicantUserMock);
        given(userServiceMock.getManagerOfUser(applicantUserMock)).willReturn(managerUserMock);
        given(userServiceMock.getUserData(applicantUserMock)).willReturn(applicantDataMock);

        given(velocityMailTemplateServiceMock.getSubjectAndText(expectedTemplatePath, expectedContextMap))
            .willReturn(subjectTextPair);

        // when
        mailProjectApplicationEventHandler.onApplicationReceived(projectApplicationMock);

        // then
        verify(mailSenderServiceMock).queueMessage(expectedMessage);
    }

}