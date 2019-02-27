package de.adesso.projectboard.adapter.mail.handler;

import de.adesso.projectboard.adapter.mail.MailSenderService;
import de.adesso.projectboard.adapter.mail.VelocityMailTemplateService;
import de.adesso.projectboard.adapter.mail.persistence.TimeAwareMessage;
import de.adesso.projectboard.base.access.persistence.AccessInterval;
import de.adesso.projectboard.base.user.persistence.User;
import de.adesso.projectboard.base.user.persistence.data.UserData;
import de.adesso.projectboard.base.user.service.UserService;
import org.apache.velocity.util.Pair;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.util.Map;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class MailUserAccessEventHandlerTest {

    @Mock
    private MailSenderService mailSenderServiceMock;

    @Mock
    private UserService userServiceMock;

    @Mock
    private VelocityMailTemplateService velocityMailTemplateServiceMock;

    @Mock
    private User userMock;

    @Mock
    private UserData userDataMock;

    @Mock
    private AccessInterval accessIntervalMock;

    private MailUserAccessEventHandler mailUserAccessEventHandler;

    @Before
    public void setUp() {
        this.mailUserAccessEventHandler = new MailUserAccessEventHandler(mailSenderServiceMock,
                userServiceMock, velocityMailTemplateServiceMock);
    }

    @Test
    public void onAccessCreatedQueuesExpectedMessage() {
        // given
        var expectedTemplatePath = "/templates/mail/UserAccessCreated.vm";
        var expectedSubject = "Subject!";
        var expectedText = "Text!";
        var expectedEndTime = LocalDateTime.of(2019, 2, 3, 10, 0);
        var expectedContextMap = Map.of(
                "userData", userDataMock,
                "dateAndTime", "03.02.2019 10:00"
        );
        var expectedMessage = new TimeAwareMessage(userMock, userMock, expectedSubject, expectedText, expectedEndTime);
        var subjectTextPair = new Pair<>(expectedSubject, expectedText);

        given(accessIntervalMock.getEndTime()).willReturn(expectedEndTime);
        given(userServiceMock.getUserData(userMock)).willReturn(userDataMock);

        given(velocityMailTemplateServiceMock.getSubjectAndText(expectedTemplatePath, expectedContextMap))
                .willReturn(subjectTextPair);

        // when
        mailUserAccessEventHandler.onAccessCreated(userMock, accessIntervalMock);

        // then
        verify(mailSenderServiceMock).queueMessage(expectedMessage);
    }

    @Test
    public void onAccessChangedQueuesExpectedMessage() {
        // given
        var expectedTemplatePath = "/templates/mail/UserAccessChanged.vm";
        var expectedSubject = "Subject!";
        var expectedText = "Text!";
        var expectedEndTime = LocalDateTime.of(2019, 2, 3, 10, 0);
        var expectedOldEndTime = LocalDateTime.of(2019, 1, 1, 10, 0);
        var expectedContextMap = Map.of(
                "userData", userDataMock,
                "newDateAndTime", "03.02.2019 10:00",
                "oldDateAndTime", "01.01.2019 10:00"
        );
        var expectedMessage = new TimeAwareMessage(userMock, userMock, expectedSubject, expectedText, expectedEndTime);
        var subjectTextPair = new Pair<>(expectedSubject, expectedText);

        given(accessIntervalMock.getEndTime()).willReturn(expectedEndTime);
        given(userServiceMock.getUserData(userMock)).willReturn(userDataMock);

        given(velocityMailTemplateServiceMock.getSubjectAndText(expectedTemplatePath, expectedContextMap))
                .willReturn(subjectTextPair);

        // when
        mailUserAccessEventHandler.onAccessChanged(userMock, accessIntervalMock, expectedOldEndTime);

        // then
        verify(mailSenderServiceMock).queueMessage(expectedMessage);
    }

    @Test
    public void onAccessRevokedQueuesExpectedMessage() {
        // given
        var expectedTemplatePath = "/templates/mail/UserAccessRevoked.vm";
        var expectedSubject = "Subject!";
        var expectedText = "Text!";
        var expectedPreviousEndTime = LocalDateTime.of(2019, 2, 3, 10, 0);
        var expectedContextMap = Map.<String, Object>of(
                "userData", userDataMock
        );
        var expectedMessage = new TimeAwareMessage(userMock, userMock, expectedSubject, expectedText, expectedPreviousEndTime);
        var subjectTextPair = new Pair<>(expectedSubject, expectedText);

        given(userServiceMock.getUserData(userMock)).willReturn(userDataMock);

        given(velocityMailTemplateServiceMock.getSubjectAndText(expectedTemplatePath, expectedContextMap))
                .willReturn(subjectTextPair);

        // when
        mailUserAccessEventHandler.onAccessRevoked(userMock, expectedPreviousEndTime);

        // then
        verify(mailSenderServiceMock).queueMessage(expectedMessage);
    }

}