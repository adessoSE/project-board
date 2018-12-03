package de.adesso.projectboard.base.access.persistence;

import de.adesso.projectboard.base.user.persistence.User;
import de.adesso.projectboard.base.user.persistence.UserRepository;
import org.assertj.core.api.SoftAssertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;

@RunWith(SpringRunner.class)
@DataJpaTest
@TestPropertySource("classpath:application-persistence-test.properties")
public class AccessInfoPersistenceTest {

    @Autowired
    AccessInfoRepository infoRepo;

    @Autowired
    UserRepository userRepo;

    @Test
    @Sql("classpath:de/adesso/projectboard/persistence/Users.sql")
    public void save() {
        // given
        User user = userRepo.findById("User2").orElseThrow(EntityNotFoundException::new);
        LocalDateTime startTime = LocalDateTime.of(2018, 1, 1, 13, 37);
        LocalDateTime endTime = LocalDateTime.of(2018, 1, 2, 13, 37);

        AccessInfo accessInfo = new AccessInfo(user, startTime, endTime);

        // when
        AccessInfo savedInfo = infoRepo.save(accessInfo);
        AccessInfo retrievedInfo = infoRepo.findById(savedInfo.getId()).orElseThrow(EntityNotFoundException::new);

        // then
        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(retrievedInfo.getAccessStart()).isEqualTo(startTime);
        softly.assertThat(retrievedInfo.getAccessEnd()).isEqualTo(endTime);
        softly.assertThat(retrievedInfo.getUser().getId()).isEqualTo("User2");

        softly.assertAll();
    }

}