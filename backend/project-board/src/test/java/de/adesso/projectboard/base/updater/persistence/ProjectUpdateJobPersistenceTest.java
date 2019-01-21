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
public class ProjectUpdateJobPersistenceTest {

    @Autowired
    ProjectUpdateJobRepository jobRepo;

    @Test
    public void testSave() {
        LocalDateTime time = LocalDateTime.of(2018, 1, 1, 13, 37);
        ProjectUpdateJob updateJob = new ProjectUpdateJob(time, ProjectUpdateJob.Status.FAILURE);
        updateJob.setFailureReason("Testreason");

        ProjectUpdateJob persistedjob = jobRepo.save(updateJob);

        assertEquals(ProjectUpdateJob.Status.FAILURE, persistedjob.getStatus());
        assertEquals(time, persistedjob.getTime());
        assertEquals("Testreason", persistedjob.getFailureReason());
    }

    @Test
    @Sql("classpath:de/adesso/projectboard/persistence/ProjectUpdateJobs.sql")
    public void testFindFirstByStatusOrderByTimeDesc() {
        Optional<ProjectUpdateJob> jobOptional
                = jobRepo.findFirstByStatusOrderByTimeDesc(ProjectUpdateJob.Status.SUCCESS);
        assertTrue(jobOptional.isPresent());

        ProjectUpdateJob job = jobOptional.get();

        for(ProjectUpdateJob otherJob : jobRepo.findAll()) {
            if(otherJob.getStatus().equals(job.getStatus())) {
                boolean after = job.getTime().isAfter(otherJob.getTime());
                boolean equal = job.getTime().isEqual(otherJob.getTime());

                assertTrue(after || equal);
            }
        }
    }

    @Test
    @Sql("classpath:de/adesso/projectboard/persistence/ProjectUpdateJobs.sql")
    public void testFindLatest() {
        Optional<ProjectUpdateJob> latestOptional = jobRepo.findLatest();
        assertTrue(latestOptional.isPresent());

        ProjectUpdateJob latest = latestOptional.get();
        assertEquals(LocalDateTime.of(2018, 1, 5, 13, 37), latest.getTime());
        assertEquals(ProjectUpdateJob.Status.SUCCESS, latest.getStatus());
    }

    @Test
    @Sql("classpath:de/adesso/projectboard/persistence/ProjectUpdateJobs.sql")
    public void testCountByStatus() {
        assertEquals(3L, jobRepo.countByStatus(ProjectUpdateJob.Status.FAILURE));
        assertEquals(2L, jobRepo.countByStatus(ProjectUpdateJob.Status.SUCCESS));
    }

}