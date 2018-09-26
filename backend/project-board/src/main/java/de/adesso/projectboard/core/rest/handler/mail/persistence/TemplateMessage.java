package de.adesso.projectboard.core.rest.handler.mail.persistence;

import de.adesso.projectboard.core.base.rest.user.persistence.User;
import de.adesso.projectboard.core.rest.handler.mail.MailService;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.mail.SimpleMailMessage;

import javax.persistence.*;

/**
 * Entity used to persist template mail message contents. The {@link MailService} persists them in
 * a database and tries to send a {@link SimpleMailMessage} with the content periodically.
 *
 *
 * @see MailService
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class TemplateMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(optional = false)
    private User referencedUser;

    @ManyToOne(optional = false)
    private User addressee;

    @Lob
    @Column(length = 512, nullable = false)
    private String text = "Template text.";

    @Column(nullable = false)
    private String subject = "Template subject.";

    /**
     * Constructs a new instance.
     *
     * <p>
     *     <b>Note:</b> The {@link #subject} and {@link #text} have
     *     to be set afterwards!
     * </p>
     *
     * @param referencedUser
     *          The user this message refers to.
     *
     * @param addressee
     *          The addressee of this message.
     *
     */
    protected TemplateMessage(User referencedUser, User addressee) {
        this.referencedUser = referencedUser;
        this.addressee = addressee;
    }

    /**
     *
     * @return
     *          {@code true}, when the message is still relevant at the
     *          current moment, {@code false} otherwise
     */
    public abstract boolean isStillRelevant();

}

