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
public class AccessInfoTest {

    @Mock
    User userMock;

    @Mock
    User otherUserMock;

    Clock clock;

    @Before
    public void setUp() {
        Instant instant = Instant.parse("2017-10-10T12:00:00.00Z");
        ZoneId zoneId = ZoneId.systemDefault();

        this.clock = Clock.fixed(instant, zoneId);
    }

    @Test
    public void constructorThrowsExceptionWhenStartTimeAfterEndTime() {
        // given
        LocalDateTime startTime = LocalDateTime.now(clock).plus(10L, ChronoUnit.DAYS);
        LocalDateTime endTime = LocalDateTime.now(clock).minus(10L, ChronoUnit.DAYS);

        assertThatThrownBy(() -> new AccessInfo(userMock, startTime, endTime))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("The end time has to be after the start time!");
    }

    @Test
    public void equalsReturnsTrueForSameInstance() {
        // given
        long accessInfoId = 1;
        LocalDateTime startTime = LocalDateTime.now(clock);
        LocalDateTime endTime = LocalDateTime.now(clock).plus(1L, ChronoUnit.DAYS);

        AccessInfo accessInfo = new AccessInfo(userMock, startTime, endTime);
        accessInfo.id = accessInfoId;

        // when
        boolean actualEquals = accessInfo.equals(accessInfo);

        // then
        assertThat(actualEquals).isTrue();
    }

    @Test
    public void equalsReturnsTrueForSameFieldValuesAndHashCodeEquals() {
        // given
        long accessInfoId = 1;
        String userId = "user";
        LocalDateTime startTime = LocalDateTime.now(clock);
        LocalDateTime endTime = LocalDateTime.now(clock).plus(1L, ChronoUnit.DAYS);

        given(userMock.getId()).willReturn(userId);

        AccessInfo accessInfo = new AccessInfo(userMock, startTime, endTime);
        accessInfo.id = accessInfoId;

        AccessInfo otherAccessInfo = new AccessInfo(userMock, startTime, endTime);
        otherAccessInfo.id = accessInfoId;

        // when & then
        assertEqualsAndHashCodeEquals(accessInfo, otherAccessInfo);
    }

    @Test
    public void equalsReturnsFalseForDifferentIds() {
        // given
        long accessInfoId = 1;
        long otherAccessInfoId = 2;
        String userId = "user";
        LocalDateTime startTime = LocalDateTime.now(clock);
        LocalDateTime endTime = LocalDateTime.now(clock).plus(1L, ChronoUnit.DAYS);

        given(userMock.getId()).willReturn(userId);

        AccessInfo accessInfo = new AccessInfo(userMock, startTime, endTime);
        accessInfo.id = accessInfoId;

        AccessInfo otherAccessInfo = new AccessInfo(userMock, startTime, endTime);
        otherAccessInfo.id = otherAccessInfoId;

        // when
        boolean actualEquals = accessInfo.equals(otherAccessInfo);

        // then
        assertThat(actualEquals).isFalse();
    }

    @Test
    public void equalsReturnsFalseForDifferentUserIds() {
        // given
        long accessInfoId = 1;
        String userId = "user";
        String otherUserId = "other-user";
        LocalDateTime startTime = LocalDateTime.now(clock);
        LocalDateTime endTime = LocalDateTime.now(clock).plus(1L, ChronoUnit.DAYS);

        given(userMock.getId()).willReturn(userId);
        given(otherUserMock.getId()).willReturn(otherUserId);

        AccessInfo accessInfo = new AccessInfo(userMock, startTime, endTime);
        accessInfo.id = accessInfoId;

        AccessInfo otherAccessInfo = new AccessInfo(otherUserMock, startTime, endTime);
        otherAccessInfo.id = accessInfoId;

        // when
        boolean actualEquals = accessInfo.equals(otherAccessInfo);

        // then
        assertThat(actualEquals).isFalse();
    }

    @Test
    public void equalsReturnsFalseForDifferentStartTimes() {
        // given
        long accessInfoId = 1;
        String userId = "user";
        LocalDateTime startTime = LocalDateTime.now(clock);
        LocalDateTime otherStartTime = LocalDateTime.now(clock).plus(10L, ChronoUnit.MINUTES);
        LocalDateTime endTime = LocalDateTime.now(clock).plus(1L, ChronoUnit.DAYS);

        given(userMock.getId()).willReturn(userId);

        AccessInfo accessInfo = new AccessInfo(userMock, startTime, endTime);
        accessInfo.id = accessInfoId;

        AccessInfo otherAccessInfo = new AccessInfo(userMock, otherStartTime, endTime);
        otherAccessInfo.id = accessInfoId;

        // when
        boolean actualEquals = accessInfo.equals(otherAccessInfo);

        // then
        assertThat(actualEquals).isFalse();
    }

    @Test
    public void equalsReturnsFalseForDifferentEndTimes() {
        // given
        long accessInfoId = 1;
        String userId = "user";
        LocalDateTime startTime = LocalDateTime.now(clock);
        LocalDateTime endTime = LocalDateTime.now(clock).plus(1L, ChronoUnit.DAYS);
        LocalDateTime otherEndTime = LocalDateTime.now(clock).plus(2L, ChronoUnit.DAYS);

        given(userMock.getId()).willReturn(userId);

        AccessInfo accessInfo = new AccessInfo(userMock, startTime, endTime);
        accessInfo.id = accessInfoId;

        AccessInfo otherAccessInfo = new AccessInfo(userMock, startTime, otherEndTime);
        otherAccessInfo.id = accessInfoId;

        // when
        boolean actualEquals = accessInfo.equals(otherAccessInfo);

        // then
        assertThat(actualEquals).isFalse();
    }

}