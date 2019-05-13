package de.adesso.projectboard.ad.application.service;

import de.adesso.projectboard.base.application.handler.ProjectApplicationOfferedEventHandler;
import de.adesso.projectboard.base.application.handler.ProjectApplicationReceivedEventHandler;
import de.adesso.projectboard.base.application.persistence.ProjectApplication;
import de.adesso.projectboard.base.application.persistence.ProjectApplicationRepository;
import de.adesso.projectboard.base.exceptions.AlreadyAppliedException;
import de.adesso.projectboard.base.exceptions.ApplicationAlreadyOfferedException;
import de.adesso.projectboard.base.exceptions.ApplicationNotFoundException;
import de.adesso.projectboard.base.project.persistence.Project;
import de.adesso.projectboard.base.project.service.ProjectService;
import de.adesso.projectboard.base.user.persistence.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Sort;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class RepositoryApplicationServiceTest {

    private final String PROJECT_ID = "project";

    @Mock
    private ProjectService projectServiceMock;

    @Mock
    private ProjectApplicationRepository applicationRepoMock;

    @Mock
    private ProjectApplicationReceivedEventHandler applicationEventHandlerMock;

    @Mock
    private ProjectApplicationOfferedEventHandler applicationOfferedEventHandlerMock;

    @Mock
    private User userMock;

    @Mock
    private Project projectMock;

    private Clock clock;

    private RepositoryApplicationService applicationService;

    @Before
    public void setUp() {
        var instant = Instant.parse("2018-01-01T10:00:00.00Z");
        var zoneId = ZoneId.systemDefault();

        this.clock = Clock.fixed(instant, zoneId);
        this.applicationService = new RepositoryApplicationService(projectServiceMock, applicationRepoMock,
                applicationEventHandlerMock, applicationOfferedEventHandlerMock, clock);
    }

    @Test
    public void userHasAppliedForProjectReturnsTrueWhenApplicationIsPresent() {
        // given
        given(applicationRepoMock.existsByUserAndProject(userMock, projectMock)).willReturn(true);

        // when
        boolean actualApplied = applicationService.userHasAppliedForProject(userMock, projectMock);

        // then
        assertThat(actualApplied).isTrue();
    }

    @Test
    public void userHasAppliedForProjectReturnsFalseWhenApplicationNotPresent() {
        // given
        given(applicationRepoMock.existsByUserAndProject(userMock, projectMock)).willReturn(false);

        // when
        boolean actualApplied = applicationService.userHasAppliedForProject(userMock, projectMock);

        // then
        assertThat(actualApplied).isFalse();
    }

    @Test
    public void createApplicationForUser() {
        // given
        var expectedComment = "A nice Comment!";
        var expectedDate = LocalDateTime.now(clock);
        var expectedApplication = new ProjectApplication(projectMock, expectedComment, userMock, expectedDate);

        given(applicationRepoMock.existsByUserAndProject(userMock, projectMock)).willReturn(false);
        given(projectServiceMock.getProjectById(PROJECT_ID)).willReturn(projectMock);
        given(applicationRepoMock.existsByUserAndProject(userMock, projectMock)).willReturn(false);

        given(applicationRepoMock.save(expectedApplication)).willReturn(expectedApplication);

        // when
        var actualApplication = applicationService.createApplicationForUser(userMock, PROJECT_ID, expectedComment);

        // then
        assertThat(actualApplication).isEqualTo(expectedApplication);

        verify(applicationRepoMock).save(actualApplication);
        verify(applicationEventHandlerMock).onApplicationReceived(expectedApplication);
    }

    @Test
    public void createApplicationForUserThrowsExceptionWhenAlreadyApplied() {
        // given
        given(projectServiceMock.getProjectById(PROJECT_ID)).willReturn(projectMock);
        given(applicationRepoMock.existsByUserAndProject(userMock, projectMock)).willReturn(true);

        // when
        assertThatThrownBy(() -> applicationService.createApplicationForUser(userMock, PROJECT_ID, ""))
                .isInstanceOf(AlreadyAppliedException.class);
    }

    @Test
    public void getApplicationsOfUser() {
        // given
        var sort = Sort.unsorted();
        var firstApplicationMock = mock(ProjectApplication.class);
        var secondApplicationMock = mock(ProjectApplication.class);
        var expectedApplications = List.of(firstApplicationMock, secondApplicationMock);

        given(applicationRepoMock.findAllByUser(userMock, sort)).willReturn(expectedApplications);

        // when
        var actualApplications = applicationService.getApplicationsOfUser(userMock, sort);

        // then
        assertThat(actualApplications).containsExactlyInAnyOrder(firstApplicationMock, secondApplicationMock);
    }

    @Test
    public void getApplicationsOfUsers() {
        // given
        var sort = Sort.unsorted();
        var users = Set.of(userMock);
        var expectedApplication = new ProjectApplication(projectMock, "Comment", userMock, LocalDateTime.now(clock));

        given(applicationRepoMock.findAllByUserIn(users, sort)).willReturn(Collections.singletonList(expectedApplication));

        // when
        var actualApplications = applicationService.getApplicationsOfUsers(users, sort);

        // then
        assertThat(actualApplications).containsExactlyInAnyOrder(expectedApplication);

        verify(applicationRepoMock).findAllByUserIn(users, sort);
    }

    @Test
    public void deleteApplicationDeletesApplicationWhenPresent() {
        // given
        var applicationId = 1L;
        var application = new ProjectApplication(projectMock, "Comment", userMock, LocalDateTime.now(clock));

        given(applicationRepoMock.findByUserAndId(userMock, applicationId)).willReturn(Optional.of(application));

        // when
        var deletedApplication = applicationService.deleteApplication(userMock, applicationId);

        // then
        assertThat(deletedApplication).isEqualTo(application);

        verify(applicationRepoMock).delete(application);
    }

    @Test
    public void deleteApplicationDeletesApplicationWhenApplicationNotFound() {
        // given
        var applicationId = 1L;

        given(applicationRepoMock.findByUserAndId(userMock, applicationId)).willReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> applicationService.deleteApplication(userMock, applicationId))
                .isInstanceOf(ApplicationNotFoundException.class);
    }

    @Test
    public void offerApplicationThrowsExceptionWhenApplicationNotFound() {
        // given
        var applicationId = 1L;
        var offeringUser = mock(User.class);
        var offeredUser = mock(User.class);

        given(applicationRepoMock.findByUserAndId(offeredUser, applicationId)).willReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> applicationService.offerApplication(offeringUser, offeredUser, applicationId))
                .isInstanceOf(ApplicationNotFoundException.class);
    }

    @Test
    public void offerApplicationThrowsExceptionWhenApplicationIsAlreadyOffered() {
        // given
        var applicationId = 1L;
        var offeringUser = mock(User.class);
        var offeredUser = mock(User.class);

        var application = new ProjectApplication(projectMock, "Comment", offeredUser, LocalDateTime.now(clock))
                .setOffered(true);

        given(applicationRepoMock.findByUserAndId(offeredUser, applicationId)).willReturn(Optional.of(application));

        // when / then
        assertThatThrownBy(() -> applicationService.offerApplication(offeringUser, offeredUser, applicationId))
                .isInstanceOf(ApplicationAlreadyOfferedException.class);
    }

    @Test
    public void offerApplicationOffersApplicationWhenPresentAndNotOffered() {
        // given
        var applicationId = 1L;
        var offeringUserMock = mock(User.class);
        var offeredUserMock = mock(User.class);

        var application = new ProjectApplication(projectMock, "Comment", offeredUserMock, LocalDateTime.now(clock));
        var expectedOfferedApplication = new ProjectApplication(projectMock, "Comment", offeredUserMock, LocalDateTime.now(clock))
                .setOffered(true);

        given(applicationRepoMock.findByUserAndId(offeredUserMock, applicationId)).willReturn(Optional.of(application));

        // when
        var offeredApplication = applicationService.offerApplication(offeringUserMock, offeredUserMock, applicationId);

        // then
        assertThat(offeredApplication).isEqualTo(expectedOfferedApplication);

        verify(applicationRepoMock).save(offeredApplication);
        verify(applicationOfferedEventHandlerMock).onApplicationOffered(offeringUserMock, offeredApplication);
    }

}
