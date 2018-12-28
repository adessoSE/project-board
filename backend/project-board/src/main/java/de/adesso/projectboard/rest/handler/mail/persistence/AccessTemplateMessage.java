package de.adesso.projectboard.rest.handler.mail.persistence;

import de.adesso.projectboard.base.access.rest.UserAccessController;
import de.adesso.projectboard.base.user.persistence.data.UserData;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;

/**
 * {@link TemplateMessage} sent to users when access was granted.
 *
 * @see UserAccessController
 */
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AccessTemplateMessage extends TemplateMessage {

    private static final String SUBJECT = "Du wurdest für die Projektbörse freigeschaltet!";

    private static final String TEXT_PLACEHOLDER = "Du hast nun bis %s Uhr Zugriff auf die Projektbörse! <Link zur Projektbörse>";

    public AccessTemplateMessage(UserData userData) {
        super(userData, userData);

        setSubject(SUBJECT);
    }

    @Override
    public boolean isStillRelevant() {
        return false;
    }

}
