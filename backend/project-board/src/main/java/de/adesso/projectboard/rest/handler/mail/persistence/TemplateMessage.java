package de.adesso.projectboard.rest.handler.mail.persistence;

import de.adesso.projectboard.base.user.persistence.User;
import de.adesso.projectboard.rest.handler.mail.MailService;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.mail.SimpleMailMessage;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;

/**
 * Entity used to persist template mail message contents. The {@link MailService} persists them in
 * a database and tries to send a {@link SimpleMailMessage} with the content periodically.
 *
 * @see MailService
 */
@Entity
@Table(name = "TEMPLATE_MESSAGE")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class TemplateMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @ManyToOne
    @JoinColumn(
            name = "REF_USER_ID",
            nullable = false
    )
    protected User referencedUser;

    @ManyToOne
    @JoinColumn(
            name = "ADDRESSEE_USER_ID",
            nullable = false
    )
    protected User addressee;

    @Lob
    @Column(length = 512)
    @NotEmpty
    protected String text;

    @Column
    @NotEmpty
    protected String subject;

    /**
     * Constructs a new instance.
     *
     * <p>
     *     <b>Note:</b> The {@link #subject} and {@link #text} have
     *     to be set afterwards!
     * </p>
     *
     * @param referencedUser
     *          The user this message refers to, not null.
     *
     * @param addressee
     *          The user this message is sent to, not null.
     */
    protected TemplateMessage(User referencedUser, User addressee) {
        this.referencedUser = referencedUser;
        this.addressee = addressee;
    }

    /**
     *
     * @return
     *          {@code true}, iff the message is still relevant and
     *          will needs to be sent.
     */
    public abstract boolean isStillRelevant();

}

