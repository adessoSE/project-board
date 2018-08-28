package de.adesso.projectboard.core.rest;

import de.adesso.projectboard.core.base.project.persistence.AbstractProject;
import de.adesso.projectboard.core.base.project.persistence.ProjectRepository;
import de.adesso.projectboard.core.base.rest.application.ProjectApplication;
import de.adesso.projectboard.core.base.rest.application.ProjectApplicationHandler;
import de.adesso.projectboard.core.base.rest.application.persistence.ProjectApplicationLog;
import de.adesso.projectboard.core.base.rest.exceptions.ProjectNotFoundException;
import de.adesso.projectboard.core.mail.ApplicationTemplateMessage;
import de.adesso.projectboard.core.mail.MailService;
import de.adesso.projectboard.core.project.persistence.JiraProject;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Profile("adesso-jira")
@Service
public class JiraProjectApplicationHandler implements ProjectApplicationHandler {

    private final ProjectRepository projectRepository;

    private final MailService mailService;

    public JiraProjectApplicationHandler(ProjectRepository projectRepository, MailService mailService) {
        this.projectRepository = projectRepository;
        this.mailService = mailService;
    }

    @Override
    public ProjectApplicationLog onApplicationReceived(ProjectApplication application) {
        Optional<AbstractProject> optionalProject = projectRepository.findById(application.getProjectId());

        if(optionalProject.isPresent()) {
            JiraProject jiraProject = (JiraProject) optionalProject.get();

            KeycloakAuthenticationToken auth = (KeycloakAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
            KeycloakPrincipal principal = (KeycloakPrincipal) auth.getPrincipal();
            Map<String, Object> otherClaims = principal.getKeycloakSecurityContext().getToken().getOtherClaims();

            SimpleMailMessage message = new ApplicationTemplateMessage(jiraProject, application.getComment(), "Testuser");
            message.setTo("daniel.meier@adesso.de");

            //mailService.sendMessage(message);

            return new ProjectApplicationLog(principal.getName(), application);
        } else {
            throw new ProjectNotFoundException();
        }
    }

}
