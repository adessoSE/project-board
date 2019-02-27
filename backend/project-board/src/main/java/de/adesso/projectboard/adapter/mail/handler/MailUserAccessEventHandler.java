package de.adesso.projectboard.adapter.mail.handler;

import de.adesso.projectboard.adapter.mail.MailSenderService;
import de.adesso.projectboard.adapter.mail.VelocityMailTemplateService;
import de.adesso.projectboard.adapter.mail.persistence.TimeAwareMessage;
import de.adesso.projectboard.base.access.handler.UserAccessEventHandler;
import de.adesso.projectboard.base.access.persistence.AccessInterval;
import de.adesso.projectboard.base.user.persistence.User;
import de.adesso.projectboard.base.user.service.UserService;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * {@link UserAccessEventHandler} implementation that sends out a mail to the
 * user who was affected by the user access event.
 */
@Slf4j
public class MailUserAccessEventHandler implements UserAccessEventHandler {

    static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    private final MailSenderService mailSenderService;

    private final UserService userService;

    private final VelocityMailTemplateService velocityMailTemplateService;

    public MailUserAccessEventHandler(MailSenderService mailSenderService,
                                      UserService userService,
                                      VelocityMailTemplateService velocityMailTemplateService) {
        this.mailSenderService = mailSenderService;
        this.userService = userService;
        this.velocityMailTemplateService = velocityMailTemplateService;
    }

    @Override
    public void onAccessCreated(User user, AccessInterval accessInterval) {
        var dateAndTime = accessInterval.getEndTime().format(DATE_TIME_FORMATTER);
        var userData = userService.getUserData(user);

        var contextMap = Map.of(
                "userData", userData,
                "dateAndTime", dateAndTime
        );
        var subjectTextPair =
                velocityMailTemplateService.getSubjectAndText("/templates/mail/UserAccessCreated.vm", contextMap);
        var message = new TimeAwareMessage(user, user, subjectTextPair.getFirst(),
                subjectTextPair.getSecond(), accessInterval.getEndTime());
        mailSenderService.queueMessage(message);
    }

    @Override
    public void onAccessChanged(User user, AccessInterval accessInterval, LocalDateTime previousEndTime) {
        var newDateAndTime = accessInterval.getEndTime().format(DATE_TIME_FORMATTER);
        var oldDateAndTime = previousEndTime.format(DATE_TIME_FORMATTER);
        var userData = userService.getUserData(user);

        var contextMap = Map.of(
                "userData", userData,
                "newDateAndTime", newDateAndTime,
                "oldDateAndTime", oldDateAndTime
        );
        var subjectTextPair =
                velocityMailTemplateService.getSubjectAndText("/templates/mail/UserAccessChanged.vm", contextMap);
        var message = new TimeAwareMessage(user, user, subjectTextPair.getFirst(),
                subjectTextPair.getSecond(), accessInterval.getEndTime());
        mailSenderService.queueMessage(message);
    }

    @Override
    public void onAccessRevoked(User user, LocalDateTime previousEndTime) {
        var userData = userService.getUserData(user);

        var contextMap = Map.<String, Object>of(
                "userData", userData
        );

        var subjectTextPair =
                velocityMailTemplateService.getSubjectAndText("/templates/mail/UserAccessRevoked.vm", contextMap);
        var message = new TimeAwareMessage(user, user, subjectTextPair.getFirst(),
                subjectTextPair.getSecond(), previousEndTime);
        mailSenderService.queueMessage(message);
    }

}
