package de.adesso.projectboard.project.service;

import de.adesso.projectboard.base.application.persistence.ProjectApplication;
import de.adesso.projectboard.base.application.persistence.ProjectApplicationRepository;
import de.adesso.projectboard.base.exceptions.ProjectNotEditableException;
import de.adesso.projectboard.base.exceptions.ProjectNotFoundException;
import de.adesso.projectboard.base.project.persistence.Project;
import de.adesso.projectboard.base.project.persistence.ProjectOrigin;
import de.adesso.projectboard.base.project.persistence.ProjectRepository;
import de.adesso.projectboard.base.user.persistence.User;
import de.adesso.projectboard.base.user.persistence.UserRepository;
import de.adesso.projectboard.base.user.persistence.data.UserData;
import de.adesso.projectboard.base.user.service.UserService;
import org.assertj.core.api.SoftAssertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RepositoryProjectServiceTest {

    private final String PROJECT_ID = "project";

    private final String USER_ID = "user";

    @Mock
    ProjectRepository projectRepo;

    @Mock
    ProjectApplicationRepository applicationRepo;

    @Mock
    UserRepository userRepo;

    @Mock
    UserService userService;

    @Mock
    Project projectMock;

    @Mock
    User userMock;

    Clock clock;

    RepositoryProjectService projectService;

    @Before
    public void setUp() {
        Instant instant = Instant.parse("2018-01-01T10:00:00.00Z");
        ZoneId zoneId = ZoneId.systemDefault();

        this.clock = Clock.fixed(instant, zoneId);
        this.projectService = new RepositoryProjectService(projectRepo, applicationRepo, userRepo, userService, clock);
    }

    @Test
    public void getProjectByIdReturnsProjectForExistingProjectForId() {
        // given
        given(projectRepo.findById(PROJECT_ID)).willReturn(Optional.of(projectMock));

        // when
        Project actualProject = projectService.getProjectById(PROJECT_ID);

        // then
        assertThat(actualProject).isEqualTo(projectMock);
    }

    @Test
    public void getProjectByIdThrowsExceptionForNotExistingProjectForId() {
        // given
        when(projectRepo.findById(PROJECT_ID)).thenReturn(Optional.empty());

        // when
        assertThatThrownBy(() -> projectService.getProjectById(PROJECT_ID))
                .isInstanceOf(ProjectNotFoundException.class);
    }

    @Test
    public void projectExistsReturnsTrueForExistingProjectForId() {
        // given
        when(projectRepo.existsById(PROJECT_ID)).thenReturn(true);

        // when
        boolean actualExists = projectService.projectExists(PROJECT_ID);

        // then
        assertThat(actualExists).isTrue();
    }

    @Test
    public void projectExistsReturnsFalseForNotExistingProjectForId() {
        // given
        when(projectRepo.existsById(PROJECT_ID)).thenReturn(false);

        // when
        boolean actualExists = projectService.projectExists(PROJECT_ID);

        // then
        assertThat(actualExists).isFalse();
    }

    @Test
    public void updateProjectThrowsExceptionForNotEditableExistingProjectForId() {
        // given
        given(projectRepo.findById(PROJECT_ID)).willReturn(Optional.of(projectMock));
        given(projectMock.getOrigin()).willReturn(ProjectOrigin.JIRA);

        // when
        assertThatThrownBy(() -> projectService.updateProject(new Project(), PROJECT_ID))
                .isInstanceOf(ProjectNotEditableException.class);
    }

    @Test
    public void updateProjectThrowsExceptionForNotExistingProjectForId() {
        // given
        given(projectRepo.findById(PROJECT_ID)).willReturn(Optional.empty());

        // when
        assertThatThrownBy(() -> projectService.updateProject(new Project(), PROJECT_ID))
                .isInstanceOf(ProjectNotFoundException.class);
    }

    @Test
    public void updateProjectUpdatesProjectForExistingEditableProjectForId() {
        // given
        String expectedStatus = "Status";
        String expectedIssueType = "Issue Type";
        String expectedTitle = "Title";
        List<String> expectedLabels = Arrays.asList("Label 1", "Label 2");
        String expectedJob = "Job";
        String expectedSkills = "Skills";
        String expectedDescription = "Description";
        String expectedLob = "LOB Test";
        String expectedCustomer = "Customer";
        String expectedLocation = "Anywhere";
        String expectedOperationStart = "Maybe tomorrow";
        String expectedOperationEnd = "Maybe never";
        String expectedEffort = "100h per week";
        String expectedFreelancer = "Yup";
        String expectedElongation = "Nope";
        String expectedOther = "Other stuff";
        ProjectOrigin expectedOrigin = ProjectOrigin.CUSTOM;
        LocalDateTime expectedCreatedTime = LocalDateTime.of(2018, 1, 1, 12, 0);

        Project project = new Project("Other ID", expectedStatus, expectedIssueType, expectedTitle, expectedLabels, expectedJob, expectedSkills,
                expectedDescription, expectedLob, expectedCustomer,
                expectedLocation, expectedOperationStart, expectedOperationEnd,
                expectedEffort, null, null, expectedFreelancer, expectedElongation, expectedOther, ProjectOrigin.JIRA);

        given(projectRepo.findById(PROJECT_ID)).willReturn(Optional.of(projectMock));
        given(projectMock.getOrigin()).willReturn(ProjectOrigin.CUSTOM);
        given(projectMock.getId()).willReturn(PROJECT_ID);
        given(projectMock.getCreated()).willReturn(expectedCreatedTime);

        given(projectRepo.save(project)).willReturn(project);

        // when
        Project updatedProject = projectService.updateProject(project, PROJECT_ID);

        // then
        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(updatedProject.getId()).isEqualTo(PROJECT_ID);
        softly.assertThat(updatedProject.getStatus()).isEqualTo(expectedStatus);
        softly.assertThat(updatedProject.getIssuetype()).isEqualTo(expectedIssueType);
        softly.assertThat(updatedProject.getTitle()).isEqualTo(expectedTitle);
        softly.assertThat(updatedProject.getLabels()).isEqualTo(expectedLabels);
        softly.assertThat(updatedProject.getJob()).isEqualTo(expectedJob);
        softly.assertThat(updatedProject.getSkills()).isEqualTo(expectedSkills);
        softly.assertThat(updatedProject.getDescription()).isEqualTo(expectedDescription);
        softly.assertThat(updatedProject.getLob()).isEqualTo(expectedLob);
        softly.assertThat(updatedProject.getCustomer()).isEqualTo(expectedCustomer);
        softly.assertThat(updatedProject.getLocation()).isEqualTo(expectedLocation);
        softly.assertThat(updatedProject.getOperationStart()).isEqualTo(expectedOperationStart);
        softly.assertThat(updatedProject.getOperationEnd()).isEqualTo(expectedOperationEnd);
        softly.assertThat(updatedProject.getEffort()).isEqualTo(expectedEffort);
        softly.assertThat(updatedProject.getFreelancer()).isEqualTo(expectedFreelancer);
        softly.assertThat(updatedProject.getElongation()).isEqualTo(expectedElongation);
        softly.assertThat(updatedProject.getOther()).isEqualTo(expectedOther);
        softly.assertThat(updatedProject.getCreated()).isEqualTo(expectedCreatedTime);
        softly.assertThat(updatedProject.getUpdated()).isEqualTo(LocalDateTime.now(clock));
        softly.assertThat(updatedProject.getOrigin()).isEqualTo(expectedOrigin);

        softly.assertAll();
    }

    @Test
    public void createProject() {
        // given
        String expectedStatus = "Status";
        String expectedIssueType = "Issue Type";
        String expectedTitle = "Title";
        List<String> expectedLabels = Arrays.asList("Label 1", "Label 2");
        String expectedJob = "Job";
        String expectedSkills = "Skills";
        String expectedDescription = "Description";
        String expectedLob = "LOB Test";
        String expectedCustomer = "Customer";
        String expectedLocation = "Anywhere";
        String expectedOperationStart = "Maybe tomorrow";
        String expectedOperationEnd = "Maybe never";
        String expectedEffort = "100h per week";
        String expectedFreelancer = "Yup";
        String expectedElongation = "Nope";
        String expectedOther = "Other stuff";
        ProjectOrigin expectedOrigin = ProjectOrigin.CUSTOM;

        Project project = new Project("Other ID", expectedStatus, expectedIssueType, expectedTitle, expectedLabels, expectedJob, expectedSkills,
                expectedDescription, expectedLob, expectedCustomer,
                expectedLocation, expectedOperationStart, expectedOperationEnd,
                expectedEffort, null, null, expectedFreelancer, expectedElongation, expectedOther, ProjectOrigin.JIRA);

        given(projectRepo.save(project)).willReturn(project);

        // when
        Project createdProject = projectService.createProject(project);

        // then
        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(createdProject.getStatus()).isEqualTo(expectedStatus);
        softly.assertThat(createdProject.getIssuetype()).isEqualTo(expectedIssueType);
        softly.assertThat(createdProject.getTitle()).isEqualTo(expectedTitle);
        softly.assertThat(createdProject.getLabels()).isEqualTo(expectedLabels);
        softly.assertThat(createdProject.getJob()).isEqualTo(expectedJob);
        softly.assertThat(createdProject.getSkills()).isEqualTo(expectedSkills);
        softly.assertThat(createdProject.getDescription()).isEqualTo(expectedDescription);
        softly.assertThat(createdProject.getLob()).isEqualTo(expectedLob);
        softly.assertThat(createdProject.getCustomer()).isEqualTo(expectedCustomer);
        softly.assertThat(createdProject.getLocation()).isEqualTo(expectedLocation);
        softly.assertThat(createdProject.getOperationStart()).isEqualTo(expectedOperationStart);
        softly.assertThat(createdProject.getOperationEnd()).isEqualTo(expectedOperationEnd);
        softly.assertThat(createdProject.getEffort()).isEqualTo(expectedEffort);
        softly.assertThat(createdProject.getFreelancer()).isEqualTo(expectedFreelancer);
        softly.assertThat(createdProject.getElongation()).isEqualTo(expectedElongation);
        softly.assertThat(createdProject.getOther()).isEqualTo(expectedOther);
        softly.assertThat(createdProject.getCreated()).isEqualTo(LocalDateTime.now(clock));
        softly.assertThat(createdProject.getUpdated()).isEqualTo(LocalDateTime.now(clock));
        softly.assertThat(createdProject.getOrigin()).isEqualTo(expectedOrigin);

        softly.assertAll();
    }

    @Test
    public void deleteProjectByIdThrowsExceptionForNotEditableExistingProjectForId() {
        // given
        given(projectRepo.findById(PROJECT_ID)).willReturn(Optional.of(projectMock));
        given(projectMock.getOrigin()).willReturn(ProjectOrigin.JIRA);

        // when
        assertThatThrownBy(() -> projectService.deleteProjectById(PROJECT_ID))
                .isInstanceOf(ProjectNotEditableException.class);
    }

    @Test
    public void deleteProjectByIdDeletesProjectForId() {
        // given
        ProjectApplication applicationMock = mock(ProjectApplication.class);
        given(applicationMock.getUser()).willReturn(userMock);

        given(projectMock.getOrigin()).willReturn(ProjectOrigin.CUSTOM);
        given(projectRepo.findById(PROJECT_ID)).willReturn(Optional.of(projectMock));

        given(userRepo.findAllByOwnedProjectsContaining(projectMock)).willReturn(Collections.singletonList(userMock));
        given(userRepo.findAllByBookmarksContaining(projectMock)).willReturn(Collections.singletonList(userMock));
        given(applicationRepo.findAllByProjectEquals(projectMock)).willReturn(Collections.singletonList(applicationMock));

        // when
        projectService.deleteProjectById(PROJECT_ID);

        // then
        verify(userMock).removeOwnedProject(projectMock);
        verify(userMock).removeBookmark(projectMock);
        verify(userMock).removeApplication(applicationMock);
        verify(userService, atLeastOnce()).save(userMock);

        verify(projectRepo).delete(projectMock);
    }

    @Test
    public void getProjectsForUserReturnsProjectsForManagerWhenUserIsManager() {
        // given
        Sort sort = Sort.unsorted();

        given(userService.userIsManager(userMock)).willReturn(true);

        // when
        projectService.getProjectsForUser(userMock, sort);

        // then
        verify(projectRepo).findAllForManager(sort);
    }

    @Test
    public void getProjectsForUserReturnsProjectsForUserWhenUserIsNotAManager() {
        // given
        String expectedLob = "LOB Test";
        Sort sort = Sort.unsorted();

        UserData userDataMock = mock(UserData.class);
        given(userDataMock.getLob()).willReturn(expectedLob);

        given(userService.userIsManager(userMock)).willReturn(false);
        given(userService.getUserData(userMock)).willReturn(userDataMock);

        // when
        projectService.getProjectsForUser(userMock, sort);

        // then
        verify(projectRepo).findAllForUser(expectedLob, sort);
    }

    @Test
    public void searchProjectsForUserReturnsProjectsForManagerWhenUserIsManager() {
        // given
        String expectedKeyword = "Keyword";
        Sort sort = Sort.unsorted();

        given(userService.userIsManager(userMock)).willReturn(true);

        // when
        projectService.searchProjectsForUser(userMock, expectedKeyword, sort);

        // then
        verify(projectRepo).findAllForManagerByKeyword(expectedKeyword, sort);
    }

    @Test
    public void searchProjectsForUserReturnsProjectsForUserWhenUserIsNotAManager() {
        // given
        String expectedKeyword = "Keyword";
        String expectedLob = "LOB Test";
        Sort sort = Sort.unsorted();

        UserData userDataMock = mock(UserData.class);
        given(userDataMock.getLob()).willReturn(expectedLob);

        given(userService.userIsManager(userMock)).willReturn(false);
        given(userService.getUserData(userMock)).willReturn(userDataMock);

        // when
        projectService.searchProjectsForUser(userMock, expectedKeyword, sort);

        // then
        verify(projectRepo).findAllForUserByKeyword(expectedLob, expectedKeyword, sort);
    }

    @Test
    public void userOwnsProject() {
        // given
        given(userMock.getId()).willReturn(USER_ID);

        // when
        projectService.userOwnsProject(userMock, projectMock);

        // then
        verify(userRepo).existsByIdAndOwnedProjectsContaining(USER_ID, projectMock);
    }

    @Test
    public void createProjectForUser() {
        // given
        String expectedStatus = "Status";
        String expectedIssueType = "Issue Type";
        String expectedTitle = "Title";
        List<String> expectedLabels = Arrays.asList("Label 1", "Label 2");
        String expectedJob = "Job";
        String expectedSkills = "Skills";
        String expectedDescription = "Description";
        String expectedLob = "LOB Test";
        String expectedCustomer = "Customer";
        String expectedLocation = "Anywhere";
        String expectedOperationStart = "Maybe tomorrow";
        String expectedOperationEnd = "Maybe never";
        String expectedEffort = "100h per week";
        String expectedFreelancer = "Yup";
        String expectedElongation = "Nope";
        String expectedOther = "Other stuff";
        ProjectOrigin expectedOrigin = ProjectOrigin.CUSTOM;

        Project project = new Project("Other ID", expectedStatus, expectedIssueType, expectedTitle, expectedLabels, expectedJob, expectedSkills,
                expectedDescription, expectedLob, expectedCustomer,
                expectedLocation, expectedOperationStart, expectedOperationEnd,
                expectedEffort, null, null, expectedFreelancer, expectedElongation, expectedOther, ProjectOrigin.JIRA);

        given(projectRepo.save(project)).willReturn(project);

        // when
        Project createdProject = projectService.createProjectForUser(project, userMock);

        // then
        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(createdProject.getStatus()).isEqualTo(expectedStatus);
        softly.assertThat(createdProject.getIssuetype()).isEqualTo(expectedIssueType);
        softly.assertThat(createdProject.getTitle()).isEqualTo(expectedTitle);
        softly.assertThat(createdProject.getLabels()).isEqualTo(expectedLabels);
        softly.assertThat(createdProject.getJob()).isEqualTo(expectedJob);
        softly.assertThat(createdProject.getSkills()).isEqualTo(expectedSkills);
        softly.assertThat(createdProject.getDescription()).isEqualTo(expectedDescription);
        softly.assertThat(createdProject.getLob()).isEqualTo(expectedLob);
        softly.assertThat(createdProject.getCustomer()).isEqualTo(expectedCustomer);
        softly.assertThat(createdProject.getLocation()).isEqualTo(expectedLocation);
        softly.assertThat(createdProject.getOperationStart()).isEqualTo(expectedOperationStart);
        softly.assertThat(createdProject.getOperationEnd()).isEqualTo(expectedOperationEnd);
        softly.assertThat(createdProject.getEffort()).isEqualTo(expectedEffort);
        softly.assertThat(createdProject.getFreelancer()).isEqualTo(expectedFreelancer);
        softly.assertThat(createdProject.getElongation()).isEqualTo(expectedElongation);
        softly.assertThat(createdProject.getOther()).isEqualTo(expectedOther);
        softly.assertThat(createdProject.getCreated()).isEqualTo(LocalDateTime.now(clock));
        softly.assertThat(createdProject.getUpdated()).isEqualTo(LocalDateTime.now(clock));
        softly.assertThat(createdProject.getOrigin()).isEqualTo(expectedOrigin);

        softly.assertAll();

        verify(userMock).addOwnedProject(project);
        verify(userService).save(userMock);
    }

    @Test
    public void addProjectToUser() {
        // given

        // when
        projectService.addProjectToUser(userMock, projectMock);

        // then
        verify(userMock).addOwnedProject(projectMock);
        verify(userService).save(userMock);
    }

    @Test
    public void getProjectsForUserPaginatedReturnsProjectsForManagerWhenUserIsManager() {
        // given
        Pageable pageable = PageRequest.of(1, 10);

        given(userService.userIsManager(userMock)).willReturn(true);

        // when
        projectService.getProjectsForUserPaginated(userMock, pageable);

        // then
        verify(projectRepo).findAllForManagerPageable(pageable);
    }

    @Test
    public void getProjectsForUserPaginatedReturnsProjectsForUserWhenUserIsNotAManager() {
        // given
        String expectedLob = "LOB Test";
        Pageable pageable = PageRequest.of(1, 10);

        UserData userDataMock = mock(UserData.class);
        given(userDataMock.getLob()).willReturn(expectedLob);

        given(userService.getUserData(userMock)).willReturn(userDataMock);
        given(userService.userIsManager(userMock)).willReturn(false);

        // when
        projectService.getProjectsForUserPaginated(userMock, pageable);

        // then
        verify(projectRepo).findAllForUserPageable(expectedLob, pageable);
    }

    @Test
    public void searchProjectsForUserPaginatedReturnsProjectsForManagerWhenUserIsManager() {
        // given
        String expectedKeyword = "Keyword";
        Pageable pageable = PageRequest.of(2, 12);

        given(userService.userIsManager(userMock)).willReturn(true);

        // when
        projectService.searchProjectsForUserPaginated(expectedKeyword, userMock, pageable);

        // then
        verify(projectRepo).findAllForManagerByKeywordPageable(expectedKeyword, pageable);
    }

    @Test
    public void searchProjectsForUserPaginatedReturnsProjectsForUserWhenUserIsNotAManager() {
        // given
        String expectedLob = "LOB Test";
        String expectedKeyword = "Keyword";
        Pageable pageable = PageRequest.of(2, 12);

        UserData userDataMock = mock(UserData.class);
        given(userDataMock.getLob()).willReturn(expectedLob);

        given(userService.getUserData(userMock)).willReturn(userDataMock);
        given(userService.userIsManager(userMock)).willReturn(false);

        // when
        projectService.searchProjectsForUserPaginated(expectedKeyword, userMock, pageable);

        // then
        verify(projectRepo).findAllForUserByKeywordPageable(expectedLob, expectedKeyword, pageable);
    }

}