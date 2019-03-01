package de.adesso.projectboard.adapter.mail.persistence;

import de.adesso.projectboard.base.user.persistence.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@TestPropertySource("classpath:application-persistence-test.properties")
@DataJpaTest
public class SimpleMessagePersistenceTest {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    @Sql("classpath:de/adesso/projectboard/persistence/Users.sql")
    public void save() {
        // given
        var addressee = userRepository.findById("User1").get();
        var referencedUser = userRepository.findById("User2").get();
        var subject = "Subject";
        var text = "Text";

        var expectedMessage = new SimpleMessage(referencedUser, addressee, subject, text);

        // when
        var savedMessage = messageRepository.save(expectedMessage);
        var retrievedMessage = messageRepository.findById(savedMessage.getId()).get();
        expectedMessage.id = retrievedMessage.id;

        // then
        assertThat(retrievedMessage).isEqualTo(expectedMessage);
    }

}