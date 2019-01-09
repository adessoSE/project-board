package de.adesso.projectboard.base.updater.persistence;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@DataJpaTest
@TestPropertySource("classpath:application-persistence-test.properties")
public class UpdateJobPersistenceTest {

    @Autowired
    UpdateJobRepository jobRepo;

    @Test
    public void testSave() {
        LocalDateTime time = LocalDateTime.of(2018, 1, 1, 13, 37);
        UpdateJob updateJob = new UpdateJob(time, UpdateJob.Status.FAILURE);
        updateJob.setFailureReason("Testreason");

        UpdateJob persistedjob = jobRepo.save(updateJob);

        assertEquals(UpdateJob.Status.FAILURE, persistedjob.getStatus());
        assertEquals(time, persistedjob.getTime());
        assertEquals("Testreason", persistedjob.getFailureReason());
    }

    @Test
    @Sql("classpath:de/adesso/projectboard/persistence/UpdateJobs.sql")
    public void testFindFirstByStatusOrderByTimeDesc() {
        Optional<UpdateJob> jobOptional
                = jobRepo.findFirstByStatusOrderByTimeDesc(UpdateJob.Status.SUCCESS);
        assertTrue(jobOptional.isPresent());

        UpdateJob job = jobOptional.get();

        for(UpdateJob otherJob : jobRepo.findAll()) {
            if(otherJob.getStatus().equals(job.getStatus())) {
                boolean after = job.getTime().isAfter(otherJob.getTime());
                boolean equal = job.getTime().isEqual(otherJob.getTime());

                assertTrue(after || equal);
            }
        }
    }

    @Test
    @Sql("classpath:de/adesso/projectboard/persistence/UpdateJobs.sql")
    public void testFindLatest() {
        Optional<UpdateJob> latestOptional = jobRepo.findLatest();
        assertTrue(latestOptional.isPresent());

        UpdateJob latest = latestOptional.get();
        assertEquals(LocalDateTime.of(2018, 1, 5, 13, 37), latest.getTime());
        assertEquals(UpdateJob.Status.SUCCESS, latest.getStatus());
    }

    @Test
    @Sql("classpath:de/adesso/projectboard/persistence/UpdateJobs.sql")
    public void testCountByStatus() {
        assertEquals(3L, jobRepo.countByStatus(UpdateJob.Status.FAILURE));
        assertEquals(2L, jobRepo.countByStatus(UpdateJob.Status.SUCCESS));
    }

}