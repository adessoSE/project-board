package de.adesso.projectboard.rest.handler.useraccess;

import de.adesso.projectboard.base.access.handler.UserAccessHandler;
import de.adesso.projectboard.base.user.persistence.User;
import de.adesso.projectboard.base.user.persistence.data.UserData;
import de.adesso.projectboard.base.user.service.UserService;
import de.adesso.projectboard.rest.handler.mail.MailService;
import de.adesso.projectboard.rest.handler.mail.persistence.AccessTemplateMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Profile("mail")
@Service
public class ProjectBoardAccessHandler implements UserAccessHandler {

    private final UserService userService;

    private final MailService mailService;

    @Autowired
    public ProjectBoardAccessHandler(UserService userService, MailService mailService) {
        this.userService = userService;
        this.mailService = mailService;
    }

    @Override
    public void onAccessGranted(User user) {
        UserData userData = userService.getUserData(user);

        mailService.queueMessage(new AccessTemplateMessage(userData));
    }

}
