package de.adesso.projectboard.base.project.updater;

import de.adesso.projectboard.base.configuration.ProjectBoardConfigurationProperties;
import de.adesso.projectboard.base.normalizer.Normalizer;
import de.adesso.projectboard.base.project.persistence.Project;
import de.adesso.projectboard.base.project.service.ProjectService;
import de.adesso.projectboard.base.reader.ProjectReader;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ProjectUpdaterTest {

    private static final long REFRESH_INTERVAL = 10;

    @Captor
    private ArgumentCaptor<List<Project>> projectListCaptor;

    @Mock
    private ProjectService projectServiceMock;

    @Mock
    private ProjectReader projectReaderMock;

    @Mock
    private ProjectBoardConfigurationProperties pbConfigPropertiesMock;

    @Mock
    private Project projectMock;

    @Mock
    private Normalizer<Project> normalizerMock;

    private Clock clock;

    private ProjectUpdater projectUpdater;

    @Before
    public void setUp() {
        var instant = Instant.parse("2019-03-14T16:31:00.00Z");
        var zoneId = ZoneId.systemDefault();

        given(pbConfigPropertiesMock.getRefreshInterval()).willReturn(REFRESH_INTERVAL);

        this.clock = Clock.fixed(instant, zoneId);
        this.projectUpdater = new ProjectUpdater(projectServiceMock, projectReaderMock, pbConfigPropertiesMock, List.of(normalizerMock), clock);
    }

    @Test
    public void executeWithTimeSavesAllProjectsSince() throws Exception {
        // given
        var lastExecuteTime = LocalDateTime.now(clock);
        var expectedProjects = List.of(projectMock);

        given(projectReaderMock.getAllProjectsSince(lastExecuteTime))
                .willReturn(expectedProjects);
        given(normalizerMock.normalize(expectedProjects))
                .willReturn(expectedProjects);

        // when
        projectUpdater.execute(lastExecuteTime);

        // then
        verify(projectServiceMock).saveAll(projectListCaptor.capture());

        var actualProjects = projectListCaptor.getValue();
        assertThat(actualProjects).containsExactlyElementsOf(expectedProjects);
    }

    @Test
    public void executeSavesInitialProjects() throws Exception {
        // given
        var expectedProjects = List.of(projectMock);

        given(projectReaderMock.getInitialProjects())
                .willReturn(expectedProjects);
        given(normalizerMock.normalize(expectedProjects))
                .willReturn(expectedProjects);

        // when
        projectUpdater.execute();

        // then
        verify(projectServiceMock).saveAll(projectListCaptor.capture());

        var actualProjects = projectListCaptor.getValue();
        assertThat(actualProjects).containsExactlyElementsOf(expectedProjects);
    }

    @Test
    public void getJobIdentifierReturnsExpectedIdentifier() {
        // given
        var expectedIdentifier = "PROJECT-UPDATER";

        // when
        var actualIdentifier = projectUpdater.getJobIdentifier();

        // then
        assertThat(actualIdentifier).isEqualTo(expectedIdentifier);
    }

    @Test
    public void shouldExecuteReturnsTrueWhenLastExecuteMoreThanRefreshIntervalMinutesAgo() {
        // given
        var minutesAgo = REFRESH_INTERVAL + 1L;
        var lastExecuteTime = LocalDateTime.now(clock).plus(minutesAgo, ChronoUnit.MINUTES);

        // when
        var actualShouldUpdate = projectUpdater.shouldExecute(lastExecuteTime);

        // then
        assertThat(actualShouldUpdate).isTrue();
    }

    @Test
    public void shouldExecuteReturnsTrueWhenLastExecuteExactlyRefreshIntervalMinutesAgo() {
        // given
        var lastExecuteTime = LocalDateTime.now(clock).plus(REFRESH_INTERVAL, ChronoUnit.MINUTES);

        // when
        var actualShouldUpdate = projectUpdater.shouldExecute(lastExecuteTime);

        // then
        assertThat(actualShouldUpdate).isTrue();
    }

    @Test
    public void shouldExecuteReturnsFalseWhenLastExecuteLessThanRefreshIntervalMinutesAgo() {
        // given
        var minutesAgo = REFRESH_INTERVAL - 1L;
        var lastExecuteTime = LocalDateTime.now(clock).plus(minutesAgo, ChronoUnit.MINUTES);

        // when
        var actualShouldUpdate = projectUpdater.shouldExecute(lastExecuteTime);

        // then
        assertThat(actualShouldUpdate).isFalse();
    }

}
