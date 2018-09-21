package de.adesso.projectboard.core.rest.handler.mail.persistence;

import de.adesso.projectboard.core.base.rest.user.persistence.User;
import de.adesso.projectboard.core.rest.handler.mail.MailService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.mail.SimpleMailMessage;

import javax.persistence.*;

/**
 * Entity used to persist mail messages. The {@link MailService} persists them in
 * a database and tries to send them periodically.
 *
 * @see MailService
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Getter
@Setter
public abstract class TemplateMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private User referencedUser;

    @ManyToOne
    private User addressee;

    private MessageStatus status;

    /**
     * Constructs a new instance. The {@link #status} is set to
     * {@link MessageStatus#PENDING}.
     *
     * @param referencedUser
     *          The user this message refers to.
     *
     * @param addressee
     *          The addressee of this message.
     */
    public TemplateMessage(User referencedUser, User addressee) {
        this.referencedUser = referencedUser;
        this.addressee = addressee;

        this.status = MessageStatus.PENDING;
    }

    protected TemplateMessage() {
        // protected no-arg constructor for JPA
    }

    /**
     *
     * @return
     *          The {@link SimpleMailMessage} send to the
     *          addressee.
     */
    public abstract SimpleMailMessage getMailMessage();

}

