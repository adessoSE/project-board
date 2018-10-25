package de.adesso.projectboard.core.rest.handler.mail.persistence;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
public class ApplicationTemplateMessagePersistenceTest {

    @Autowired
    private MessageRepository messageRepository;

    @Test
    @Sql({
            "classpath:de/adesso/projectboard/core/base/persistence/Users.sql",
            "classpath:de/adesso/projectboard/core/base/persistence/Projects.sql",
            "classpath:de/adesso/projectboard/core/base/persistence/Applications.sql",
            "classpath:de/adesso/projectboard/core/base/persistence/ApplicationTemplateMessages.sql"
    })
    public void testSave() {
        Optional<TemplateMessage> messageOptional = messageRepository.findById(2L);
        assertTrue(messageOptional.isPresent());

        ApplicationTemplateMessage message = (ApplicationTemplateMessage) messageOptional.get();

        assertEquals("Subject", message.getSubject());
        assertEquals("Text", message.getText());
        assertNotNull(message.getReferencedUser());
        assertEquals("User1", message.getReferencedUser().getId());
        assertNotNull(message.getAddressee());
        assertEquals("SuperUser2", message.getAddressee().getId());
        assertNotNull(message.getApplication());
        assertEquals(1L, message.getApplication().getId());
    }

}