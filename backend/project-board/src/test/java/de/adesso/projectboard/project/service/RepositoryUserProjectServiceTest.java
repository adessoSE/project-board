package de.adesso.projectboard.project.service;

import de.adesso.projectboard.base.project.persistence.Project;
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
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class RepositoryUserProjectServiceTest {

    private final String USER_ID = "user";

    @Mock
    private UserService userService;

    @Mock
    private ProjectRepository projectRepo;

    @Mock
    private UserRepository userRepo;

    @Mock
    private RepositoryProjectService projectService;

    @Mock
    private User userMock;

    @Mock
    private Project projectMock;

    private Clock clock;

    private RepositoryUserProjectService userProjectService;

    @Before
    public void setUp() {
        Instant instant = Instant.parse("2018-01-01T10:00:00.00Z");
        ZoneId zoneId = ZoneId.systemDefault();

        this.clock = Clock.fixed(instant, zoneId);
        this.userProjectService
                = new RepositoryUserProjectService(userService, projectRepo, userRepo, projectService);
    }

    @Test
    public void getProjectsForUserReturnsProjectsForManagerWhenUserIsManager() {
        // given
        Sort sort = Sort.unsorted();

        given(userService.userIsManager(userMock)).willReturn(true);

        // when
        userProjectService.getProjectsForUser(userMock, sort);

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
        userProjectService.getProjectsForUser(userMock, sort);

        // then
        verify(projectRepo).findAllForUser(expectedLob, sort);
    }

    @Test
    public void getProjectsForUserReturnsProjectsForManagerWhenUserIsNotAManagerButAnAdmin() {
        // given
        Sort sort = Sort.unsorted();

        given(userService.getAuthenticatedUser()).willReturn(userMock);
        given(userService.authenticatedUserIsAdmin()).willReturn(true);
        given(userService.userIsManager(userMock)).willReturn(false);

        // when
        userProjectService.getProjectsForUser(userMock, sort);

        // then
        verify(projectRepo).findAllForManager(sort);
    }

    @Test
    public void getProjectsForUserReturnsProjectsForUserWhenUserIsNotAManagerAndNoAdmin() {
        // given
        String expectedLob = "LOB Test";
        Sort sort = Sort.unsorted();

        UserData userDataMock = mock(UserData.class);
        given(userDataMock.getLob()).willReturn(expectedLob);
        given(userService.getUserData(userMock)).willReturn(userDataMock);

        given(userService.getAuthenticatedUser()).willReturn(userMock);
        given(userService.authenticatedUserIsAdmin()).willReturn(false);
        given(userService.userIsManager(userMock)).willReturn(false);

        // when
        userProjectService.getProjectsForUser(userMock, sort);

        // then
        verify(projectRepo).findAllForUser(expectedLob, sort);
    }

    @Test
    public void searchProjectsForUserReturnsProjectsForManagerWhenUserIsAManager() {
        // given
        String expectedKeyword = "Keyword";
        Sort sort = Sort.unsorted();

        given(userService.userIsManager(userMock)).willReturn(true);

        // when
        userProjectService.searchProjectsForUser(userMock, expectedKeyword, sort);

        // then
        verify(projectRepo).findAllForManagerByKeyword(expectedKeyword, sort);
    }

    @Test
    public void searchProjectsForUserReturnsProjectsForUserWhenUserIsNotAManagerAndNoAdmin() {
        // given
        String expectedKeyword = "Keyword";
        String expectedLob = "LOB Test";
        Sort sort = Sort.unsorted();

        UserData userDataMock = mock(UserData.class);
        given(userDataMock.getLob()).willReturn(expectedLob);

        given(userService.userIsManager(userMock)).willReturn(false);
        given(userService.getUserData(userMock)).willReturn(userDataMock);

        given(userService.getAuthenticatedUser()).willReturn(userMock);
        given(userService.authenticatedUserIsAdmin()).willReturn(false);

        // when
        userProjectService.searchProjectsForUser(userMock, expectedKeyword, sort);

        // then
        verify(projectRepo).findAllForUserByKeyword(expectedLob, expectedKeyword, sort);
    }

    @Test
    public void userOwnsProject() {
        // given
        given(userMock.getId()).willReturn(USER_ID);

        // when
        userProjectService.userOwnsProject(userMock, projectMock);

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
        LocalDateTime expectedCreated = LocalDateTime.now(clock);
        LocalDateTime expectedUpdated = LocalDateTime.now(clock);
        String expectedDayRate = "Test Rate";
        String expectedTravelCostsCompensated = "Nope";
        Project.Origin expectedOrigin = Project.Origin.CUSTOM;

        Project project = new Project("Other ID", expectedStatus, expectedIssueType, expectedTitle, expectedLabels, expectedJob, expectedSkills,
                expectedDescription, expectedLob, expectedCustomer,
                expectedLocation, expectedOperationStart, expectedOperationEnd,
                expectedEffort, null, null, expectedFreelancer, expectedElongation, expectedOther, expectedDayRate,
                expectedTravelCostsCompensated, Project.Origin.JIRA);

        given(projectService.createProject(project)).willReturn(project
                .setCreated(expectedCreated)
                .setUpdated(expectedUpdated)
                .setOrigin(Project.Origin.CUSTOM));

        // when
        Project createdProject = userProjectService.createProjectForUser(project, userMock);

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
        softly.assertThat(createdProject.getCreated()).isEqualTo(expectedCreated);
        softly.assertThat(createdProject.getUpdated()).isEqualTo(expectedUpdated);
        softly.assertThat(createdProject.getOrigin()).isEqualTo(expectedOrigin);

        softly.assertAll();

        verify(userMock).addOwnedProject(project);
        verify(userService).save(userMock);
    }

    @Test
    public void addProjectToUser() {
        // given

        // when
        userProjectService.addProjectToUser(userMock, projectMock);

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
        userProjectService.getProjectsForUserPaginated(userMock, pageable);

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
        userProjectService.getProjectsForUserPaginated(userMock, pageable);

        // then
        verify(projectRepo).findAllForUserPageable(expectedLob, pageable);
    }

    @Test
    public void getProjectsForUserPaginatedReturnsProjectsForManagerWhenUserIsNotAManagerButAnAdmin() {
        // given
        Pageable pageable = PageRequest.of(1, 10);

        given(userService.getAuthenticatedUser()).willReturn(userMock);
        given(userService.authenticatedUserIsAdmin()).willReturn(true);

        // when
        userProjectService.getProjectsForUserPaginated(userMock, pageable);

        // then
        verify(projectRepo).findAllForManagerPageable(pageable);
    }

    @Test
    public void getProjectsForUserPaginatedReturnsProjectsForUserWhenUserIsNotAManagerAndNoAdmin() {
        // given
        String expectedLob = "LOB Test";
        Pageable pageable = PageRequest.of(1, 10);

        UserData userDataMock = mock(UserData.class);
        given(userDataMock.getLob()).willReturn(expectedLob);

        given(userService.getUserData(userMock)).willReturn(userDataMock);
        given(userService.userIsManager(userMock)).willReturn(false);

        given(userService.getAuthenticatedUser()).willReturn(userMock);
        given(userService.authenticatedUserIsAdmin()).willReturn(false);

        // when
        userProjectService.getProjectsForUserPaginated(userMock, pageable);

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
        userProjectService.searchProjectsForUserPaginated(expectedKeyword, userMock, pageable);

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
        userProjectService.searchProjectsForUserPaginated(expectedKeyword, userMock, pageable);

        // then
        verify(projectRepo).findAllForUserByKeywordPageable(expectedLob, expectedKeyword, pageable);
    }

    @Test
    public void searchProjectsForUserPaginatedReturnsProjectsForManagerWhenUserIsNotAManagerButAnAdmin() {
        // given
        String expectedKeyword = "Keyword";
        Pageable pageable = PageRequest.of(2, 12);

        given(userService.userIsManager(userMock)).willReturn(false);

        given(userService.getAuthenticatedUser()).willReturn(userMock);
        given(userService.authenticatedUserIsAdmin()).willReturn(true);

        // when
        userProjectService.searchProjectsForUserPaginated(expectedKeyword, userMock, pageable);

        // then
        verify(projectRepo).findAllForManagerByKeywordPageable(expectedKeyword, pageable);
    }

    @Test
    public void searchProjectsForUserPaginatedReturnsProjectsForUserWhenUserIsNotAManagerAndNotAnAdmin() {
        // given
        String expectedLob = "LOB Test";
        String expectedKeyword = "Keyword";
        Pageable pageable = PageRequest.of(2, 12);

        UserData userDataMock = mock(UserData.class);
        given(userDataMock.getLob()).willReturn(expectedLob);

        given(userService.getUserData(userMock)).willReturn(userDataMock);
        given(userService.userIsManager(userMock)).willReturn(false);

        given(userService.getAuthenticatedUser()).willReturn(userMock);
        given(userService.authenticatedUserIsAdmin()).willReturn(false);

        // when
        userProjectService.searchProjectsForUserPaginated(expectedKeyword, userMock, pageable);

        // then
        verify(projectRepo).findAllForUserByKeywordPageable(expectedLob, expectedKeyword, pageable);
    }

}