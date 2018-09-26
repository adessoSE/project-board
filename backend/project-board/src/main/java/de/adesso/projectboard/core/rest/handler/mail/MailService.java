package de.adesso.projectboard.core.rest.handler.mail;

import de.adesso.projectboard.core.rest.handler.mail.persistence.MessageRepository;
import de.adesso.projectboard.core.rest.handler.mail.persistence.TemplateMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;


@Profile("mail")
@Service
public class MailService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final JavaMailSenderImpl mailSender;

    private final MessageRepository messageRepository;

    @Autowired
    public MailService(JavaMailSenderImpl mailSender, MessageRepository messageRepository) {
        this.mailSender = mailSender;
        this.messageRepository = messageRepository;
    }

    /**
     * Scheduled method to send pending messages.
     */
    @Scheduled(fixedDelay = 30000L)
    protected void sendMessages() {
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

    /**
     *
     * @param message
     *          The message to queue for sending.
     *
     * @return
     *          The persisted message entity.
     */
    public TemplateMessage queueMessage(TemplateMessage message) {
        return messageRepository.save(message);
    }

}
