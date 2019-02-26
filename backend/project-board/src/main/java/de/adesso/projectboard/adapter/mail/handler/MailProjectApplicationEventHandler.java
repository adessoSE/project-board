package de.adesso.projectboard.adapter.mail.handler;

import de.adesso.projectboard.adapter.mail.MailSenderService;
import de.adesso.projectboard.base.application.handler.ProjectApplicationEventHandler;
import de.adesso.projectboard.base.application.persistence.ProjectApplication;
import de.adesso.projectboard.base.user.service.UserService;
import lombok.extern.slf4j.Slf4j;

/**
 * A {@link ProjectApplicationEventHandler} implementation that sends out a mail to the
 * supervisor of the applicant.
 */
@Slf4j
public class MailProjectApplicationEventHandler implements ProjectApplicationEventHandler {

    private final MailSenderService mailSenderService;

    private final UserService userService;

    public MailProjectApplicationEventHandler(MailSenderService mailSenderService, UserService userService) {
        this.mailSenderService = mailSenderService;
        this.userService = userService;
    }

    @Override
    public void onApplicationReceived(ProjectApplication application) {
        var applicant = application.getUser();
        var manager = userService.getManagerOfUser(applicant);
        var applicantData = userService.getUserData(applicant);
        var managerData = userService.getUserData(manager);

//        var message = new SimpleMessage(application, managerData, applicantData);
//        mailSenderService.queueMessage(message);
    }

}
