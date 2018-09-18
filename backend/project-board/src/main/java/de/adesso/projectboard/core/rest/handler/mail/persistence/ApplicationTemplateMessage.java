package de.adesso.projectboard.core.rest.handler.mail.persistence;

import de.adesso.projectboard.core.base.rest.user.application.persistence.ProjectApplication;
import de.adesso.projectboard.core.base.rest.user.persistence.User;
import de.adesso.projectboard.core.project.persistence.JiraProject;
import de.adesso.projectboard.core.rest.handler.application.ProjectBoardApplicationHandler;
import org.springframework.mail.SimpleMailMessage;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 *
 *
 * @see ProjectBoardApplicationHandler
 */
@Entity
public class ApplicationTemplateMessage extends TemplateMessage {

    private static final String DISCLAIMER = "\n\n\nThis is a automatically generated message, please do not reply!";

    private static final String TITLE_PATTERN = "New application for JIRA Project %s";

    private static final String MAIN_TEXT_PATTERN = "%s applied for the JIRA Project \"%s\".";

    private static final String COMMENT_PATTERN = "\n\nAdditional comment from user:\n\n\"%s\"\n\n";

    @ManyToOne
    private ProjectApplication application;

    public ApplicationTemplateMessage(ProjectApplication application) {
        super(application.getUser(), application.getUser().getBoss());

        this.application = application;
    }

    protected ApplicationTemplateMessage() {
        // protected no-arg constructor for JPA
    }

    private String buildText() {
        return String.format("New Application by %s!", getReferencedUser().getFullName());
    }

    @Override
    public SimpleMailMessage getMailMessage() {
        SimpleMailMessage mailMessage = new SimpleMailMessage();

        mailMessage.setSubject(String.format(TITLE_PATTERN, "Projekt!"));
        mailMessage.setText(buildText());

        return mailMessage;
    }
}
