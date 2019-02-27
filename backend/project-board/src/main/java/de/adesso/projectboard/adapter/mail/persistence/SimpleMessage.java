package de.adesso.projectboard.adapter.mail.persistence;

import de.adesso.projectboard.base.user.persistence.User;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * {@link TemplateMessage} whose {@link TemplateMessage#isStillRelevant(LocalDateTime)}
 * implementation always returns {@code true}
 */
@Entity
@Table(name = "SIMPLE_MESSAGE")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class SimpleMessage extends TemplateMessage {

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
     */
    public SimpleMessage(@NotNull User referencedUser, @NotNull User addressee, @NotNull String subject, @NotNull String text) {
        super(referencedUser, addressee, subject, text);
    }

    @Override
    public boolean isStillRelevant(LocalDateTime localDateTime) {
        return true;
    }

}
