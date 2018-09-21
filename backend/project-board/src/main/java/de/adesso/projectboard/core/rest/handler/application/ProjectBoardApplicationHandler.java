package de.adesso.projectboard.core.rest.handler.application;

import de.adesso.projectboard.core.base.rest.user.application.ProjectApplicationHandler;
import de.adesso.projectboard.core.base.rest.user.application.persistence.ProjectApplication;
import de.adesso.projectboard.core.rest.handler.mail.persistence.ApplicationTemplateMessage;
import de.adesso.projectboard.core.rest.handler.mail.MailService;
import de.adesso.projectboard.core.rest.handler.mail.persistence.TemplateMessage;
import de.adesso.projectboard.core.rest.security.KeycloakAuthenticationInfo;
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
