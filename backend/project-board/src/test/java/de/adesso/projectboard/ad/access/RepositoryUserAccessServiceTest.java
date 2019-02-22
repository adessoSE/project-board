package de.adesso.projectboard.ad.access;

import de.adesso.projectboard.ad.user.RepositoryUserService;
import de.adesso.projectboard.base.access.handler.UserAccessEventHandler;
import de.adesso.projectboard.base.access.persistence.AccessInterval;
import de.adesso.projectboard.base.access.persistence.AccessIntervalRepository;
import de.adesso.projectboard.base.user.persistence.User;
import org.assertj.core.api.SoftAssertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RepositoryUserAccessServiceTest {

    @Mock
    private RepositoryUserService userService;

    @Mock
    private AccessIntervalRepository intervalRepo;

    @Mock
    private UserAccessEventHandler userAccessEventHandlerMock;

    @Mock
    private User userMock;

    @Mock
    private AccessInterval accessIntervalMock;

    private Clock clock;

    private RepositoryUserAccessService accessService;

    @Before
    public void setUp() {
        Instant instant = Instant.parse("2018-01-01T13:00:00.00Z");
        ZoneId zoneId = ZoneId.systemDefault();

        this.clock = Clock.fixed(instant, zoneId);
        this.accessService = new RepositoryUserAccessService(userService, intervalRepo, userAccessEventHandlerMock, clock);
    }

    @Test
    public void giveUserAccessUntilThrowsExceptionWhenTimeIsInThePast() {
        // given
        LocalDateTime endTime = LocalDateTime.now(clock).minus(1L, ChronoUnit.MINUTES);

        // when
        assertThatThrownBy(() -> accessService.giveUserAccessUntil(userMock, endTime))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("End date must lie in the future!");
    }

    @Test
    public void giveUserAccessUntilUserHasActiveAccess() {
        // given
        LocalDateTime expectedStartTime = LocalDateTime.now(clock).minus(1L, ChronoUnit.DAYS);
        LocalDateTime initialEndTime = LocalDateTime.now(clock).plus(10L, ChronoUnit.MINUTES);
        LocalDateTime expectedEndTime = LocalDateTime.now(clock).plus(20L, ChronoUnit.MINUTES);

        given(userMock.getLatestAccessInterval()).willReturn(Optional.of(accessIntervalMock));

        given(accessIntervalMock.getStartTime()).willReturn(expectedStartTime);
        given(accessIntervalMock.getEndTime()).willReturn(initialEndTime);

        // when
        accessService.giveUserAccessUntil(userMock, expectedEndTime);

        // then
        verify(accessIntervalMock).setEndTime(expectedEndTime);
        verify(intervalRepo).save(accessIntervalMock);
        verify(userAccessEventHandlerMock).onAccessChanged(userMock, accessIntervalMock);
    }

    @Test
    public void giveUserAccessUntilUserHasNoActiveAccess() {
        // given
        LocalDateTime inactiveStartTime = LocalDateTime.now(clock).minus(10L, ChronoUnit.DAYS);
        LocalDateTime inactiveEndTime = LocalDateTime.now(clock).minus(1L, ChronoUnit.DAYS);

        LocalDateTime expectedStartTime = LocalDateTime.now(clock);
        LocalDateTime expectedEndTime = LocalDateTime.now(clock).plus(10L , ChronoUnit.DAYS);

        given(userMock.getLatestAccessInterval()).willReturn(Optional.of(accessIntervalMock));
        given(accessIntervalMock.getStartTime()).willReturn(inactiveStartTime);
        given(accessIntervalMock.getEndTime()).willReturn(inactiveEndTime);

        // when
        accessService.giveUserAccessUntil(userMock, expectedEndTime);

        // then
        ArgumentCaptor<AccessInterval> argument = ArgumentCaptor.forClass(AccessInterval.class);
        verify(userMock, times(2)).addAccessInterval(argument.capture());

        AccessInterval createdAccessInterval = argument.getValue();

        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(createdAccessInterval.getUser()).isEqualTo(userMock);
        softly.assertThat(createdAccessInterval.getStartTime()).isEqualTo(expectedStartTime);
        softly.assertThat(createdAccessInterval.getEndTime()).isEqualTo(expectedEndTime);

        softly.assertAll();

        verify(userService).save(userMock);
        verify(userAccessEventHandlerMock).onAccessCreated(userMock, createdAccessInterval);
    }

    @Test
    public void giveUserAccessUntilUserHasNoAccessInstance() {
        // given
        LocalDateTime expectedStartTime = LocalDateTime.now(clock);
        LocalDateTime expectedEndTime = LocalDateTime.now(clock).plus(10L , ChronoUnit.DAYS);

        given(userMock.getLatestAccessInterval()).willReturn(Optional.empty());

        // when
        accessService.giveUserAccessUntil(userMock, expectedEndTime);

        // then
        ArgumentCaptor<AccessInterval> accessInfoArgumentCaptor = ArgumentCaptor.forClass(AccessInterval.class);
        verify(userMock, times(2)).addAccessInterval(accessInfoArgumentCaptor.capture());

        AccessInterval createdAccessInterval = accessInfoArgumentCaptor.getValue();

        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(createdAccessInterval.getUser()).isEqualTo(userMock);
        softly.assertThat(createdAccessInterval.getStartTime()).isEqualTo(expectedStartTime);
        softly.assertThat(createdAccessInterval.getEndTime()).isEqualTo(expectedEndTime);

        softly.assertAll();

        verify(userService).save(userMock);
        verify(userAccessEventHandlerMock).onAccessCreated(userMock, createdAccessInterval);
    }

    @Test
    public void removeAccessFromUserNoActiveAccessIntervalPresent() {
        // given
        LocalDateTime inactiveStartTime = LocalDateTime.now(clock).minus(10L, ChronoUnit.DAYS);
        LocalDateTime inactiveEndTime = LocalDateTime.now(clock).minus(1L, ChronoUnit.DAYS);

        given(userMock.getLatestAccessInterval()).willReturn(Optional.of(accessIntervalMock));
        given(accessIntervalMock.getStartTime()).willReturn(inactiveStartTime);
        given(accessIntervalMock.getEndTime()).willReturn(inactiveEndTime);

        // when
        accessService.removeAccessFromUser(userMock);

        // then
        verify(accessIntervalMock, never()).setEndTime(any());
        verify(userAccessEventHandlerMock, never()).onAccessCreated(any(), any());
    }

    @Test
    public void removeAccessFromUserActiveAccessIntervalPresent() {
        // given
        LocalDateTime activeStartTime = LocalDateTime.now(clock).minus(10L, ChronoUnit.DAYS);
        LocalDateTime activeEndTime = LocalDateTime.now(clock).plus(1L, ChronoUnit.WEEKS);
        LocalDateTime expectedEndTime = LocalDateTime.now(clock);

        given(userMock.getLatestAccessInterval()).willReturn(Optional.of(accessIntervalMock));
        given(accessIntervalMock.getStartTime()).willReturn(activeStartTime);
        given(accessIntervalMock.getEndTime()).willReturn(activeEndTime);

        // when
        accessService.removeAccessFromUser(userMock);

        // then
        verify(accessIntervalMock).setEndTime(expectedEndTime);
        verify(intervalRepo).save(accessIntervalMock);
        verify(userAccessEventHandlerMock).onAccessRevoked(userMock);
    }

}
