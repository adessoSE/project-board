package de.adesso.projectboard.base.scheduled.persistence;

import org.assertj.core.api.SoftAssertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
@TestPropertySource("classpath:application-persistence-test.properties")
public class ScheduledJobLogPersistenceTest {

    @Autowired
    private ScheduledJobLogRepository jobLogRepo;

    @Test
    public void save() {
        // given
        var time = LocalDateTime.now();
        var jobIdentifier = "JOB";
        var expectedJobLog = new ScheduledJobLog(time, jobIdentifier, ScheduledJobLog.Status.SUCCESS);

        // when
        var savedJobLog = jobLogRepo.save(expectedJobLog);
        var retrievedJobLog = jobLogRepo.findById(savedJobLog.getId()).orElseThrow();
        expectedJobLog.id = retrievedJobLog.id;

        // then
        var softly = new SoftAssertions();

        softly.assertThat(retrievedJobLog.getId()).isNotNull();
        softly.assertThat(retrievedJobLog).isEqualTo(expectedJobLog);

        softly.assertAll();
    }

    @Test
    @Sql("classpath:de/adesso/projectboard/persistence/ScheduledJobLogs.sql")
    public void findFirstByJobIdentifierAndStatusOrderByTimeDescReturnsLatestLogWhenPresent() {
        // given
        var jobIdentifier = "FIRST-JOB";
        var expectedJobLog = jobLogRepo.findById(3L).orElseThrow();

        // when
        var actualJobLog =
                jobLogRepo.findFirstByJobIdentifierAndStatusOrderByTimeDesc(jobIdentifier, ScheduledJobLog.Status.SUCCESS);

        // then
        assertThat(actualJobLog).contains(expectedJobLog);
    }

    @Test
    @Sql("classpath:de/adesso/projectboard/persistence/ScheduledJobLogs.sql")
    public void findFirstByJobIdentifierAndStatusOrderByTimeDescReturnsEmptyOptionalWhenNonePresent() {
        // given
        var jobIdentifier = "SECOND-JOB";

        // when
        var actualJobLog =
                jobLogRepo.findFirstByJobIdentifierAndStatusOrderByTimeDesc(jobIdentifier, ScheduledJobLog.Status.FAILURE);

        // then
        assertThat(actualJobLog).isEmpty();
    }

}
