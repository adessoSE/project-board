package de.adesso.projectboard.rest.handler.mail;

import de.adesso.projectboard.base.user.service.UserService;
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
 * to make method calls to {@link #sendMessages()} through the object proxy.
 *
 * @see MailService
 */
@Profile("mail")
@Service
public class MailSenderService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final MessageRepository messageRepository;

    private final JavaMailSenderImpl mailSender;

    private final UserService userService;

    @Autowired
    public MailSenderService(MessageRepository messageRepository,
                             JavaMailSenderImpl mailSender,
                             UserService userService) {
        this.messageRepository = messageRepository;
        this.mailSender = mailSender;
        this.userService = userService;
    }

    @Transactional
    public void sendMessages() {
        messageRepository.findAll().forEach(templateMessage -> {
            try {
                if(templateMessage.isStillRelevant()) {
                    var addressee = templateMessage.getAddressee();
                    var addresseeMail = userService.getUserData(addressee).getEmail();

                    // create a new mail message, set the subject, text, addressee and send it
                    var mailMessage = new SimpleMailMessage();
                    mailMessage.setSubject(templateMessage.getSubject());
                    mailMessage.setText(templateMessage.getText());
                    mailMessage.setTo("daniel.meier@adesso.de");

                    mailSender.send(mailMessage);

                    logger.info(String.format("Mail sent to %s!", addresseeMail));
                }

                // delete the message when it was sent
                // successfully or is not relevant anymore
                messageRepository.delete(templateMessage);
            } catch (Exception err) {
                logger.error("Error sending mail!", err);
            }
        });
    }

}
