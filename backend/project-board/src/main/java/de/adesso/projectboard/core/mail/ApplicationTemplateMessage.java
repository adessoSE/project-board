package de.adesso.projectboard.core.mail;

import de.adesso.projectboard.core.project.persistence.JiraProject;
import org.springframework.mail.SimpleMailMessage;

/**
 * A template message for a project application.
 */
public class ApplicationTemplateMessage extends SimpleMailMessage {

    private final String DISCLAIMER = "This is a automatically generated message, please do not reply!";

    private final String TITLE_PATTERN = "New application for JIRA Project %s";

    /**
     * Constructs a new template message.
     *
     * @param project
     *          The {@link JiraProject} the application message refers to.
     *
     * @param comment
     *          A additional comment.
     */
    public ApplicationTemplateMessage(JiraProject project, String comment) {
        setSubject(String.format(TITLE_PATTERN, project.getKey()));
        setText(buildText());
    }

    private String buildText() {
        StringBuilder builder = new StringBuilder();

        builder.append(DISCLAIMER);

        return builder.toString();
    }

}
