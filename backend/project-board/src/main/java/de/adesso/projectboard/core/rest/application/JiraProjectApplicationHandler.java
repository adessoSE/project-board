package de.adesso.projectboard.core.rest.application;

import de.adesso.projectboard.core.base.project.persistence.AbstractProject;
import de.adesso.projectboard.core.base.project.persistence.ProjectRepository;
import de.adesso.projectboard.core.base.rest.application.ProjectApplication;
import de.adesso.projectboard.core.base.rest.application.ProjectApplicationHandler;
import de.adesso.projectboard.core.base.rest.application.persistence.ProjectApplicationLog;
import de.adesso.projectboard.core.base.rest.exceptions.ProjectNotFoundException;
import de.adesso.projectboard.core.mail.ApplicationTemplateMessage;
import de.adesso.projectboard.core.mail.MailService;
import de.adesso.projectboard.core.project.persistence.JiraProject;
import de.adesso.projectboard.core.security.KeycloakAuthorizationInfo;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * A {@link ProjectApplicationHandler} implementation that sends out a mail to the
 * supervisor of the applicant.
 *
 * @see MailService
 * @see ApplicationTemplateMessage
 * @see KeycloakAuthorizationInfo
 */
@Profile({"adesso-jira", "adesso-keycloak"})
@Service
public class JiraProjectApplicationHandler implements ProjectApplicationHandler {

    private final ProjectRepository projectRepository;

    private final MailService mailService;

    private final KeycloakAuthorizationInfo authInfo;

    public JiraProjectApplicationHandler(ProjectRepository projectRepository, MailService mailService, KeycloakAuthorizationInfo authInfo) {
        this.projectRepository = projectRepository;
        this.mailService = mailService;
        this.authInfo = authInfo;
    }

    @Override
    public ProjectApplicationLog onApplicationReceived(ProjectApplication application) {
        Optional<AbstractProject> optionalProject = projectRepository.findById(application.getProjectId());

        if(optionalProject.isPresent()) {
            JiraProject jiraProject = (JiraProject) optionalProject.get();

            SimpleMailMessage message = new ApplicationTemplateMessage(jiraProject, application.getComment(), authInfo.getName());
            message.setTo(authInfo.getManagerEmail());
            message.setCc(authInfo.getEmail());

            mailService.sendMessage(message);

            return new ProjectApplicationLog(authInfo.getUsername(), application);
        } else {
            throw new ProjectNotFoundException();
        }
    }

}
