package de.adesso.projectboard.rest.handler.mail.persistence;

import de.adesso.projectboard.base.access.rest.UserAccessController;
import de.adesso.projectboard.base.user.persistence.User;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * {@link TemplateMessage} sent to users when access was granted.
 *
 * @see UserAccessController
 */
@Entity
@Table(name = "ACCESS_TEMPLATE_MESSAGE")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AccessTemplateMessage extends TemplateMessage {

    private static final String SUBJECT = "Du wurdest für das Project Board freigeschaltet!";

    private static final String TEXT_PLACEHOLDER = "Du hast nun bis zum %s Uhr Zugriff auf das Project Board!";

    private static final String DATE_FORMAT = "dd.MM.yyyy HH:mm";

    public AccessTemplateMessage(User user, LocalDateTime localDateTime) {
        super(user, user);

        setSubject(SUBJECT);
        setText(String.format(TEXT_PLACEHOLDER, localDateTime.format(DateTimeFormatter.ofPattern(DATE_FORMAT))));
    }

    @Override
    public boolean isStillRelevant() {
        return true;
    }

}
