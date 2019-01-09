package de.adesso.projectboard.ldap.access;

import de.adesso.projectboard.base.access.persistence.AccessInfo;
import de.adesso.projectboard.base.access.persistence.AccessInfoRepository;
import de.adesso.projectboard.base.user.persistence.User;
import de.adesso.projectboard.ldap.user.LdapUserService;
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
public class LdapUserAccessServiceTest {

    @Mock
    LdapUserService userService;

    @Mock
    AccessInfoRepository infoRepo;

    @Mock
    User userMock;

    @Mock
    AccessInfo accessInfoMock;

    Clock clock;

    LdapUserAccessService accessService;

    @Before
    public void setUp() {
        Instant instant = Instant.parse("2018-01-01T13:00:00.00Z");
        ZoneId zoneId = ZoneId.systemDefault();

        this.clock = Clock.fixed(instant, zoneId);
        this.accessService = new LdapUserAccessService(userService, infoRepo, clock);
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

        given(userMock.getLatestAccessInfo()).willReturn(Optional.of(accessInfoMock));

        given(accessInfoMock.getAccessStart()).willReturn(expectedStartTime);
        given(accessInfoMock.getAccessEnd()).willReturn(initialEndTime);

        // when
        accessService.giveUserAccessUntil(userMock, expectedEndTime);

        // then
        verify(accessInfoMock).setAccessEnd(expectedEndTime);
        verify(infoRepo).save(accessInfoMock);
    }

    @Test
    public void giveUserAccessUntilUserHasNoActiveAccess() {
        // given
        LocalDateTime inactiveStartTime = LocalDateTime.now(clock).minus(10L, ChronoUnit.DAYS);
        LocalDateTime inactiveEndTime = LocalDateTime.now(clock).minus(1L, ChronoUnit.DAYS);

        LocalDateTime expectedStartTime = LocalDateTime.now(clock);
        LocalDateTime expectedEndTime = LocalDateTime.now(clock).plus(10L , ChronoUnit.DAYS);

        given(userMock.getLatestAccessInfo()).willReturn(Optional.of(accessInfoMock));
        given(accessInfoMock.getAccessStart()).willReturn(inactiveStartTime);
        given(accessInfoMock.getAccessEnd()).willReturn(inactiveEndTime);

        // when
        accessService.giveUserAccessUntil(userMock, expectedEndTime);

        // then
        ArgumentCaptor<AccessInfo> argument = ArgumentCaptor.forClass(AccessInfo.class);
        verify(userMock, times(2)).addAccessInfo(argument.capture());

        AccessInfo createdAccessInfo = argument.getValue();

        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(createdAccessInfo.getUser()).isEqualTo(userMock);
        softly.assertThat(createdAccessInfo.getAccessStart()).isEqualTo(expectedStartTime);
        softly.assertThat(createdAccessInfo.getAccessEnd()).isEqualTo(expectedEndTime);

        softly.assertAll();

        verify(userService).save(userMock);
    }

    @Test
    public void giveUserAccessUntilUserHasNoAccessInstance() {
        // given
        LocalDateTime expectedStartTime = LocalDateTime.now(clock);
        LocalDateTime expectedEndTime = LocalDateTime.now(clock).plus(10L , ChronoUnit.DAYS);

        given(userMock.getLatestAccessInfo()).willReturn(Optional.empty());

        // when
        accessService.giveUserAccessUntil(userMock, expectedEndTime);

        // then
        ArgumentCaptor<AccessInfo> accessInfoArgumentCaptor = ArgumentCaptor.forClass(AccessInfo.class);
        verify(userMock, times(2)).addAccessInfo(accessInfoArgumentCaptor.capture());

        AccessInfo createdAccessInfo = accessInfoArgumentCaptor.getValue();

        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(createdAccessInfo.getUser()).isEqualTo(userMock);
        softly.assertThat(createdAccessInfo.getAccessStart()).isEqualTo(expectedStartTime);
        softly.assertThat(createdAccessInfo.getAccessEnd()).isEqualTo(expectedEndTime);

        softly.assertAll();

        verify(userService).save(userMock);
    }

    @Test
    public void removeAccessFromUserNoActiveAccessInfoPresent() {
        // given
        LocalDateTime inactiveStartTime = LocalDateTime.now(clock).minus(10L, ChronoUnit.DAYS);
        LocalDateTime inactiveEndTime = LocalDateTime.now(clock).minus(1L, ChronoUnit.DAYS);

        given(userMock.getLatestAccessInfo()).willReturn(Optional.of(accessInfoMock));
        given(accessInfoMock.getAccessStart()).willReturn(inactiveStartTime);
        given(accessInfoMock.getAccessEnd()).willReturn(inactiveEndTime);

        // when
        accessService.removeAccessFromUser(userMock);

        // then
        verify(accessInfoMock, never()).setAccessEnd(any());
    }

    @Test
    public void removeAccessFromUserActiveAccessInfoPresent() {
        // given
        LocalDateTime activeStartTime = LocalDateTime.now(clock).minus(10L, ChronoUnit.DAYS);
        LocalDateTime activeEndTime = LocalDateTime.now(clock).plus(1L, ChronoUnit.WEEKS);
        LocalDateTime expectedEndTime = LocalDateTime.now(clock);

        given(userMock.getLatestAccessInfo()).willReturn(Optional.of(accessInfoMock));
        given(accessInfoMock.getAccessStart()).willReturn(activeStartTime);
        given(accessInfoMock.getAccessEnd()).willReturn(activeEndTime);

        // when
        accessService.removeAccessFromUser(userMock);

        // then
        verify(accessInfoMock).setAccessEnd(expectedEndTime);
        verify(infoRepo).save(accessInfoMock);
    }

}