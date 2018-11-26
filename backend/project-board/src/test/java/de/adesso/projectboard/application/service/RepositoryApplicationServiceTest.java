package de.adesso.projectboard.application.service;

import de.adesso.projectboard.base.application.dto.ProjectApplicationRequestDTO;
import de.adesso.projectboard.base.application.persistence.ProjectApplication;
import de.adesso.projectboard.base.application.persistence.ProjectApplicationRepository;
import de.adesso.projectboard.base.exceptions.AlreadyAppliedException;
import de.adesso.projectboard.base.project.persistence.Project;
import de.adesso.projectboard.base.project.service.ProjectService;
import de.adesso.projectboard.base.user.persistence.User;
import org.assertj.core.api.SoftAssertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RepositoryApplicationServiceTest {

    private final String PROJECT_ID = "project";

    @Mock
    ProjectService projectService;

    @Mock
    ProjectApplicationRepository applicationRepo;

    @Mock
    User userMock;

    @Mock
    Project projectMock;

    RepositoryApplicationService applicationService;

    @Before
    public void setUp() {
        this.applicationService = new RepositoryApplicationService(projectService, applicationRepo);
    }

    @Test
    public void userHasAppliedForProjectReturnsTrueWhenApplicationIsPresent() {
        // given
        given(applicationRepo.existsByUserAndProject(userMock, projectMock)).willReturn(true);

        // when
        boolean actualApplied = applicationService.userHasAppliedForProject(userMock, projectMock);

        // then
        assertThat(actualApplied).isTrue();
    }

    @Test
    public void userHasAppliedForProjectReturnsFalseWhenApplicationNotPresent() {
        // given
        given(applicationRepo.existsByUserAndProject(userMock, projectMock)).willReturn(false);

        // when
        boolean actualApplied = applicationService.userHasAppliedForProject(userMock, projectMock);

        // then
        assertThat(actualApplied).isFalse();
    }

    @Test
    public void testCreateApplicationForUser() {
        // given
        String expectedComment = "Comment!";

        ProjectApplicationRequestDTO dto = mock(ProjectApplicationRequestDTO.class);
        when(dto.getComment()).thenReturn(expectedComment);
        when(dto.getProjectId()).thenReturn(PROJECT_ID);

        given(applicationRepo.existsByUserAndProject(userMock, projectMock)).willReturn(false);
        given(projectService.getProjectById(PROJECT_ID)).willReturn(projectMock);
        given(applicationRepo.existsByUserAndProject(userMock, projectMock)).willReturn(false);

        given(applicationRepo.save(any(ProjectApplication.class))).willAnswer((Answer<ProjectApplication>) invocation -> {
            Object[] args = invocation.getArguments();

            return (ProjectApplication) args[0];
        });

        // when
        ProjectApplication createdApplication = applicationService.createApplicationForUser(userMock, dto);

        // then
        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(createdApplication.getComment()).isEqualTo(expectedComment);
        softly.assertThat(createdApplication.getUser()).isEqualTo(userMock);
        softly.assertThat(createdApplication.getProject()).isEqualTo(projectMock);

        softly.assertAll();

        verify(applicationRepo).save(createdApplication);
    }

    @Test
    public void testCreateApplicationForUserThrowsExceptionWhenAlreadyApplied() {
        // given
        ProjectApplicationRequestDTO dtoMock = mock(ProjectApplicationRequestDTO.class);
        given(dtoMock.getProjectId()).willReturn(PROJECT_ID);

        given(projectService.getProjectById(PROJECT_ID)).willReturn(projectMock);
        given(applicationRepo.existsByUserAndProject(userMock, projectMock)).willReturn(true);

        // when
        assertThatThrownBy(() -> applicationService.createApplicationForUser(userMock, dtoMock))
                .isInstanceOf(AlreadyAppliedException.class);
    }

    @Test
    public void testGetApplicationsOfUser() {
        // given
        ProjectApplication firstApplicationMock = mock(ProjectApplication.class);
        ProjectApplication secondApplicationMock = mock(ProjectApplication.class);
        Set<ProjectApplication> expectedApplications = Stream.of(firstApplicationMock, secondApplicationMock).collect(Collectors.toSet());

        given(userMock.getApplications()).willReturn(expectedApplications);

        // when
        List<ProjectApplication> actualApplications = applicationService.getApplicationsOfUser(userMock);

        // then
        assertThat(actualApplications).containsExactlyInAnyOrder(firstApplicationMock, secondApplicationMock);
    }

}