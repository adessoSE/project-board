package de.adesso.projectboard.core.mail;

import de.adesso.projectboard.core.project.persistence.JiraProject;
import org.springframework.mail.SimpleMailMessage;

/**
 * A template message for a project application.
 */
public class ApplicationTemplateMessage extends SimpleMailMessage {

    private static final String DISCLAIMER = "\n\n\nThis is a automatically generated message, please do not reply!";

    private static final String TITLE_PATTERN = "New application for JIRA Project %s";

    private static final String MAIN_TEXT_PATTERN = "%s applied for the JIRA Project \"%s\".";

    private static final String COMMENT_PATTERN = "\n\nAdditional comment from user:\n\n\"%s\"\n\n";

    private final String applicantName;

    private final String comment;

    private final JiraProject project;

    /**
     * Constructs a new template message.
     *
     * @param project
     *          The {@link JiraProject} the application message refers to.
     *
     * @param comment
     *          A additional comment message.
     *
     * @param applicantName
     *          The name of the applicant.
     */
    public ApplicationTemplateMessage(JiraProject project, String comment, String applicantName) {
        this.project = project;
        this.comment = comment;
        this.applicantName = applicantName;

        setSubject(String.format(TITLE_PATTERN, project.getKey()));
        setText(buildText());
    }

    private String buildText() {
        StringBuilder builder = new StringBuilder();

        builder.append(String.format(MAIN_TEXT_PATTERN, applicantName, project.getTitle()));

        if(comment != null && !comment.isEmpty()) {
            builder.append(String.format(COMMENT_PATTERN, comment));
        }

        builder.append(DISCLAIMER);

        return builder.toString();
    }

}
