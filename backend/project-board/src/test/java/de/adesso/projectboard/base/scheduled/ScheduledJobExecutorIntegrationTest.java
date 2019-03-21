package de.adesso.projectboard.base.scheduled;

import de.adesso.projectboard.base.scheduled.configuration.ScheduledJobExecutorIntegrationTestConfiguration;
import de.adesso.projectboard.base.scheduled.persistence.ScheduledJobLogRepository;
import helper.base.scheduled.AnnotatedJob;
import helper.base.scheduled.NonAnnotatedJob;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ContextConfiguration(classes = ScheduledJobExecutorIntegrationTestConfiguration.class)
@RunWith(SpringRunner.class)
public class ScheduledJobExecutorIntegrationTest {

    @MockBean
    private ScheduledJobLogRepository jobLogRepoMock;

    @Autowired
    private ScheduledJobExecutor scheduledJobExecutor;

    @Autowired
    private NonAnnotatedJob nonAnnotatedJob;

    @Autowired
    private AnnotatedJob annotatedJob;

    @Test
    public void scheduledJobExecutorAddsExpectedBeans() {
        // given / when / then
        assertThat(scheduledJobExecutor.scheduledJobIdMap).containsAllEntriesOf(Map.of(
                nonAnnotatedJob, nonAnnotatedJob.getJobIdentifier(),
                annotatedJob, annotatedJob.getJobIdentifier()
        ));
    }

}
