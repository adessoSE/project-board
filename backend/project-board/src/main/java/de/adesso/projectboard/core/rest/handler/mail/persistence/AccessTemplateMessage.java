package de.adesso.projectboard.core.rest.handler.mail.persistence;

import de.adesso.projectboard.core.base.rest.user.UserAccessController;
import de.adesso.projectboard.core.base.rest.user.persistence.User;
import org.hibernate.Hibernate;
import org.springframework.mail.SimpleMailMessage;

import javax.persistence.Entity;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 *
 *
 * @see UserAccessController
 */
@Entity
public class AccessTemplateMessage extends TemplateMessage {

    private static final String SUBJECT = "Du wurdest für die Projektbörse freigeschaltet!";

    private static final String TEXT_PLACEHOLDER = "Du hast nun bis %s Uhr Zugriff zur Projektbörse! <Link einfügen>";

    public AccessTemplateMessage(User user) {
        super(user, user);
    }

    protected AccessTemplateMessage() {
        // protected no-arg constructor for JPA
    }

    @Override
    public SimpleMailMessage getMailMessage() {
        SimpleMailMessage mailMessage = new SimpleMailMessage();

        mailMessage.setSubject(SUBJECT);

        // TODO: LazyInitializationException
//        if(getReferencedUser().hasAccess()) {
//            LocalDateTime accessEnd = getReferencedUser().getAccessObject().getAccessEnd();
//
//            String formattedDate = accessEnd.format(DateTimeFormatter.ofPattern("d.M H:m"));
//
//            mailMessage.setText(String.format(TEXT_PLACEHOLDER, formattedDate));
//        }

        mailMessage.setText(String.format(TEXT_PLACEHOLDER, "<Placeholder>"));

        return mailMessage;
    }

}
