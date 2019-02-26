package de.adesso.projectboard.adapter.mail.handler;

import de.adesso.projectboard.adapter.mail.MailSenderService;
import de.adesso.projectboard.adapter.mail.VelocityTemplateService;
import de.adesso.projectboard.base.access.handler.UserAccessEventHandler;
import de.adesso.projectboard.base.access.persistence.AccessInterval;
import de.adesso.projectboard.base.user.persistence.User;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * {@link UserAccessEventHandler} implementation that sends out a mail to the
 * user who was affected by the user access event.
 */
@Slf4j
public class MailUserAccessEventHandler implements UserAccessEventHandler {

    private final MailSenderService mailSenderService;

    private final VelocityTemplateService velocityTemplateService;

    public MailUserAccessEventHandler(MailSenderService mailSenderService, VelocityTemplateService velocityTemplateService) {
        this.mailSenderService = mailSenderService;
        this.velocityTemplateService = velocityTemplateService;
    }

    @Override
    public void onAccessCreated(User user, AccessInterval accessInterval) {
        var messageText = velocityTemplateService.mergeTemplate("templates/UserAccessCreatedTemplate.vm", Map.of("name", "Ein cooler Name!"));
        System.out.println("test");
        // mailSenderService.queueMessage(new UserAccessEventMessage(user, accessInterval.getEndTime()));
    }

    @Override
    public void onAccessChanged(User user, AccessInterval accessInterval) {
        // implement
    }

    @Override
    public void onAccessRevoked(User user) {
        // implement
    }

}
