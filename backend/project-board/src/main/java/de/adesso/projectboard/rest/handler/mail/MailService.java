package de.adesso.projectboard.rest.handler.mail;

import de.adesso.projectboard.rest.handler.mail.persistence.MessageRepository;
import de.adesso.projectboard.rest.handler.mail.persistence.TemplateMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;


@Profile("mail")
@Service
public class MailService {

    private final MessageRepository messageRepository;

    private final MailSenderService mailSenderService;

    @Autowired
    public MailService(MessageRepository messageRepository, MailSenderService mailSenderService) {
        this.messageRepository = messageRepository;
        this.mailSenderService = mailSenderService;
    }

    /**
     * Scheduled method to send pending messages.
     */
    @Scheduled(fixedDelay = 30000L)
    protected void sendMessage() {
        mailSenderService.sendMessages();
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
