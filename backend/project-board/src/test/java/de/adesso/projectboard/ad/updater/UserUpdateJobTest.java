package de.adesso.projectboard.ad.updater;

import de.adesso.projectboard.ad.configuration.LdapConfigurationProperties;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.*;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class UserUpdateJobTest {

    private final int UPDATE_HOUR = 4;

    @Mock
    private UserUpdater userUpdaterMock;

    @Mock
    private LdapConfigurationProperties ldapConfigPropertiesMock;

    private Clock clock;

    private UserUpdateJob userUpdateJob;

    @Before
    public void setUp() {
        var instant = Instant.parse("2019-03-14T17:00:00.00Z");
        var zoneId = ZoneId.systemDefault();

        given(ldapConfigPropertiesMock.getUpdateHour()).willReturn(UPDATE_HOUR);

        this.clock = Clock.fixed(instant, zoneId);
        this.userUpdateJob = new UserUpdateJob(userUpdaterMock, ldapConfigPropertiesMock, clock);
    }

    @Test
    public void executeWithTimeUpdatesUsersAndHierarchy() {
        // given
        var lastExecuteTime = LocalDateTime.now(clock);

        // when
        userUpdateJob.execute(lastExecuteTime);

        // then
        verify(userUpdaterMock).updateHierarchyAndUserData();
    }

    @Test
    public void executeUpdatesUsersAndHierarchy() {
        // given / when
        userUpdateJob.execute();

        // then
        verify(userUpdaterMock).updateHierarchyAndUserData();
    }

    @Test
    public void getJobIdentifierReturnsExpectedIdentifier() {
        // given
        var expectedIdentifier = "USER-UPDATER";

        // when
        var actualIdentifier = userUpdateJob.getJobIdentifier();

        // then
        assertThat(actualIdentifier).isEqualTo(expectedIdentifier);
    }

    @Test
    public void shouldUpdateReturnsTrueWhenLastExecuteAtLeastTwoDaysAgo() {
        // given
        var lastExecuteTime = LocalDateTime.now(clock).minus(2L, ChronoUnit.DAYS);

        // when
        var actualShouldUpdate = userUpdateJob.shouldExecute(lastExecuteTime);

        // then
        assertThat(actualShouldUpdate).isTrue();
    }

    @Test
    public void shouldUpdateReturnsTrueWhenLastExecuteWasTodayButBeforeUpdateHour() {
        // given
        var lastExecuteHour = UPDATE_HOUR - 1;
        var lastExecuteTime = LocalDate.now(clock).atTime(lastExecuteHour, 0);

        // when
        var actualShouldUpdate = userUpdateJob.shouldExecute(lastExecuteTime);

        // then
        assertThat(actualShouldUpdate).isTrue();
    }

    @Test
    public void shouldUpdateReturnsTrueWhenLastExecuteWasYesterdayAndUpdateHourPassed() {
        // given
        var updateHour = 4;
        var instant = Instant.parse("2019-03-14T05:00:00.00Z");
        var zoneId = ZoneId.systemDefault();
        var localClock = Clock.fixed(instant, zoneId);
        given(ldapConfigPropertiesMock.getUpdateHour()).willReturn(updateHour);
        var localUserUpdateJob = new UserUpdateJob(userUpdaterMock, ldapConfigPropertiesMock, localClock);

        var lastExecuteTime = LocalDateTime.now(localClock).minus(1L, ChronoUnit.DAYS);

        // when
        var actualShouldUpdate = localUserUpdateJob.shouldExecute(lastExecuteTime);

        // then
        assertThat(actualShouldUpdate).isTrue();
    }

    @Test
    public void shouldUpdateReturnsFalseWhenLastExecuteWasTodayAfterUpdateHour() {
        // given
        var updateHour = 4;
        var instant = Instant.parse("2019-03-14T05:00:00.00Z");
        var zoneId = ZoneId.systemDefault();
        var localClock = Clock.fixed(instant, zoneId);
        given(ldapConfigPropertiesMock.getUpdateHour()).willReturn(updateHour);
        var localUserUpdateJob = new UserUpdateJob(userUpdaterMock, ldapConfigPropertiesMock, localClock);

        var lastExecuteTime = LocalDateTime.now(localClock);

        // when
        var actualShouldUpdate = localUserUpdateJob.shouldExecute(lastExecuteTime);

        // then
        assertThat(actualShouldUpdate).isFalse();
    }

}
