package de.adesso.projectboard.rest.handler.useraccess;

import de.adesso.projectboard.base.access.handler.UserAccessEventHandler;
import de.adesso.projectboard.base.access.persistence.AccessInterval;
import de.adesso.projectboard.base.user.persistence.User;
import de.adesso.projectboard.rest.handler.mail.MailService;
import de.adesso.projectboard.rest.handler.mail.persistence.AccessTemplateMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("mail")
@Component
public class ProjectBoardAccessEventHandler implements UserAccessEventHandler {

    private final MailService mailService;

    @Autowired
    public ProjectBoardAccessEventHandler(MailService mailService) {
        this.mailService = mailService;
    }

    @Override
    public void onAccessCreated(User user, AccessInterval accessInterval) {
        mailService.queueMessage(new AccessTemplateMessage(user, accessInterval.getEndTime()));
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
