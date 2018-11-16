package de.adesso.projectboard.base.access.persistence;

import de.adesso.projectboard.base.user.persistence.User;
import de.adesso.projectboard.base.user.persistence.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@DataJpaTest
public class AccessInfoPersistenceTest {

    @Autowired
    AccessInfoRepository infoRepo;

    @Autowired
    UserRepository userRepo;

    @Test
    @Sql("classpath:de/adesso/projectboard/persistence/Users.sql")
    public void testSave() {
        User user = userRepo.findById("User2").orElseThrow(EntityNotFoundException::new);
        LocalDateTime startTime = LocalDateTime.of(2018, 1, 1, 13, 37);
        LocalDateTime endTime = LocalDateTime.of(2018, 1, 2, 13, 37);

        AccessInfo accessInfo = new AccessInfo(user, startTime, endTime);
        AccessInfo savedInfo = infoRepo.save(accessInfo);
        AccessInfo retrievedInfo = infoRepo.findById(savedInfo.getId()).orElseThrow(EntityNotFoundException::new);

        assertEquals(startTime, retrievedInfo.getAccessStart());
        assertEquals(endTime, retrievedInfo.getAccessEnd());
        assertNotNull(retrievedInfo.getUser());
        assertEquals("User2", retrievedInfo.getUser().getId());
    }

}