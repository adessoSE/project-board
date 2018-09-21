package de.adesso.projectboard.core.rest.handler.mail.persistence;

import de.adesso.projectboard.core.base.rest.user.UserAccessController;
import de.adesso.projectboard.core.base.rest.user.persistence.User;
import org.springframework.mail.SimpleMailMessage;

import javax.persistence.Entity;

/**
 *
 *
 * @see UserAccessController
 */
@Entity
public class AccessTemplateMessage extends TemplateMessage {

    public AccessTemplateMessage(User user) {
        super(user, user);
    }

    protected AccessTemplateMessage() {
        // protected no-arg constructor for JPA
    }

    @Override
    public SimpleMailMessage getMailMessage() {
        SimpleMailMessage mailMessage = new SimpleMailMessage();

        mailMessage.setSubject("You have been granted access!");
        mailMessage.setText("GZ!");

        return mailMessage;
    }

}
