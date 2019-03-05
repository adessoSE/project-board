package de.adesso.projectboard.adapter.mail.handler;

import de.adesso.projectboard.adapter.mail.MailSenderService;
import de.adesso.projectboard.adapter.mail.VelocityMailTemplateService;
import de.adesso.projectboard.adapter.mail.persistence.TimeAwareMessage;
import de.adesso.projectboard.base.access.persistence.AccessInterval;
import de.adesso.projectboard.base.configuration.ProjectBoardConfigurationProperties;
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
import java.time.temporal.ChronoUnit;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MailUserAccessEventHandlerTest {

    private static final String PROJECT_BOARD_URL = "http://localhost:4200/";

    @Mock
    private MailSenderService mailSenderServiceMock;

    @Mock
    private UserService userServiceMock;

    @Mock
    private VelocityMailTemplateService velocityMailTemplateServiceMock;

    @Mock
    private ProjectBoardConfigurationProperties configurationPropertiesMock;

    @Mock
    private User userMock;

    @Mock
    private UserData userDataMock;

    @Mock
    private AccessInterval accessIntervalMock;

    private MailUserAccessEventHandler mailUserAccessEventHandler;

    @Before
    public void setUp() {
        given(configurationPropertiesMock.getUrl()).willReturn(PROJECT_BOARD_URL);

        this.mailUserAccessEventHandler = new MailUserAccessEventHandler(mailSenderServiceMock,
                userServiceMock, velocityMailTemplateServiceMock, configurationPropertiesMock);
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
                "newDateTime", "03.02.2019 10:00",
                "projectBoardUrl", PROJECT_BOARD_URL
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
    public void onAccessChangedQueuesExtendedMessageWhenNewEndAfterOldEnd() {
        // given
        var expectedTemplatePath = "/templates/mail/UserAccessExtended.vm";
        var expectedSubject = "Subject!";
        var expectedText = "Text!";
        var expectedEndTime = LocalDateTime.of(2019, 2, 3, 10, 0);
        var expectedOldEndTime = LocalDateTime.of(2019, 1, 1, 10, 0);
        var expectedContextMap = Map.of(
                "userData", userDataMock,
                "newDateTime", expectedEndTime.format(MailUserAccessEventHandler.DATE_TIME_FORMATTER),
                "projectBoardUrl", PROJECT_BOARD_URL
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
    public void onAccessChangedQueuesShortenedMessageWhenNewEndBeforeOldEnd() {
        // given
        var expectedTemplatePath = "/templates/mail/UserAccessShortened.vm";
        var expectedSubject = "Subject!";
        var expectedText = "Text!";
        var expectedEndTime = LocalDateTime.of(2019, 1, 3, 10, 0);
        var expectedOldEndTime = LocalDateTime.of(2019, 3, 1, 10, 0);
        var expectedContextMap = Map.of(
                "userData", userDataMock,
                "newDateTime", expectedEndTime.format(MailUserAccessEventHandler.DATE_TIME_FORMATTER),
                "projectBoardUrl", PROJECT_BOARD_URL
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
    public void onAccessChangedQueuesNoMessageWhenOldEndIsEqualToNewEnd() {
        // given
        var newAndOldEndTime = LocalDateTime.of(2019, 1, 3, 10, 0);

        given(accessIntervalMock.getEndTime()).willReturn(newAndOldEndTime);

        // when
        mailUserAccessEventHandler.onAccessChanged(userMock, accessIntervalMock, newAndOldEndTime);

        // then
        verifyZeroInteractions(mailSenderServiceMock);
    }

    @Test
    public void onAccessRevokedDoesNotQueueAMessage() {
        // given / when / then
        verifyZeroInteractions(mailSenderServiceMock);
    }

    @Test
    public void getContextMap() {
        // given
        var newDateTime = LocalDateTime.now();
        var formattedNewEndDateTime = newDateTime.format(MailUserAccessEventHandler.DATE_TIME_FORMATTER);
        var expectedContextMap = Map.of(
            "userData", userDataMock,
            "newDateTime", formattedNewEndDateTime,
            "projectBoardUrl", PROJECT_BOARD_URL
        );

        given(userServiceMock.getUserData(userMock)).willReturn(userDataMock);

        // when
        var actualContextMap = mailUserAccessEventHandler.getContextMap(userMock, newDateTime);

        // then
        assertThat(actualContextMap).isEqualTo(expectedContextMap);
    }

    @Test
    public void getTemplatePathAccessChangedReturnsPathForExtendedWhenNewAfterOld() {
        // given
        var expectedTemplatePath = "/templates/mail/UserAccessExtended.vm";

        var oldEndTime = LocalDateTime.of(2019, 2, 3, 10, 0);
        var newEndTime = oldEndTime.plus(1L, ChronoUnit.HOURS);

        // when
        var actualTemplatePath = mailUserAccessEventHandler.getTemplatePathAccessChanged(newEndTime, oldEndTime);

        // then
        assertThat(actualTemplatePath).isEqualTo(expectedTemplatePath);
    }

    @Test
    public void getTemplatePathAccessChangedReturnsPathForShortenedWhenOldAfterNew() {
        // given
        var expectedTemplatePath = "/templates/mail/UserAccessShortened.vm";

        var oldEndTime = LocalDateTime.of(2019, 2, 3, 10, 0);
        var newEndTime = oldEndTime.minus(1L, ChronoUnit.HOURS);

        // when
        var actualTemplatePath = mailUserAccessEventHandler.getTemplatePathAccessChanged(newEndTime, oldEndTime);

        // then
        assertThat(actualTemplatePath).isEqualTo(expectedTemplatePath);
    }

}
