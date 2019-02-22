package de.adesso.projectboard.rest.handler.mail.persistence;

import de.adesso.projectboard.base.application.persistence.ProjectApplication;
import de.adesso.projectboard.base.project.persistence.Project;
import de.adesso.projectboard.base.user.persistence.data.UserData;
import de.adesso.projectboard.rest.handler.application.ProjectBoardApplicationEventHandler;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * {@link TemplateMessage} sent to managers when a user applies for a {@link Project}.
 *
 * @see ProjectBoardApplicationEventHandler
 */
@Entity
@Table(name = "APPLICATION_TEMPLATE_MESSAGE")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
public class ApplicationTemplateMessage extends TemplateMessage {

    private static final String DISCLAIMER = "\n\n\nDiese Nachricht wurde automatisch generiert!";

    private static final String SUBJECT_PATTERN = "Neue Anfrage für das Projekt %s!";

    private static final String MAIN_TEXT_PATTERN = "%s hat eine Anfrage für das Projekt \"%s\" gestellt.";

    private static final String COMMENT_PATTERN = "\n\nZusätzlicher Kommentar vom Nutzer:\n\n\"%s\"\n\n";

    @ManyToOne
    @JoinColumn(
            name = "APPLICATION_ID",
            nullable = false
    )
    ProjectApplication application;

    public ApplicationTemplateMessage(ProjectApplication application, UserData addresseeData, UserData referencedUserData) {
        super(referencedUserData.getUser(), addresseeData.getUser());
        this.application = application;

        setSubject(String.format(SUBJECT_PATTERN, application.getProject().getId()));
        setText(buildText(referencedUserData));
    }

    private String buildText(UserData referencedUserData) {
        String mainText = String.format(MAIN_TEXT_PATTERN,
                referencedUserData.getFullName(),
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
