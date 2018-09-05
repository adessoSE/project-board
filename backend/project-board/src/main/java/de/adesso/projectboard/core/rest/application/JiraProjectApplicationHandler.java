package de.adesso.projectboard.core.rest.application;

import de.adesso.projectboard.core.base.project.persistence.AbstractProject;
import de.adesso.projectboard.core.base.project.persistence.ProjectRepository;
import de.adesso.projectboard.core.base.rest.application.ProjectApplicationDTO;
import de.adesso.projectboard.core.base.rest.application.ProjectApplicationHandler;
import de.adesso.projectboard.core.base.rest.application.persistence.ProjectApplication;
import de.adesso.projectboard.core.base.rest.exceptions.ProjectNotFoundException;
import de.adesso.projectboard.core.mail.ApplicationTemplateMessage;
import de.adesso.projectboard.core.mail.MailService;
import de.adesso.projectboard.core.project.persistence.JiraProject;
import de.adesso.projectboard.core.rest.security.KeycloakAuthenticationInfo;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import java.util.Optional;

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

    private final ProjectRepository projectRepository;

    private final MailService mailService;

    private final KeycloakAuthenticationInfo authInfo;

    public JiraProjectApplicationHandler(ProjectRepository projectRepository, MailService mailService, KeycloakAuthenticationInfo authInfo) {
        this.projectRepository = projectRepository;
        this.mailService = mailService;
        this.authInfo = authInfo;
    }

    @Override
    public ProjectApplication onApplicationReceived(ProjectApplicationDTO applicationDTO) {
        Optional<AbstractProject> optionalProject = projectRepository.findById(applicationDTO.getProjectId());

        if(optionalProject.isPresent()) {
            JiraProject jiraProject = (JiraProject) optionalProject.get();

            SimpleMailMessage message = new ApplicationTemplateMessage(jiraProject, applicationDTO.getComment(), authInfo.getName());
            message.setTo(authInfo.getManagerEmail());
            message.setCc(authInfo.getEmail());

            mailService.sendMessage(message);

            return new ProjectApplication(applicationDTO.getComment(), optionalProject.get());
        } else {
            throw new ProjectNotFoundException();
        }
    }

}
