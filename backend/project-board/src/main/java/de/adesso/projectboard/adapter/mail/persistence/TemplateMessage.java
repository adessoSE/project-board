package de.adesso.projectboard.adapter.mail.persistence;

import de.adesso.projectboard.base.user.persistence.User;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

@Entity
@Table(name = "TEMPLATE_MESSAGE")
@Inheritance(strategy = InheritanceType.JOINED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@EqualsAndHashCode
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
    @Column(length = 4600)
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
     * @see #TemplateMessage(User, User)
     */
    protected TemplateMessage(User referencedUser, User addressee, String subject, String text) {
        this(referencedUser, addressee);

        this.text = text;
        this.subject = subject;
    }

    /**
     *
     * @param localDateTime
     *          The date and time to check, not null.
     *
     * @return
     *          {@code true}, iff the message is still relevant at the given
     *          {@code localDateTime} and needs to be sent.
     */
    public abstract boolean isStillRelevant(LocalDateTime localDateTime);

}

