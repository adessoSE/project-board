package de.adesso.projectboard.adapter.mail.handler;

import de.adesso.projectboard.adapter.mail.MailSenderAdapter;
import de.adesso.projectboard.adapter.mail.configuration.MailConfigurationProperties;
import de.adesso.projectboard.adapter.mail.persistence.SimpleMessage;
import de.adesso.projectboard.adapter.velocity.VelocityTemplateService;
import de.adesso.projectboard.base.application.handler.ProjectApplicationReceivedEventHandler;
import de.adesso.projectboard.base.application.persistence.ProjectApplication;
import de.adesso.projectboard.base.user.service.UserService;

import java.util.Map;

/**
 * A {@link ProjectApplicationReceivedEventHandler} implementation that sends out a mail to the
 * supervisor of the applicant.
 */
public class MailProjectApplicationReceivedEventHandler implements ProjectApplicationReceivedEventHandler {

    private final MailSenderAdapter mailSenderAdapter;

    private final UserService userService;

    private final VelocityTemplateService velocityMailTemplateService;

    private final String referralBaseUrl;

    public MailProjectApplicationReceivedEventHandler(MailSenderAdapter mailSenderAdapter,
                                                      UserService userService,
                                                      VelocityTemplateService velocityMailTemplateService,
                                                      MailConfigurationProperties mailConfigProperties) {
        this.mailSenderAdapter = mailSenderAdapter;
        this.userService = userService;
        this.velocityMailTemplateService = velocityMailTemplateService;

        this.referralBaseUrl = mailConfigProperties.getReferralBaseUrl();
    }

    @Override
    public void onApplicationReceived(ProjectApplication application) {
        var applicant = application.getUser();
        var manager = userService.getManagerOfUser(applicant);
        var applicantData = userService.getUserDataWithImage(applicant);
        var managerData = userService.getUserDataWithImage(manager);
        var issueLink = referralBaseUrl + application.getProject().getId();

        var contextMap = Map.of(
            "projectApplication", application,
            "applicantData", applicantData,
                "managerData", managerData,
                "issueLink", issueLink
        );
        var subjectTextPair = velocityMailTemplateService.getSubjectAndText("/templates/mail/UserAppliedForProject.vm", contextMap);

        var simpleMessage = new SimpleMessage(applicant, manager, subjectTextPair.getFirst(), subjectTextPair.getSecond());
        mailSenderAdapter.queueMessage(simpleMessage);
    }

}
