package de.adesso.projectboard.ad.updater;

import de.adesso.projectboard.ad.configuration.LdapConfigurationProperties;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

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


}
