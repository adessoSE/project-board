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

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@RunWith(MockitoJUnitRunner.class)
public class AccessInfoTest {

    @Mock
    User userMock;

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

}