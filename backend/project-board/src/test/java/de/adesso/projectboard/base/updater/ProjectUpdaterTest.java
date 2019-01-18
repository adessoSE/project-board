package de.adesso.projectboard.base.updater;

import de.adesso.projectboard.base.configuration.ProjectBoardConfigurationProperties;
import de.adesso.projectboard.base.project.persistence.Project;
import de.adesso.projectboard.base.project.service.ProjectService;
import de.adesso.projectboard.base.reader.ProjectReader;
import de.adesso.projectboard.base.updater.persistence.UpdateJob;
import de.adesso.projectboard.base.updater.persistence.UpdateJobRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ProjectUpdaterTest {

    private final long REFRESH_INTERVAL = 10;

    private final String INSTANT_STRING = "2018-01-01T13:00:00.00Z";

    @Captor
    private ArgumentCaptor<UpdateJob> updateJobArgumentCaptor;

    @Mock
    private ProjectService projectServiceMock;

    @Mock
    private UpdateJobRepository updateJobRepoMock;

    @Mock
    private ProjectReader projectReaderMock;

    @Mock
    private Project projectMock;

    @Mock
    private UpdateJob updateJobMock;

    @Mock
    private ProjectBoardConfigurationProperties propertiesMock;

    private ProjectUpdater projectUpdater;

    private Clock clock;

    @Before
    public void setUp() {
        given(propertiesMock.getRefreshInterval()).willReturn(REFRESH_INTERVAL);

        Instant instant = Instant.parse(INSTANT_STRING);
        ZoneId zoneId = ZoneId.systemDefault();

        this.clock = Clock.fixed(instant, zoneId);

        this.projectUpdater
                = new ProjectUpdater(projectServiceMock, updateJobRepoMock, projectReaderMock, propertiesMock, clock);
    }

    @Test
    public void refreshProjectDatabaseGetsInitialProjectsOnFirstUpdate() throws Exception {
        // given
        UpdateJob expectedUpdateJob = new UpdateJob(LocalDateTime.now(clock), UpdateJob.Status.SUCCESS);
        given(updateJobRepoMock.findFirstByStatusOrderByTimeDesc(UpdateJob.Status.SUCCESS)).willReturn(Optional.empty());

        given(projectReaderMock.getInitialProjects()).willReturn(Collections.singletonList(projectMock));

        // when
        projectUpdater.refreshProjectDatabase();

        // then
        verify(projectServiceMock).saveAll(Collections.singletonList(projectMock));

        verify(updateJobRepoMock).save(updateJobArgumentCaptor.capture());
        UpdateJob actualUpdateJob = updateJobArgumentCaptor.getValue();
        assertThat(actualUpdateJob).isEqualTo(expectedUpdateJob);
    }

    @Test
    public void refreshProjectDatabaseGetsProjectsSinceLastUpdateWhenLastUpdateIsMoreThan10MinutesAgo() throws Exception {
        // given
        UpdateJob expectedUpdateJob = new UpdateJob(LocalDateTime.now(clock), UpdateJob.Status.SUCCESS);
        List<Project> expectedProjects = Collections.singletonList(projectMock);
        LocalDateTime lastUpdateTime = LocalDateTime.now(clock).plus(11L, ChronoUnit.MINUTES);

        given(updateJobMock.getTime()).willReturn(lastUpdateTime);

        given(updateJobRepoMock.findFirstByStatusOrderByTimeDesc(UpdateJob.Status.SUCCESS))
                .willReturn(Optional.of(updateJobMock));

        given(updateJobMock.getTime()).willReturn(lastUpdateTime);
        given(projectReaderMock.getAllProjectsSince(lastUpdateTime)).willReturn(expectedProjects);

        // when
        projectUpdater.refreshProjectDatabase();

        // then
        verify(projectServiceMock).saveAll(expectedProjects);

        verify(updateJobRepoMock).save(updateJobArgumentCaptor.capture());
        UpdateJob actualUpdateJob = updateJobArgumentCaptor.getValue();
        assertThat(actualUpdateJob).isEqualTo(expectedUpdateJob);
    }

    @Test
    public void refreshProjectDatabaseDoesNotUpdateWhenLastUpdateIsLessThan10MinutesAgo() throws Exception {
        // given
        LocalDateTime lastUpdateTime = LocalDateTime.now(clock).plus(5L, ChronoUnit.MINUTES);

        given(updateJobMock.getTime()).willReturn(lastUpdateTime);

        given(updateJobRepoMock.findFirstByStatusOrderByTimeDesc(UpdateJob.Status.SUCCESS))
                .willReturn(Optional.of(updateJobMock));

        // when
        projectUpdater.refreshProjectDatabase();

        // then
        verify(projectReaderMock, never()).getAllProjectsSince(any());
        verify(projectReaderMock, never()).getInitialProjects();
        verify(projectServiceMock, never()).saveAll(any());
        verify(updateJobRepoMock, never()).save(any());
    }

    @Test
    public void refreshProjectDatabaseCatchesExceptionsAndSavesFailure() throws Exception {
        // given
        UpdateJob expectedUpdateJob = new UpdateJob(LocalDateTime.now(clock), UpdateJob.Status.FAILURE, new IOException());

        given(updateJobRepoMock.findFirstByStatusOrderByTimeDesc(UpdateJob.Status.SUCCESS))
                .willReturn(Optional.empty());

        given(projectReaderMock.getInitialProjects()).willThrow(new IOException());

        // when
        projectUpdater.refreshProjectDatabase();

        // then
        verify(updateJobRepoMock).save(updateJobArgumentCaptor.capture());
        UpdateJob actualUpdateJob = updateJobArgumentCaptor.getValue();

        assertThat(actualUpdateJob).isEqualTo(expectedUpdateJob);
    }

}