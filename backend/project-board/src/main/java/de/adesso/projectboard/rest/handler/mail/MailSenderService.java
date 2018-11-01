package de.adesso.projectboard.rest.handler.mail;

import de.adesso.projectboard.rest.handler.mail.persistence.MessageRepository;
import de.adesso.projectboard.rest.handler.mail.persistence.TemplateMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service to send {@link SimpleMailMessage}s generated from persisted {@link TemplateMessage}s.
 * <br>
 * Used by {@link MailService} in its scheduled {@link MailService#sendMessage()} method
 * to make method calls to {@link #sendMessages()} go through the object proxy.
 *
 * @see MailService
 */
@Profile("mail")
@Service
public class MailSenderService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final MessageRepository messageRepository;

    private final JavaMailSenderImpl mailSender;

    @Autowired
    public MailSenderService(MessageRepository messageRepository, JavaMailSenderImpl mailSender) {
        this.messageRepository = messageRepository;
        this.mailSender = mailSender;
    }

    @Transactional
    public void sendMessages() {
        messageRepository.findAll().forEach(message -> {
            try {
                if(message.isStillRelevant()) {
                    // create a new mail message, set the subject, text, addressee and send it
                    SimpleMailMessage mailMessage = new SimpleMailMessage();
                    mailMessage.setSubject(message.getSubject());
                    mailMessage.setText(message.getText());
                    mailMessage.setTo(message.getAddressee().getEmail());

                    mailSender.send(mailMessage);

                    logger.debug(String.format("Mail sent to %s!", message.getAddressee().getEmail()));
                }

                // delete the message when it was sent
                // successfully or is not relevant anymore
                messageRepository.delete(message);
            } catch (Exception err) {
                logger.error("Error sending mail!", err);
            }
        });
    }

}
