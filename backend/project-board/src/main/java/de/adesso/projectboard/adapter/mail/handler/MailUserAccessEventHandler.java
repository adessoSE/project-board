package de.adesso.projectboard.adapter.mail.handler;

import de.adesso.projectboard.adapter.mail.MailSenderAdapter;
import de.adesso.projectboard.adapter.mail.VelocityMailTemplateService;
import de.adesso.projectboard.adapter.mail.persistence.TimeAwareMessage;
import de.adesso.projectboard.base.access.handler.UserAccessEventHandler;
import de.adesso.projectboard.base.access.persistence.AccessInterval;
import de.adesso.projectboard.base.configuration.ProjectBoardConfigurationProperties;
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

    static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private final MailSenderAdapter mailSenderAdapter;

    private final UserService userService;

    private final VelocityMailTemplateService velocityMailTemplateService;

    private final String projectBoardUrl;

    public MailUserAccessEventHandler(MailSenderAdapter mailSenderAdapter,
                                      UserService userService,
                                      VelocityMailTemplateService velocityMailTemplateService,
                                      ProjectBoardConfigurationProperties configurationProperties) {
        this.mailSenderAdapter = mailSenderAdapter;
        this.userService = userService;
        this.velocityMailTemplateService = velocityMailTemplateService;

        this.projectBoardUrl = configurationProperties.getUrl();
    }

    @Override
    public void onAccessCreated(User user, AccessInterval accessInterval) {
        var contextMap = getContextMap(user, accessInterval.getEndTime());
        var subjectTextPair =
                velocityMailTemplateService.getSubjectAndText("/templates/mail/UserAccessCreated.vm", contextMap);
        var message = new TimeAwareMessage(user, user, subjectTextPair.getFirst(),
                subjectTextPair.getSecond(), accessInterval.getEndTime());
        mailSenderAdapter.queueMessage(message);
    }

    @Override
    public void onAccessChanged(User user, AccessInterval accessInterval, LocalDateTime previousEndTime) {
        if(!accessInterval.getEndTime().equals(previousEndTime)) {
            var contextMap = getContextMap(user, accessInterval.getEndTime());
            var templatePath = getTemplatePathAccessChanged(accessInterval.getEndTime(), previousEndTime);

            var subjectTextPair = velocityMailTemplateService.getSubjectAndText(templatePath, contextMap);
            var message = new TimeAwareMessage(user, user, subjectTextPair.getFirst(),
                    subjectTextPair.getSecond(), accessInterval.getEndTime());
            mailSenderAdapter.queueMessage(message);
        }
    }

    @Override
    public void onAccessRevoked(User user, LocalDateTime previousEndTime) {
        // do not send a message at this point of time
        log.info(String.format("Access for user %s revoked!", user.getId()));
    }

    Map<String, Object> getContextMap(User user, LocalDateTime newEndTime) {
        var newDateAndTime = newEndTime.format(DATE_TIME_FORMATTER);
        var userData = userService.getUserData(user);

        return Map.of(
                "userData", userData,
                "newDate", newDateAndTime,
                "projectBoardUrl", projectBoardUrl
        );
    }

    String getTemplatePathAccessChanged(LocalDateTime newEndTime, LocalDateTime previousEndTime) {
        if(previousEndTime.isAfter(newEndTime)) {
            return "/templates/mail/UserAccessShortened.vm";
        } else {
            return "/templates/mail/UserAccessExtended.vm";
        }
    }

}
