package de.adesso.projectboard.rest.handler.mail.persistence;

import de.adesso.projectboard.base.user.persistence.data.UserData;
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
    protected Long id;

    @ManyToOne
    @JoinColumn(
            name = "REF_USER_DATA_ID",
            nullable = false
    )
    protected UserData referencedUserData;

    @ManyToOne
    @JoinColumn(
            name = "ADDRESSEE_USER_DATA_ID",
            nullable = false
    )
    protected UserData addresseeData;

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
     * @param referencedUserData
     *          The {@link UserData} of the user this message refers to.
     *
     * @param addresseeData
     *          The {@link UserData} of the addressee of this message.
     */
    protected TemplateMessage(UserData referencedUserData, UserData addresseeData) {
        this.referencedUserData = referencedUserData;
        this.addresseeData = addresseeData;
    }

    /**
     *
     * @return
     *          {@code true}, iff the message is still relevant.
     */
    public abstract boolean isStillRelevant();

}

