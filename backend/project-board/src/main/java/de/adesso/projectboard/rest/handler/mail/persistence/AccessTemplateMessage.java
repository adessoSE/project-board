package de.adesso.projectboard.rest.handler.mail.persistence;

import de.adesso.projectboard.base.access.persistence.AccessInfo;
import de.adesso.projectboard.base.access.rest.UserAccessController;
import de.adesso.projectboard.base.user.persistence.data.UserData;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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

        LocalDateTime accessEnd = userData.getUser().getLatestAccessInfo().getAccessEnd();
        String formattedString = accessEnd.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
        setText(String.format(TEXT_PLACEHOLDER, formattedString));
    }

    @Override
    public boolean isStillRelevant() {
        AccessInfo latestInfo = referencedUserData.getUser().getLatestAccessInfo();

        return latestInfo != null && latestInfo.isCurrentlyActive();
    }

}
