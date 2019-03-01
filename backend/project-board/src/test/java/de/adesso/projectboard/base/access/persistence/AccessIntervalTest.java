package de.adesso.projectboard.base.access.persistence;

import de.adesso.projectboard.base.user.persistence.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

import static de.adesso.projectboard.util.TestHelper.assertEqualsAndHashCodeEquals;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class AccessIntervalTest {

    @Mock
    private User userMock;

    @Mock
    private User otherUserMock;

    private Clock clock;

    @Before
    public void setUp() {
        var instant = Instant.parse("2017-10-10T12:00:00.00Z");
        var zoneId = ZoneId.systemDefault();

        this.clock = Clock.fixed(instant, zoneId);
    }

    @Test
    public void constructorThrowsExceptionWhenStartTimeAfterEndTime() {
        // given
        var startTime = LocalDateTime.now(clock).plus(10L, ChronoUnit.DAYS);
        var endTime = LocalDateTime.now(clock).minus(10L, ChronoUnit.DAYS);

        assertThatThrownBy(() -> new AccessInterval(userMock, startTime, endTime))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("The end time has to be after the start time!");
    }

    @Test
    public void equalsReturnsTrueForSameInstance() {
        // given
        var accessIntervalId = 1L;
        var startTime = LocalDateTime.now(clock);
        var endTime = LocalDateTime.now(clock).plus(1L, ChronoUnit.DAYS);

        var accessInterval = new AccessInterval(userMock, startTime, endTime);
        accessInterval.id = accessIntervalId;

        // when
        boolean actualEquals = accessInterval.equals(accessInterval);

        // then
        assertThat(actualEquals).isTrue();
    }

    @Test
    public void equalsReturnsTrueForSameFieldValuesAndHashCodeEquals() {
        // given
        var accessIntervalId = 1L;
        var userId = "user";
        var startTime = LocalDateTime.now(clock);
        var endTime = LocalDateTime.now(clock).plus(1L, ChronoUnit.DAYS);

        given(userMock.getId()).willReturn(userId);

        var accessInterval = new AccessInterval(userMock, startTime, endTime);
        accessInterval.id = accessIntervalId;

        var otherAccessInterval = new AccessInterval(userMock, startTime, endTime);
        otherAccessInterval.id = accessIntervalId;

        // when & then
        assertEqualsAndHashCodeEquals(accessInterval, otherAccessInterval);
    }

    @Test
    public void equalsReturnsFalseForDifferentIds() {
        // given
        var accessIntervalId = 1L;
        var otherAccessIntervalId = 2L;
        var userId = "user";
        var startTime = LocalDateTime.now(clock);
        var endTime = LocalDateTime.now(clock).plus(1L, ChronoUnit.DAYS);

        given(userMock.getId()).willReturn(userId);

        var accessInterval = new AccessInterval(userMock, startTime, endTime);
        accessInterval.id = accessIntervalId;

        var otherAccessInterval = new AccessInterval(userMock, startTime, endTime);
        otherAccessInterval.id = otherAccessIntervalId;

        // when
        boolean actualEquals = accessInterval.equals(otherAccessInterval);

        // then
        assertThat(actualEquals).isFalse();
    }

    @Test
    public void equalsReturnsFalseForDifferentUserIds() {
        // given
        var accessIntervalId = 1L;
        var userId = "user";
        var otherUserId = "other-user";
        var startTime = LocalDateTime.now(clock);
        var endTime = LocalDateTime.now(clock).plus(1L, ChronoUnit.DAYS);

        given(userMock.getId()).willReturn(userId);
        given(otherUserMock.getId()).willReturn(otherUserId);

        var accessInterval = new AccessInterval(userMock, startTime, endTime);
        accessInterval.id = accessIntervalId;

        var otherAccessInterval = new AccessInterval(otherUserMock, startTime, endTime);
        otherAccessInterval.id = accessIntervalId;

        // when
        boolean actualEquals = accessInterval.equals(otherAccessInterval);

        // then
        assertThat(actualEquals).isFalse();
    }

    @Test
    public void equalsReturnsFalseForDifferentStartTimes() {
        // given
        var accessIntervalId = 1L;
        var userId = "user";
        var startTime = LocalDateTime.now(clock);
        var otherStartTime = LocalDateTime.now(clock).plus(10L, ChronoUnit.MINUTES);
        var endTime = LocalDateTime.now(clock).plus(1L, ChronoUnit.DAYS);

        given(userMock.getId()).willReturn(userId);

        var accessInterval = new AccessInterval(userMock, startTime, endTime);
        accessInterval.id = accessIntervalId;

        var otherAccessInterval = new AccessInterval(userMock, otherStartTime, endTime);
        otherAccessInterval.id = accessIntervalId;

        // when
        boolean actualEquals = accessInterval.equals(otherAccessInterval);

        // then
        assertThat(actualEquals).isFalse();
    }

    @Test
    public void equalsReturnsFalseForDifferentEndTimes() {
        // given
        var accessIntervalId = 1L;
        var userId = "user";
        var startTime = LocalDateTime.now(clock);
        var endTime = LocalDateTime.now(clock).plus(1L, ChronoUnit.DAYS);
        var otherEndTime = LocalDateTime.now(clock).plus(2L, ChronoUnit.DAYS);

        given(userMock.getId()).willReturn(userId);

        var accessInterval = new AccessInterval(userMock, startTime, endTime);
        accessInterval.id = accessIntervalId;

        var otherAccessInterval = new AccessInterval(userMock, startTime, otherEndTime);
        otherAccessInterval.id = accessIntervalId;

        // when
        boolean actualEquals = accessInterval.equals(otherAccessInterval);

        // then
        assertThat(actualEquals).isFalse();
    }

}
