package de.adesso.projectboard.ad.updater;

import de.adesso.projectboard.ad.configuration.LdapConfigurationProperties;
import de.adesso.projectboard.ad.updater.persistence.UserUpdateJob;
import de.adesso.projectboard.ad.updater.persistence.UserUpdateJobRepository;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserUpdateSchedulerTest {

    private final long UPDATE_HOUR = 4;

    private final String INSTANT_STRING = "2019-01-21T00:00:00.00Z";

    @Mock
    private UserUpdater userUpdaterMock;

    @Mock
    private UserUpdateJobRepository userUpdateJobRepoMock;

    @Mock
    private LdapConfigurationProperties ldapConfigPropertiesMock;

    @Mock
    private UserUpdateJob userUpdateJobMock;

    @Captor
    private ArgumentCaptor<UserUpdateJob> updateJobArgumentCaptor;

    private Clock clock;

    private UserUpdateScheduler userUpdateScheduler;

    @Before
    public void setUp() {
        initializeUserUpdateSchedulerAndClock(INSTANT_STRING);
    }

    @Test
    public void updateCatchesAllExceptionsAndSavesJob() {
        // given
        var expectedTime = LocalDateTime.now(clock);
        var expectedJob = new UserUpdateJob(expectedTime, false);

        doThrow(IllegalStateException.class).when(userUpdaterMock).updateHierarchyAndUserData();

        // when
        userUpdateScheduler.update();

        // then
        verify(userUpdateJobRepoMock).save(updateJobArgumentCaptor.capture());
        assertThat(updateJobArgumentCaptor.getValue()).isEqualTo(expectedJob);
    }

    @Test
    public void updateDoesNotUpdateWhenShouldUpdateReturnsFalse() {
        // given
        var instantStr = "2019-01-21T13:00:00.00Z";
        initializeUserUpdateSchedulerAndClock(instantStr);

        var lastSuccessfulUpdateTime = LocalDateTime.now(clock);
        given(userUpdateJobMock.getUpdateTime()).willReturn(lastSuccessfulUpdateTime);
        given(userUpdateJobRepoMock.findFirstBySuccessTrueOrderByUpdateTimeDesc()).willReturn(Optional.of(userUpdateJobMock));

        // when
        userUpdateScheduler.update();

        // then
        verify(userUpdaterMock, never()).updateHierarchyAndUserData();
        verify(userUpdateJobRepoMock, never()).save(any());
    }

    @Test
    public void updateSavesJobOnSuccessfulUpdate() {
        // given
        var expectedTime = LocalDateTime.now(clock);
        var expectedJob = new UserUpdateJob(expectedTime, true);

        // when
        userUpdateScheduler.update();

        // then
        verify(userUpdateJobRepoMock).save(updateJobArgumentCaptor.capture());
        assertThat(updateJobArgumentCaptor.getValue()).isEqualTo(expectedJob);
    }

    @Test
    public void shouldUpdateReturnsTrueWhenLastSuccessfulUpdateNull() {
        // given / when / then
        compareShouldUpdateWithExpected(null, true);
    }

    @Test
    public void shouldUpdateReturnsTrueWhenLastSuccessfulUpdateAtLeastTwoDaysAgo() {
        // given
        var lastSuccessfulUpdateTime = LocalDateTime.now(clock).minus(2L, ChronoUnit.DAYS);
        given(userUpdateJobMock.getUpdateTime()).willReturn(lastSuccessfulUpdateTime);

        // when / then
        compareShouldUpdateWithExpected(userUpdateJobMock, true);
    }

    @Test
    public void shouldUpdateReturnsTrueWhenLastSuccessfulUpdateWasTodayButBeforeUpdateHour() {
        // given
        var instantStr = "2019-01-21T02:00:00.00Z";
        initializeUserUpdateSchedulerAndClock(instantStr);

        var lastSuccessfulUpdateTime = LocalDateTime.now(clock);
        given(userUpdateJobMock.getUpdateTime()).willReturn(lastSuccessfulUpdateTime);

        // when / then
        compareShouldUpdateWithExpected(userUpdateJobMock, true);
    }

    @Test
    public void shouldUpdateReturnsTrueWhenLastSuccessFulUpdateWasYesterdayAndUpdateHourPassed() {
        // given
        var instantStr = "2019-01-21T05:00:00.00Z";
        initializeUserUpdateSchedulerAndClock(instantStr);

        var lastSuccessfulUpdateTime = LocalDateTime.now(clock).minus(10L, ChronoUnit.HOURS);
        given(userUpdateJobMock.getUpdateTime()).willReturn(lastSuccessfulUpdateTime);

        // when / then
        compareShouldUpdateWithExpected(userUpdateJobMock, true);
    }

    @Test
    public void shouldUpdateReturnsFalseWhenLastSuccessfulUpdateWasTodayAfterUpdateHour() {
        // given
        var instantStr = "2019-01-21T08:00:00.00Z";
        initializeUserUpdateSchedulerAndClock(instantStr);

        var lastSuccessfulUpdateTime = LocalDateTime.now(clock).minus(1L, ChronoUnit.HOURS);
        given(userUpdateJobMock.getUpdateTime()).willReturn(lastSuccessfulUpdateTime);

        // when / then
        compareShouldUpdateWithExpected(userUpdateJobMock, false);
    }

    private void initializeUserUpdateSchedulerAndClock(String instantStr) {
        given(ldapConfigPropertiesMock.getUpdateHour()).willReturn(UPDATE_HOUR);

        var instant = Instant.parse(instantStr);
        var zoneId = ZoneId.systemDefault();

        this.clock = Clock.fixed(instant, zoneId);
        this.userUpdateScheduler = new UserUpdateScheduler(userUpdaterMock, userUpdateJobRepoMock, clock, ldapConfigPropertiesMock);
    }

    private void compareShouldUpdateWithExpected(UserUpdateJob updateJob, boolean expected) {
        // when
        var actualShouldUpdate = userUpdateScheduler.shouldUpdate(updateJob);

        // then
        assertThat(actualShouldUpdate).isEqualTo(expected);
    }

}