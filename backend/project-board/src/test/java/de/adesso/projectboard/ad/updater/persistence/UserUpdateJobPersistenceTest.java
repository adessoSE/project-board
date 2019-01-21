package de.adesso.projectboard.ad.updater.persistence;

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
public class UserUpdateJobPersistenceTest {

    @Autowired
    private UserUpdateJobRepository userUpdateJobRepo;

    @Test
    public void save() {
        // given
        var updateTime = LocalDateTime.now();

        var job = new UserUpdateJob(updateTime, true);

        // when
        var savedJob = userUpdateJobRepo.save(job);
        var retrievedJob = userUpdateJobRepo.findById(savedJob.id).orElseThrow();
        job.id = retrievedJob.id;

        // then
        assertThat(retrievedJob).isEqualTo(job);
    }

    @Test
    @Sql("classpath:de/adesso/projectboard/persistence/UserUpdateJobs.sql")
    public void findFirstBySuccessTrueOrderByUpdateTimeDescReturnsExpectedJob() {
        // given
        var expectedJob = userUpdateJobRepo.findById(2L).orElseThrow();

        // when
        var actualJob = userUpdateJobRepo.findFirstBySuccessTrueOrderByUpdateTimeDesc().orElseThrow();

        // then
        assertThat(actualJob).isEqualTo(expectedJob);
    }

}