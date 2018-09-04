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
public class ProjectDatabaseUpdaterInfoPersistenceTest {

    @Autowired
    private ProjectDatabaseUpdaterInfoRepository infoRepository;

    @Test
    public void testSave_OK() {
        LocalDateTime time = LocalDateTime.of(2018, 1, 1, 12, 0);

        ProjectDatabaseUpdaterInfo info = new ProjectDatabaseUpdaterInfo();

        info.setStatus(ProjectDatabaseUpdaterInfo.Status.FAILURE);
        info.setTime(time);
        info.setFailureReason("Testreason");

        infoRepository.save(info);

        List<ProjectDatabaseUpdaterInfo> infos = StreamSupport.stream(infoRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());

        assertEquals(1, infos.size());

        ProjectDatabaseUpdaterInfo retrievedInfo = infos.get(0);

        assertEquals(ProjectDatabaseUpdaterInfo.Status.FAILURE, retrievedInfo.getStatus());
        assertEquals(time, retrievedInfo.getTime());
        assertEquals("Testreason", retrievedInfo.getFailureReason());
    }

    @Test
    public void testFindByStatus() {
        LocalDateTime firstTime = LocalDateTime.of(2018, 1, 1, 12, 0);
        ProjectDatabaseUpdaterInfo firstInfo = new ProjectDatabaseUpdaterInfo();
        firstInfo.setStatus(ProjectDatabaseUpdaterInfo.Status.SUCCESS);
        firstInfo.setTime(firstTime);

        LocalDateTime secondTime = LocalDateTime.of(2018, 2, 1, 12, 0);
        ProjectDatabaseUpdaterInfo secondInfo = new ProjectDatabaseUpdaterInfo();
        secondInfo.setStatus(ProjectDatabaseUpdaterInfo.Status.SUCCESS);
        secondInfo.setTime(secondTime);

        infoRepository.save(firstInfo);
        infoRepository.save(secondInfo);

        Optional<ProjectDatabaseUpdaterInfo> firstByStatusOptional
                = infoRepository.findFirstByStatusOrderByTimeDesc(ProjectDatabaseUpdaterInfo.Status.SUCCESS);

        assertTrue(firstByStatusOptional.isPresent());

        ProjectDatabaseUpdaterInfo firstByStatus = firstByStatusOptional.get();

        assertEquals(secondTime, firstByStatus.getTime());
    }

}