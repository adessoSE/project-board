package de.adesso.projectboard.rest.handler.useraccess;

import de.adesso.projectboard.base.access.handler.UserAccessHandler;
import de.adesso.projectboard.base.user.persistence.User;
import de.adesso.projectboard.rest.handler.mail.MailService;
import de.adesso.projectboard.rest.handler.mail.persistence.AccessTemplateMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Profile("mail")
@Service
public class ProjectBoardAccessHandler implements UserAccessHandler {

    private final MailService mailService;

    @Autowired
    public ProjectBoardAccessHandler(MailService mailService) {
        this.mailService = mailService;
    }

    @Override
    public void onAccessGranted(User user) {
        mailService.queueMessage(new AccessTemplateMessage(user));
    }

}
