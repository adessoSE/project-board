package de.adesso.projectboard.adapter.mail.persistence;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Table;
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
public class SimpleMessage extends TemplateMessage {

    @Override
    public boolean isStillRelevant(LocalDateTime localDateTime) {
        return true;
    }

}
