package de.adesso.projectboard.adapter.mail.handler;

import de.adesso.projectboard.adapter.mail.MailSenderService;
import de.adesso.projectboard.adapter.mail.VelocityMailTemplateService;
import de.adesso.projectboard.adapter.mail.persistence.SimpleMessage;
import de.adesso.projectboard.base.application.handler.ProjectApplicationEventHandler;
import de.adesso.projectboard.base.application.persistence.ProjectApplication;
import de.adesso.projectboard.base.user.service.UserService;

import java.util.Map;

/**
 * A {@link ProjectApplicationEventHandler} implementation that sends out a mail to the
 * supervisor of the applicant.
 */
public class MailProjectApplicationEventHandler implements ProjectApplicationEventHandler {

    private final MailSenderService mailSenderService;

    private final UserService userService;

    private final VelocityMailTemplateService velocityMailTemplateService;

    public MailProjectApplicationEventHandler(MailSenderService mailSenderService, UserService userService, VelocityMailTemplateService velocityMailTemplateService) {
        this.mailSenderService = mailSenderService;
        this.userService = userService;
        this.velocityMailTemplateService = velocityMailTemplateService;
    }

    @Override
    public void onApplicationReceived(ProjectApplication application) {
        var applicant = application.getUser();
        var manager = userService.getManagerOfUser(applicant);
        var applicantData = userService.getUserData(applicant);

        var contextMap = Map.of(
            "projectApplication", application,
            "applicantData", applicantData
        );
        var subjectTextPair = velocityMailTemplateService.getSubjectAndText("/templates/mail/UserAppliedForProject.vm", contextMap);

        var simpleMessage = new SimpleMessage(applicant, manager, subjectTextPair.getFirst(), subjectTextPair.getSecond());
        mailSenderService.queueMessage(simpleMessage);
    }

}
