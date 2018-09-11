package de.adesso.projectboard.core.rest.application;

import de.adesso.projectboard.core.base.rest.user.application.ProjectApplicationHandler;
import de.adesso.projectboard.core.base.rest.user.application.persistence.ProjectApplication;
import de.adesso.projectboard.core.mail.ApplicationTemplateMessage;
import de.adesso.projectboard.core.mail.MailService;
import de.adesso.projectboard.core.project.persistence.JiraProject;
import de.adesso.projectboard.core.rest.security.KeycloakAuthenticationInfo;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

/**
 * A {@link ProjectApplicationHandler} implementation that sends out a mail to the
 * supervisor of the applicant.
 *
 * <p>
 *     Activated via the <i>adesso-jira</i> and <i>adesso-keycloak</i>
 *     profiles.
 * </p>
 *
 * @see MailService
 * @see ApplicationTemplateMessage
 * @see KeycloakAuthenticationInfo
 */
@Profile({"adesso-jira", "adesso-keycloak"})
@Service
public class JiraProjectApplicationHandler implements ProjectApplicationHandler {

    private final MailService mailService;

    private final KeycloakAuthenticationInfo authInfo;

    public JiraProjectApplicationHandler(MailService mailService, KeycloakAuthenticationInfo authInfo) {
        this.mailService = mailService;
        this.authInfo = authInfo;
    }

    @Override
    public void onApplicationReceived(ProjectApplication application) {
        JiraProject jiraProject = (JiraProject) application.getProject();

        SimpleMailMessage message = new ApplicationTemplateMessage(jiraProject, application.getComment(), authInfo.getName());
        message.setTo(authInfo.getManagerEmail());
        message.setCc(authInfo.getEmail());

        mailService.sendMessage(message);
    }

}
