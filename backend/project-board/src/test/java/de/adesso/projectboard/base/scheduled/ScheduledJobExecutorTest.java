package de.adesso.projectboard.base.scheduled;

import de.adesso.projectboard.base.scheduled.persistence.ScheduledJobLog;
import de.adesso.projectboard.base.scheduled.persistence.ScheduledJobLogRepository;
import helper.base.scheduled.AutoRegisteredJob;
import helper.base.scheduled.NonAnnotatedJob;
import helper.base.scheduled.NonAutoRegisteredJob;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.ApplicationContext;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ScheduledJobExecutorTest {

    @Captor
    private ArgumentCaptor<ScheduledJobLog> jobLogArgumentCaptor;

    @Mock
    private ScheduledJobLogRepository jobLogRepoMock;

    @Mock
    private ApplicationContext applicationContextMock;

    @Mock
    private ScheduledJob jobMock;

    @Mock
    private ScheduledJob otherJobMock;

    private Clock clock;

    private ScheduledJobExecutor scheduledJobExecutor;

    @Before
    public void setUp() {
        var instant = Instant.parse("2019-03-14T13:00:00.00Z");
        var zoneId = ZoneId.systemDefault();
        this.clock = Clock.fixed(instant, zoneId);

        this.scheduledJobExecutor = new ScheduledJobExecutor(jobLogRepoMock, clock);
    }

    @Test
    public void executeJobsExecutesAllJobsWhichShouldBeExecutedAndPersistsLogs() throws Exception {
        // given
        var jobId = "FIRST";
        var otherJobId = "SECOND";
        var currentTime = LocalDateTime.now(clock);
        var lastExecuteTime = currentTime.minus(10L, ChronoUnit.MINUTES);
        var expectedJobLog = new ScheduledJobLog(currentTime, otherJobId, ScheduledJobLog.Status.SUCCESS);
        var persistedJobLog = new ScheduledJobLog(lastExecuteTime, "Don't care", ScheduledJobLog.Status.SUCCESS);

        scheduledJobExecutor.scheduledJobIdMap.put(jobMock, jobId);
        scheduledJobExecutor.scheduledJobIdMap.put(otherJobMock, otherJobId);

        given(jobLogRepoMock.findFirstByJobIdentifierAndStatusOrderByTimeDesc(jobId, ScheduledJobLog.Status.SUCCESS))
                .willReturn(Optional.of(persistedJobLog));
        given(jobLogRepoMock.findFirstByJobIdentifierAndStatusOrderByTimeDesc(otherJobId, ScheduledJobLog.Status.SUCCESS))
                .willReturn(Optional.of(persistedJobLog));

        given(jobMock.shouldExecute(lastExecuteTime)).willReturn(false);
        given(otherJobMock.shouldExecute(lastExecuteTime)).willReturn(true);

        // when
        scheduledJobExecutor.executeJobs();

        // then
        verify(otherJobMock).execute(lastExecuteTime);
        verify(jobLogRepoMock).save(jobLogArgumentCaptor.capture());

        var actualJobLog = jobLogArgumentCaptor.getValue();
        assertThat(actualJobLog).isEqualTo(expectedJobLog);
    }

    @Test
    public void executeJobsExecutesJobWhenNoSuccessfulLogPresentForJob() throws Exception {
        // given
        var jobId = "JOB";
        var currentTime = LocalDateTime.now(clock);
        var expectedJobLog = new ScheduledJobLog(currentTime, jobId, ScheduledJobLog.Status.SUCCESS);

        scheduledJobExecutor.scheduledJobIdMap.put(jobMock, jobId);

        given(jobLogRepoMock.findFirstByJobIdentifierAndStatusOrderByTimeDesc(jobId, ScheduledJobLog.Status.SUCCESS))
                .willReturn(Optional.empty());

        // when
        scheduledJobExecutor.executeJobs();

        // then
        verify(jobMock).execute();
        verify(jobLogRepoMock).save(jobLogArgumentCaptor.capture());

        var actualJobLog = jobLogArgumentCaptor.getValue();
        assertThat(actualJobLog).isEqualTo(expectedJobLog);
    }

    @Test
    public void executeJobLogsSuccessWhenJobDoesNotThrowException() {
        // given
        var jobId = "JOB";
        var currentTime = LocalDateTime.now(clock);
        var expectedJobLog = new ScheduledJobLog(currentTime, jobId, ScheduledJobLog.Status.SUCCESS);

        Callable<Void> callable = () -> {
            jobMock.execute();
            return null;
        };

        // when
        scheduledJobExecutor.execute(callable, jobId);

        // then
        verify(jobLogRepoMock).save(jobLogArgumentCaptor.capture());

        var actualJobLog = jobLogArgumentCaptor.getValue();
        assertThat(actualJobLog).isEqualTo(expectedJobLog);
    }

    @Test
    public void executeJobLogsFailureWhenJobThrowsException() throws Exception {
        // given
        var jobId = "JOB";
        var currentTime = LocalDateTime.now(clock);
        var expectedJobLog = new ScheduledJobLog(currentTime, jobId, ScheduledJobLog.Status.FAILURE);

        willThrow(new Exception()).given(jobMock).execute();

        Callable<Void> callable = () -> {
            jobMock.execute();
            return null;
        };

        // when
        scheduledJobExecutor.execute(callable, jobId);

        // then
        verify(jobLogRepoMock).save(jobLogArgumentCaptor.capture());

        var actualJobLog = jobLogArgumentCaptor.getValue();
        assertThat(actualJobLog).isEqualTo(expectedJobLog);
    }

    @Test
    public void registerScheduledJobThrowsExceptionWhenIdentifierIsNull() {
        // given / when / then
        assertThatThrownBy(() -> scheduledJobExecutor.registerScheduledJob(jobMock))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("A job's identifier can't be null!");
    }

    @Test
    public void registerScheduledJobThrowExceptionWhenIdentifierContainsOnlyWhitespace() {
        // given
        var jobIdOnlyWhitespace = "   ";

        given(jobMock.getJobIdentifier()).willReturn(jobIdOnlyWhitespace);

        // when / then
        assertThatThrownBy(() -> scheduledJobExecutor.registerScheduledJob(jobMock))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("A job's identifier can't contain only whitespace!");
    }

    @Test
    public void registerScheduledJobThrowsExceptionWhenDifferentJobWithSameIdRegistered() {
        // given
        var alreadyExistingJobId = "JOB";

        scheduledJobExecutor.scheduledJobIdMap.put(jobMock, alreadyExistingJobId);

        given(otherJobMock.getJobIdentifier()).willReturn(alreadyExistingJobId);

        // when / then
        assertThatThrownBy(() -> scheduledJobExecutor.registerScheduledJob(otherJobMock))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(String.format("Multiple scheduled jobs with the same job identifier '%s' found!", alreadyExistingJobId));
    }

    @Test
    public void registerScheduledJobDoesNotRegisterSameJobTwice() {
        // given
        var jobId = "JOB";

        scheduledJobExecutor.scheduledJobIdMap.put(jobMock, jobId);

        // when
        scheduledJobExecutor.registerScheduledJob(jobMock);

        // then
        assertThat(scheduledJobExecutor.scheduledJobIdMap).containsExactly(Map.entry(jobMock, jobId));
    }

    @Test
    public void registerScheduledJobRegistersJobWhenJobHasUniqueId() {
        // given
        var existingJobId = "JOB";
        var otherJobId = "OTHER-JOB";

        scheduledJobExecutor.scheduledJobIdMap.put(jobMock, existingJobId);

        given(otherJobMock.getJobIdentifier()).willReturn(otherJobId);

        // when
        scheduledJobExecutor.registerScheduledJob(otherJobMock);

        // then
        assertThat(scheduledJobExecutor.scheduledJobIdMap)
                .containsOnly(Map.entry(jobMock, existingJobId), Map.entry(otherJobMock, otherJobId));
    }

    @Test
    public void setApplicationContextRegistersAllScheduledJobBeansWithAutoRegistration() {
        // given
        var jobId = "JOB-MOCK";
        var nonAutoRegisteredJob = new NonAutoRegisteredJob();
        var jobMap =  Map.of(
                "bean1", nonAutoRegisteredJob,
                "bean2", jobMock
        );

        given(jobMock.getJobIdentifier()).willReturn(jobId);
        given(applicationContextMock.getBeansOfType(ScheduledJob.class)).willReturn(jobMap);

        // when
        scheduledJobExecutor.setApplicationContext(applicationContextMock);

        // then
        assertThat(scheduledJobExecutor.scheduledJobIdMap).containsExactly(Map.entry(jobMock, jobId));
    }

    @Test
    public void shouldAutoRegisterReturnsTrueWhenClassNotAnnotated() {
        // given
        var job = new NonAnnotatedJob();

        // when
        var actualShouldAutoRegister = scheduledJobExecutor.shouldAutoRegister(job);

        // then
        assertThat(actualShouldAutoRegister).isTrue();
    }

    @Test
    public void shouldAutoRegisterReturnsTrueWhenAnnotatedAndValueIsTrue() {
        // given
        var job = new AutoRegisteredJob();

        // when
        var actualShouldAutoRegister = scheduledJobExecutor.shouldAutoRegister(job);

        // then
        assertThat(actualShouldAutoRegister).isTrue();
    }

    @Test
    public void shouldAutoRegisterReturnsFalseWhenAnnotatedAndValueIsFalse() {
        // given
        var job = new NonAutoRegisteredJob();

        // when
        var actualShouldAutoRegister = scheduledJobExecutor.shouldAutoRegister(job);

        // then
        assertThat(actualShouldAutoRegister).isFalse();
    }

}
