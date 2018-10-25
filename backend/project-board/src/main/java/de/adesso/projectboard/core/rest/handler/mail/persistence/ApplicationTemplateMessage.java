package de.adesso.projectboard.core.rest.handler.mail.persistence;

import de.adesso.projectboard.core.base.rest.project.persistence.Project;
import de.adesso.projectboard.core.base.rest.user.application.persistence.ProjectApplication;
import de.adesso.projectboard.core.rest.handler.application.ProjectBoardApplicationHandler;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * {@link TemplateMessage} sent to managers when a user applies for a {@link Project}.
 *
 * @see ProjectBoardApplicationHandler
 */
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
public class ApplicationTemplateMessage extends TemplateMessage {

    private static final String DISCLAIMER = "\n\n\nDiese Nachricht wurde automatisch generiert!";

    private static final String SUBJECT_PATTERN = "Neue Anfrage für Projekt %s!";

    private static final String MAIN_TEXT_PATTERN = "%s hat eine Anfrage für das Projekt \"%s\" gestellt.";

    private static final String COMMENT_PATTERN = "\n\nZusätzlicher Kommentar vom Nutzer:\n\n\"%s\"\n\n";

    @ManyToOne
    private ProjectApplication application;

    public ApplicationTemplateMessage(ProjectApplication application) {
        super(application.getUser(), application.getUser().getBoss());
        this.application = application;

        setSubject(String.format(SUBJECT_PATTERN, application.getProject().getId()));
        setText(buildText());
    }

    private String buildText() {
        String mainText = String.format(MAIN_TEXT_PATTERN,
                application.getUser().getFullName(),
                application.getProject().getTitle());

        if(application.getComment() != null && !application.getComment().isEmpty()) {
            return mainText + String.format(COMMENT_PATTERN, application.getComment());
        } else {
            return mainText;
        }
    }

    @Override
    public boolean isStillRelevant() {
        return true;
    }

}
