package de.adesso.projectboard.rest.handler.application;

import de.adesso.projectboard.base.application.handler.ProjectApplicationHandler;
import de.adesso.projectboard.base.application.persistence.ProjectApplication;
import de.adesso.projectboard.rest.handler.mail.MailService;
import de.adesso.projectboard.rest.handler.mail.persistence.ApplicationTemplateMessage;
import de.adesso.projectboard.rest.handler.mail.persistence.TemplateMessage;
import de.adesso.projectboard.rest.security.KeycloakAuthenticationInfo;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * A {@link ProjectApplicationHandler} implementation that sends out a mail to the
 * supervisor of the applicant.
 *
 * <p>
 *     Activated via the <i>mail</i> spring profile.
 * </p>
 *
 * @see MailService
 * @see ApplicationTemplateMessage
 * @see KeycloakAuthenticationInfo
 */
@Profile("mail")
@Service
public class ProjectBoardApplicationHandler implements ProjectApplicationHandler {

    private final MailService mailService;

    public ProjectBoardApplicationHandler(MailService mailService) {
        this.mailService = mailService;
    }

    @Override
    public void onApplicationReceived(ProjectApplication application) {
        TemplateMessage message = new ApplicationTemplateMessage(application);

        mailService.queueMessage(message);
    }

}
