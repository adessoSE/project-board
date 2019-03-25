package de.adesso.projectboard.base.scheduled;

import helper.base.scheduled.FixedHourJob;
import org.junit.Before;
import org.junit.Test;

import java.time.*;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class FixedHourScheduledJobTest {

    private final int EXECUTION_HOUR = 4;

    private FixedHourJob fixedHourJob;

    private Clock clock;

    @Before
    public void setUp() {
        var instant = Instant.parse("2019-03-14T17:00:00.00Z");
        var zoneId = ZoneId.systemDefault();

        this.clock = Clock.fixed(instant, zoneId);
        this.fixedHourJob = new FixedHourJob(clock, EXECUTION_HOUR);
    }

    @Test
    public void constructorDoesNotThrowExceptionWhenHourBetweenZeroAnd23() {
        // given
        var jobExecutionHour = 2;

        // when / then
        new FixedHourJob(clock, jobExecutionHour);
    }

    @Test
    public void constructorThrowsExceptionWhenHourLessThanZero() {
        // given
        var jobExecutionHour = -1;

        // when / then
        assertThatThrownBy(() -> new FixedHourJob(clock, jobExecutionHour))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Job execution hour has to be between 0 and 23!");
    }

    @Test
    public void constructorThrowsExceptionWhenHourMoreThan23() {
        // given
        var jobExecutionHour = 24;

        // when / then
        assertThatThrownBy(() -> new FixedHourJob(clock, jobExecutionHour))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Job execution hour has to be between 0 and 23!");
    }

    @Test
    public void shouldUpdateReturnsTrueWhenLastExecuteAtLeastTwoDaysAgo() {
        // given
        var lastExecuteTime = LocalDateTime.now(clock).minus(2L, ChronoUnit.DAYS);

        // when
        var actualShouldUpdate = fixedHourJob.shouldExecute(lastExecuteTime);

        // then
        assertThat(actualShouldUpdate).isTrue();
    }

    @Test
    public void shouldUpdateReturnsTrueWhenLastExecuteWasTodayButBeforeUpdateHour() {
        // given
        var lastExecuteHour = EXECUTION_HOUR - 1;
        var lastExecuteTime = LocalDate.now(clock).atTime(lastExecuteHour, 0);

        // when
        var actualShouldUpdate = fixedHourJob.shouldExecute(lastExecuteTime);

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
        var localFixedHourJob = new FixedHourJob(localClock, updateHour);

        var lastExecuteTime = LocalDateTime.now(localClock);

        // when
        var actualShouldUpdate = localFixedHourJob.shouldExecute(lastExecuteTime);

        // then
        assertThat(actualShouldUpdate).isFalse();
    }

    @Test
    public void shouldUpdateReturnsTrueWhenLastExecuteWasYesterdayAndUpdateHourPassed() {
        // given
        var updateHour = 4;
        var instant = Instant.parse("2019-03-14T05:00:00.00Z");
        var zoneId = ZoneId.systemDefault();
        var localClock = Clock.fixed(instant, zoneId);
        var localFixedHourJob = new FixedHourJob(localClock, updateHour);

        var lastExecuteTime = LocalDateTime.now(localClock).minus(1L, ChronoUnit.DAYS);

        // when
        var actualShouldUpdate = localFixedHourJob.shouldExecute(lastExecuteTime);

        // then
        assertThat(actualShouldUpdate).isTrue();
    }

    @Test
    public void shouldUpdateReturnsFalseWhenLasExecuteWasYesterdayButUpdateHourDidNotPass() {
        // given
        var updateHour = 7;
        var instant = Instant.parse("2019-03-14T05:00:00.00Z");
        var zoneId = ZoneId.systemDefault();
        var localClock = Clock.fixed(instant, zoneId);
        var localFixedHourJob = new FixedHourJob(localClock, updateHour);

        var lastExecuteTime = LocalDateTime.now(localClock).minus(1L, ChronoUnit.DAYS);

        // when
        var actualShouldUpdate = localFixedHourJob.shouldExecute(lastExecuteTime);

        // then
        assertThat(actualShouldUpdate).isFalse();
    }

}
