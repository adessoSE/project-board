package de.adesso.projectboard.base.cleaner;

import de.adesso.projectboard.base.access.persistence.AccessInterval;
import de.adesso.projectboard.base.access.persistence.AccessIntervalRepository;
import de.adesso.projectboard.base.application.persistence.ProjectApplication;
import de.adesso.projectboard.base.project.persistence.Project;
import de.adesso.projectboard.base.project.service.ProjectService;
import de.adesso.projectboard.base.user.persistence.User;
import de.adesso.projectboard.base.user.service.BookmarkService;
import de.adesso.projectboard.base.user.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.keycloak.representations.adapters.config.PolicyEnforcerConfig;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class CleanerTest {

    @Mock
    private UserService userServiceMock;

    @Mock
    private BookmarkService bookmarkServiceMock;

    @Mock
    private ProjectService projectServiceMock;

    @Mock
    private AccessIntervalRepository airMock;

    @Mock
    private AccessInterval accessIntervalMock;

    @Mock
    private User userMock;

    private Cleaner cleaner;

    private Clock clock;

    @Before
    public void setUp() {
        Instant instant = Instant.parse("2019-02-02T00:00:00.00Z");
        ZoneId zoneId = ZoneId.systemDefault();

        this.clock = Clock.fixed(instant, zoneId);

        this.cleaner = new Cleaner(this.userServiceMock, this.bookmarkServiceMock, this.airMock, this.clock);
    }

    @Test
    public void removeOldBookmarksAndApplications() {
        // given
        var firstIntervalEndTime = LocalDateTime.now(clock).minus(29L, ChronoUnit.DAYS);
        var secondIntervalEndTime = LocalDateTime.now(clock).plus(2L, ChronoUnit.DAYS);

        given(airMock.findAllLatestIntervals()).willReturn(List.of(accessIntervalMock, accessIntervalMock));

        given(accessIntervalMock.getEndTime()).willReturn(firstIntervalEndTime, secondIntervalEndTime);
        given(accessIntervalMock.getUser()).willReturn(userMock);

        // when
        cleaner.removeOldBookmarksAndApplications();

        // then
        verify(bookmarkServiceMock).removeAllBookmarksOfUser(userMock);
        verify(userServiceMock).removeAllApplicationsOfUser(userMock);
    }

}
