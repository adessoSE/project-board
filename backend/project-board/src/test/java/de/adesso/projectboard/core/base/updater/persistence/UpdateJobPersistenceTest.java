package de.adesso.projectboard.core.base.updater.persistence;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@DataJpaTest
public class UpdateJobPersistenceTest {

    @Autowired
    private ProjectDatabaseUpdaterInfoRepository infoRepository;

    @Test
    public void testSave_OK() {
        LocalDateTime time = LocalDateTime.of(2018, 1, 1, 12, 0);

        UpdateJob info = new UpdateJob();

        info.setStatus(UpdateJob.Status.FAILURE);
        info.setTime(time);
        info.setFailureReason("Testreason");

        infoRepository.save(info);

        List<UpdateJob> infos = StreamSupport.stream(infoRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());

        assertEquals(1, infos.size());

        UpdateJob retrievedInfo = infos.get(0);

        assertEquals(UpdateJob.Status.FAILURE, retrievedInfo.getStatus());
        assertEquals(time, retrievedInfo.getTime());
        assertEquals("Testreason", retrievedInfo.getFailureReason());
    }

    @Test
    public void testFindByStatus() {
        LocalDateTime firstTime = LocalDateTime.of(2018, 1, 1, 12, 0);
        UpdateJob firstInfo = new UpdateJob();
        firstInfo.setStatus(UpdateJob.Status.SUCCESS);
        firstInfo.setTime(firstTime);

        LocalDateTime secondTime = LocalDateTime.of(2018, 2, 1, 12, 0);
        UpdateJob secondInfo = new UpdateJob();
        secondInfo.setStatus(UpdateJob.Status.SUCCESS);
        secondInfo.setTime(secondTime);

        infoRepository.save(firstInfo);
        infoRepository.save(secondInfo);

        Optional<UpdateJob> firstByStatusOptional
                = infoRepository.findFirstByStatusOrderByTimeDesc(UpdateJob.Status.SUCCESS);

        assertTrue(firstByStatusOptional.isPresent());

        UpdateJob firstByStatus = firstByStatusOptional.get();

        assertEquals(secondTime, firstByStatus.getTime());
    }

}