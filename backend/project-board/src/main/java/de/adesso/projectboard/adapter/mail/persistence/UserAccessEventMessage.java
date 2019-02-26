package de.adesso.projectboard.adapter.mail.persistence;

import de.adesso.projectboard.base.access.rest.UserAccessController;
import de.adesso.projectboard.base.user.persistence.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * {@link TemplateMessage} sent to users when access was granted.
 *
 * @see UserAccessController
 */
@Entity
@Table(name = "USER_ACCESS_EVENT_MESSAGE")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserAccessEventMessage extends TemplateMessage {

    @Column(
            name = "ACCESS_END",
            nullable = false
    )
    LocalDateTime accessEnd;

    public UserAccessEventMessage(@NotNull User user, @NotNull String subject, @NotNull String text, @NotNull LocalDateTime accessEnd) {
        super(user, user, subject, text);

        this.accessEnd = accessEnd;
    }

    @Override
    public boolean isStillRelevant(LocalDateTime localDateTime) {
        return accessEnd.isBefore(localDateTime) || accessEnd.isAfter(localDateTime);
    }

}
