package de.adesso.projectboard.core.rest.handler.mail;

import de.adesso.projectboard.core.rest.handler.mail.persistence.MessageRepository;
import de.adesso.projectboard.core.rest.handler.mail.persistence.MessageStatus;
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
        messageRepository.findAllByStatus(MessageStatus.PENDING).forEach(message -> {
            try {
                // set the addressee and send it
                SimpleMailMessage mailMessage = message.getMailMessage();
                mailMessage.setTo(message.getAddressee().getEmail());
                mailSender.send(mailMessage);

                // set the status to 'sent' when everything went well
                message.setStatus(MessageStatus.SENT);
                messageRepository.save(message);
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
