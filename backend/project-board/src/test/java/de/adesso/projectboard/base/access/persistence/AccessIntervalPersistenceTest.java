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
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
@TestPropertySource("classpath:application-persistence-test.properties")
public class AccessIntervalPersistenceTest {

    @Autowired
    private AccessIntervalRepository intervalRepo;

    @Autowired
    private UserRepository userRepo;

    @Test
    @Sql("classpath:de/adesso/projectboard/persistence/Users.sql")
    public void save() {
        // given
        User user = userRepo.findById("User2").orElseThrow(EntityNotFoundException::new);
        LocalDateTime startTime = LocalDateTime.of(2018, 1, 1, 13, 37);
        LocalDateTime endTime = LocalDateTime.of(2018, 1, 2, 13, 37);

        AccessInterval accessInterval = new AccessInterval(user, startTime, endTime);

        // when
        AccessInterval savedInterval = intervalRepo.save(accessInterval);
        AccessInterval retrievedInterval = intervalRepo.findById(savedInterval.getId()).orElseThrow(EntityNotFoundException::new);

        // then
        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(retrievedInterval.getStartTime()).isEqualTo(startTime);
        softly.assertThat(retrievedInterval.getEndTime()).isEqualTo(endTime);
        softly.assertThat(retrievedInterval.getUser().getId()).isEqualTo("User2");

        softly.assertAll();
    }

    @Test
    @SqlGroup({
            @Sql("classpath:de/adesso/projectboard/persistence/Users.sql"),
            @Sql("classpath:de/adesso/projectboard/persistence/AccessIntervals.sql")
    })
    public void findAllLatestIntervals() {
        // given
        var expectedResult = intervalRepo.findAllById(List.of(1L, 3L));

        // when
        var actualResult = intervalRepo.findAllLatestIntervals();

        // then
        assertThat(actualResult).isEqualTo(expectedResult);
    }
}
