package de.adesso.projectboard.adapter.mail.persistence;

import de.adesso.projectboard.base.access.rest.UserAccessController;
import de.adesso.projectboard.base.user.persistence.User;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * {@link TemplateMessage} whose {@link TemplateMessage#isStillRelevant(LocalDateTime)} implementation
 * depends on the {@link #getRelevancyDateTime() relevancy datetime} of the message.
 *
 * @see UserAccessController
 */
@Entity
@Table(name = "TIME_AWARE_MESSAGE")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class TimeAwareMessage extends TemplateMessage {

    @Column(
            name = "RELEVANCY_DATE_TIME",
            nullable = false
    )
    LocalDateTime relevancyDateTime;

    /**
     * Constructs a new instance.
     *
     * @param referencedUser
     *          The user this message refers to, not null.
     *
     * @param addressee
     *          The user this message is sent to, not null.
     *
     * @param subject
     *          The subject of the message, neither null nor empty.
     *
     * @param text
     *          The message text, neither null or empty.
     *
     * @param relevancyDateTime
     *          The time until which the message is relevant, not null
     *
     */
    public TimeAwareMessage(@NotNull User referencedUser, @NotNull User addressee, @NotNull String subject,
                            @NotNull String text, @NotNull LocalDateTime relevancyDateTime) {
        super(referencedUser, addressee, subject, text);

        this.relevancyDateTime = relevancyDateTime;
    }

    @Override
    public boolean isStillRelevant(LocalDateTime localDateTime) {
        return relevancyDateTime.isAfter(localDateTime) || relevancyDateTime.isEqual(localDateTime);
    }

}
