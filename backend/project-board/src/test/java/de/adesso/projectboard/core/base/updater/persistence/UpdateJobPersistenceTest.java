package de.adesso.projectboard.core.base.updater.persistence;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@DataJpaTest
public class UpdateJobPersistenceTest {

    @Autowired
    private UpdateJobRepository jobRepo;

    @Test
    @Sql("classpath:de/adesso/projectboard/core/base/updater/persistence/UpdateJobs.sql")
    public void testSave_OK() {
        LocalDateTime time = LocalDateTime.of(2018, 1, 1, 13, 37);

        Optional<UpdateJob> jobOptional = jobRepo.findById(1L);
        assertTrue(jobOptional.isPresent());

        UpdateJob job = jobOptional.get();

        assertEquals(UpdateJob.Status.FAILURE, job.getStatus());
        assertEquals(time, job.getTime());
        assertEquals("Testreason", job.getFailureReason());
    }

    @Test
    @Sql("classpath:de/adesso/projectboard/core/base/updater/persistence/UpdateJobs.sql")
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

}