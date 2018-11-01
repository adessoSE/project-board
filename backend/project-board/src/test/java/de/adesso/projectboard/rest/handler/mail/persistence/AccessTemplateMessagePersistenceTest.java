package de.adesso.projectboard.rest.handler.mail.persistence;

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
public class AccessTemplateMessagePersistenceTest {

    @Autowired
    private MessageRepository messageRepo;

    @Test
    @Sql({
            "classpath:de/adesso/projectboard/core/base/persistence/Users.sql",
            "classpath:de/adesso/projectboard/core/base/persistence/AccessTemplateMessages.sql"
    })
    public void testSave() {
        Optional<TemplateMessage> messageOptional = messageRepo.findById(1L);
        assertTrue(messageOptional.isPresent());

        AccessTemplateMessage message = (AccessTemplateMessage) messageOptional.get();

        assertEquals("Subject", message.getSubject());
        assertEquals("Text", message.getText());
        assertNotNull(message.getReferencedUser());
        assertEquals("User1", message.getReferencedUser().getId());
        assertNotNull(message.getAddressee());
        assertEquals("SuperUser2", message.getAddressee().getId());
    }

}