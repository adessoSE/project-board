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

import static org.assertj.core.api.Assertions.assertThat;
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
        var instant = Instant.parse("2018-01-01T13:00:00.00Z");
        var zoneId = ZoneId.systemDefault();

        this.clock = Clock.fixed(instant, zoneId);
        this.accessService = new RepositoryUserAccessService(userService, intervalRepo, userAccessEventHandlerMock, clock);
    }

    @Test
    public void giveUserAccessUntilThrowsExceptionWhenTimeIsInThePast() {
        // given
        var endTime = LocalDateTime.now(clock).minus(1L, ChronoUnit.MINUTES);

        // when
        assertThatThrownBy(() -> accessService.giveUserAccessUntil(userMock, endTime))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("End date must lie in the future!");
    }

    @Test
    public void giveUserAccessUntilDoesNotUpdateAccessWhenOldEndEqualsNewEnd() {
        var expectedStartTime = LocalDateTime.now(clock).minus(1L, ChronoUnit.DAYS);
        var initialEndTime = LocalDateTime.now(clock).plus(10L, ChronoUnit.MINUTES);

        given(userMock.getLatestAccessInterval()).willReturn(Optional.of(accessIntervalMock));

        given(accessIntervalMock.getStartTime()).willReturn(expectedStartTime);
        given(accessIntervalMock.getEndTime()).willReturn(initialEndTime);

        // when
        var actualUser = accessService.giveUserAccessUntil(userMock, initialEndTime);

        // then
        assertThat(actualUser).isEqualTo(userMock);

        verify(accessIntervalMock, never()).setEndTime(any(LocalDateTime.class));
        verifyZeroInteractions(intervalRepo);
        verifyZeroInteractions(userAccessEventHandlerMock);
    }

    @Test
    public void giveUserAccessUntilSetsEndTimeWhenUserHasActiveAccess() {
        // given
        var expectedStartTime = LocalDateTime.now(clock).minus(1L, ChronoUnit.DAYS);
        var initialEndTime = LocalDateTime.now(clock).plus(10L, ChronoUnit.MINUTES);
        var expectedEndTime = LocalDateTime.now(clock).plus(20L, ChronoUnit.MINUTES);

        given(userMock.getLatestAccessInterval()).willReturn(Optional.of(accessIntervalMock));

        given(accessIntervalMock.getStartTime()).willReturn(expectedStartTime);
        given(accessIntervalMock.getEndTime()).willReturn(initialEndTime);

        // when
        var actualUser = accessService.giveUserAccessUntil(userMock, expectedEndTime);

        // then
        assertThat(actualUser).isEqualTo(userMock);;

        verify(accessIntervalMock).setEndTime(expectedEndTime);
        verify(intervalRepo).save(accessIntervalMock);
        verify(userAccessEventHandlerMock).onAccessChanged(userMock, accessIntervalMock, initialEndTime);
    }

    @Test
    public void giveUserAccessUntilCreatesAccessWhenUserHasNoActiveAccess() {
        // given
        var inactiveStartTime = LocalDateTime.now(clock).minus(10L, ChronoUnit.DAYS);
        var inactiveEndTime = LocalDateTime.now(clock).minus(1L, ChronoUnit.DAYS);

        var expectedStartTime = LocalDateTime.now(clock);
        var expectedEndTime = LocalDateTime.now(clock).plus(10L , ChronoUnit.DAYS);

        given(userMock.getLatestAccessInterval()).willReturn(Optional.of(accessIntervalMock));
        given(accessIntervalMock.getStartTime()).willReturn(inactiveStartTime);
        given(accessIntervalMock.getEndTime()).willReturn(inactiveEndTime);
        given(userService.save(userMock)).willReturn(userMock);

        // when
        var actualUser = accessService.giveUserAccessUntil(userMock, expectedEndTime);

        // then
        assertThat(actualUser).isEqualTo(userMock);

        var captor = ArgumentCaptor.forClass(AccessInterval.class);
        verify(userMock, times(2)).addAccessInterval(captor.capture());
        var createdAccessInterval = captor.getValue();

        var softly = new SoftAssertions();
        softly.assertThat(createdAccessInterval.getUser()).isEqualTo(userMock);
        softly.assertThat(createdAccessInterval.getStartTime()).isEqualTo(expectedStartTime);
        softly.assertThat(createdAccessInterval.getEndTime()).isEqualTo(expectedEndTime);
        softly.assertAll();

        verify(userService).save(userMock);
        verify(userAccessEventHandlerMock).onAccessCreated(userMock, createdAccessInterval);
    }

    @Test
    public void giveUserAccessUntilCreatesAccessWhenUserHasNoAccessInstance() {
        // given
        var expectedStartTime = LocalDateTime.now(clock);
        var expectedEndTime = LocalDateTime.now(clock).plus(10L , ChronoUnit.DAYS);

        given(userMock.getLatestAccessInterval()).willReturn(Optional.empty());
        given(userService.save(userMock)).willReturn(userMock);

        // when
        var actualUser = accessService.giveUserAccessUntil(userMock, expectedEndTime);

        // then
        assertThat(actualUser).isEqualTo(userMock);

        var accessInfoArgumentCaptor = ArgumentCaptor.forClass(AccessInterval.class);
        verify(userMock, times(2)).addAccessInterval(accessInfoArgumentCaptor.capture());
        var createdAccessInterval = accessInfoArgumentCaptor.getValue();

        var softly = new SoftAssertions();
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
        var inactiveStartTime = LocalDateTime.now(clock).minus(10L, ChronoUnit.DAYS);
        var inactiveEndTime = LocalDateTime.now(clock).minus(1L, ChronoUnit.DAYS);

        given(userMock.getLatestAccessInterval()).willReturn(Optional.of(accessIntervalMock));
        given(accessIntervalMock.getStartTime()).willReturn(inactiveStartTime);
        given(accessIntervalMock.getEndTime()).willReturn(inactiveEndTime);

        // when
        var actualUser = accessService.removeAccessFromUser(userMock);

        // then
        assertThat(actualUser).isEqualTo(userMock);

        verify(accessIntervalMock, never()).setEndTime(any());
        verify(userAccessEventHandlerMock, never()).onAccessCreated(any(), any());
    }

    @Test
    public void removeAccessFromUserActiveAccessIntervalPresent() {
        // given
        var activeStartTime = LocalDateTime.now(clock).minus(10L, ChronoUnit.DAYS);
        var activeEndTime = LocalDateTime.now(clock).plus(1L, ChronoUnit.WEEKS);
        var expectedEndTime = LocalDateTime.now(clock);

        given(userMock.getLatestAccessInterval()).willReturn(Optional.of(accessIntervalMock));
        given(accessIntervalMock.getStartTime()).willReturn(activeStartTime);
        given(accessIntervalMock.getEndTime()).willReturn(activeEndTime);

        // when
        var actualUser = accessService.removeAccessFromUser(userMock);

        // then
        assertThat(actualUser).isEqualTo(userMock);

        verify(accessIntervalMock).setEndTime(expectedEndTime);
        verify(intervalRepo).save(accessIntervalMock);
        verify(userAccessEventHandlerMock).onAccessRevoked(userMock, activeEndTime);
    }

    @Test
    public void userHasActiveAccessIntervalReturnsTrueWhenStartDateBeforeAndEndDateAfterToday() {
        // given
        var startDateTime = LocalDateTime.now(clock).minus(1L, ChronoUnit.DAYS);
        var endDateTime = LocalDateTime.now(clock).plus(1L, ChronoUnit.DAYS);

        given(userMock.getLatestAccessInterval()).willReturn(Optional.of(accessIntervalMock));

        given(accessIntervalMock.getStartTime()).willReturn(startDateTime);
        given(accessIntervalMock.getEndTime()).willReturn(endDateTime);

        // when
        boolean actualHasAccess = accessService.userHasActiveAccessInterval(userMock);

        // then
        assertThat(actualHasAccess).isTrue();
    }

    @Test
    public void userHasActiveAccessIntervalReturnsTrueWhenStartDateBeforeEndDateEqualToToday() {
        // given
        var startDateTime = LocalDateTime.now(clock).minus(1L, ChronoUnit.DAYS);
        var endDateTime = LocalDateTime.now(clock);

        given(userMock.getLatestAccessInterval()).willReturn(Optional.of(accessIntervalMock));

        given(accessIntervalMock.getStartTime()).willReturn(startDateTime);
        given(accessIntervalMock.getEndTime()).willReturn(endDateTime);

        // when
        boolean actualHasAccess = accessService.userHasActiveAccessInterval(userMock);

        // then
        assertThat(actualHasAccess).isTrue();
    }

    @Test
    public void userHasActiveAccessIntervalReturnsTrueWhenStartDateEqualToAndEndDateAfterToday() {
        // given
        var startDateTime = LocalDateTime.now(clock).minus(1L, ChronoUnit.MINUTES);
        var endDateTime = LocalDateTime.now(clock).plus(10L, ChronoUnit.DAYS);

        given(userMock.getLatestAccessInterval()).willReturn(Optional.of(accessIntervalMock));

        given(accessIntervalMock.getStartTime()).willReturn(startDateTime);
        given(accessIntervalMock.getEndTime()).willReturn(endDateTime);

        // when
        boolean actualHasAccess = accessService.userHasActiveAccessInterval(userMock);

        // then
        assertThat(actualHasAccess).isTrue();
    }

    @Test
    public void userHasActiveAccessIntervalReturnsFalseWhenStartDateAndEndDateBeforeToday() {
        // given
        var startDateTime = LocalDateTime.now(clock).minus(3L, ChronoUnit.DAYS);
        var endDateTime = LocalDateTime.now(clock).minus(2L, ChronoUnit.DAYS);

        given(userMock.getLatestAccessInterval()).willReturn(Optional.of(accessIntervalMock));

        given(accessIntervalMock.getStartTime()).willReturn(startDateTime);
        given(accessIntervalMock.getEndTime()).willReturn(endDateTime);

        // when
        boolean actualHasAccess = accessService.userHasActiveAccessInterval(userMock);

        // then
        assertThat(actualHasAccess).isFalse();
    }

    @Test
    public void userHasActiveAccessReturnsFalseWhenNoAccessIntervalPresent() {
        // given
        given(userMock.getLatestAccessInterval()).willReturn(Optional.empty());

        // when
        boolean actualHasAccess = accessService.userHasActiveAccessInterval(userMock);

        // then
        assertThat(actualHasAccess).isFalse();
    }

    @Test
    public void userHasActiveAccessIntervalReturnsFalseWhenStartDateAfterToday() {
        // given
        var startDateTime = LocalDateTime.now(clock).plus(1L, ChronoUnit.DAYS);
        var endDateTime = LocalDateTime.now(clock).plus(3L, ChronoUnit.DAYS);

        given(userMock.getLatestAccessInterval()).willReturn(Optional.of(accessIntervalMock));

        given(accessIntervalMock.getStartTime()).willReturn(startDateTime);
        given(accessIntervalMock.getEndTime()).willReturn(endDateTime);

        // when
        boolean actualHasAccess = accessService.userHasActiveAccessInterval(userMock);

        // then
        assertThat(actualHasAccess).isFalse();
    }

}
