package de.adesso.projectboard.adapter.mail;

import de.adesso.projectboard.adapter.mail.persistence.MessageRepository;
import de.adesso.projectboard.adapter.mail.persistence.TemplateMessage;
import de.adesso.projectboard.base.user.persistence.User;
import de.adesso.projectboard.base.user.persistence.data.UserData;
import de.adesso.projectboard.base.user.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class MailSenderAdapterTest {

    @Mock
    private MessageRepository messageRepositoryMock;

    @Mock
    private JavaMailSenderImpl javaMailSenderMock;

    @Mock
    private UserService userServiceMock;

    @Mock
    private TemplateMessage templateMessageMock;

    @Mock
    private User userMock;

    @Mock
    private UserData userDataMock;

    private Clock clock;

    private MailSenderAdapter mailSenderAdapter;

    @Before
    public void setUp() {
        var instant = Instant.parse("2017-10-10T12:00:00.00Z");
        var zoneId = ZoneId.systemDefault();

        this.clock = Clock.fixed(instant, zoneId);
        this.mailSenderAdapter = new MailSenderAdapter(messageRepositoryMock, javaMailSenderMock,
                userServiceMock, clock);
    }

    @Test
    public void sendPendingMessagesOnlySendsRelevantMessages() {
        // given
        var expectedTo = "email@test.com";
        var expectedSubject = "This is a subject!";
        var expectedText = "This is a long text!";

        var expectedSimpleMessage = new SimpleMailMessage();
        expectedSimpleMessage.setTo(expectedTo);
        expectedSimpleMessage.setSubject(expectedSubject);
        expectedSimpleMessage.setText(expectedText);

        given(messageRepositoryMock.findAll()).willReturn(List.of(templateMessageMock, templateMessageMock));

        given(templateMessageMock.isStillRelevant(LocalDateTime.now(clock)))
                .willReturn(false, true);
        given(templateMessageMock.getSubject()).willReturn(expectedSubject);
        given(templateMessageMock.getText()).willReturn(expectedText);
        given(templateMessageMock.getAddressee()).willReturn(userMock);

        given(userServiceMock.getUserDataWithImage(userMock)).willReturn(userDataMock);
        given(userDataMock.getEmail()).willReturn(expectedTo);

        // when
        mailSenderAdapter.sendPendingMessages();

        // then
        verify(messageRepositoryMock, times(2)).delete(templateMessageMock);
        verify(javaMailSenderMock).send(expectedSimpleMessage);
    }

    @Test
    public void sendMessage() {
        // given
        var expectedTo = "mail@googlemail.com";
        var expectedSubject = "This is a subject :-)";
        var expectedText = "This is a cool text and very long too!";

        var expectedSimpleMessage = new SimpleMailMessage();
        expectedSimpleMessage.setTo(expectedTo);
        expectedSimpleMessage.setSubject(expectedSubject);
        expectedSimpleMessage.setText(expectedText);

        given(templateMessageMock.getSubject()).willReturn(expectedSubject);
        given(templateMessageMock.getText()).willReturn(expectedText);
        given(templateMessageMock.getAddressee()).willReturn(userMock);

        given(userServiceMock.getUserDataWithImage(userMock)).willReturn(userDataMock);
        given(userDataMock.getEmail()).willReturn(expectedTo);

        // when
        mailSenderAdapter.sendMessage(templateMessageMock);

        // then
        verify(javaMailSenderMock).send(expectedSimpleMessage);
    }

}
