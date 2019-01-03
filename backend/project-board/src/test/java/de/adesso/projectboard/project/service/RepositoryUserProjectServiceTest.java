package de.adesso.projectboard.project.service;

import de.adesso.projectboard.base.project.persistence.Project;
import de.adesso.projectboard.base.project.persistence.ProjectRepository;
import de.adesso.projectboard.base.user.persistence.User;
import de.adesso.projectboard.base.user.persistence.UserRepository;
import de.adesso.projectboard.base.user.service.UserService;
import org.assertj.core.api.SoftAssertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class RepositoryUserProjectServiceTest {

    private final String USER_ID = "user";

    @Mock
    private UserService userServiceMock;

    @Mock
    private ProjectRepository projectRepoMock;

    @Mock
    private UserRepository userRepoMock;

    @Mock
    private RepositoryProjectService projectServiceMock;

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
                = new RepositoryUserProjectService(userServiceMock, projectRepoMock, userRepoMock, projectServiceMock);
    }

    @Test
    public void getProjectsForUser() {
        // given
        var expectedProjects = Collections.singletonList(projectMock);
        var sort = Sort.unsorted();

        given(projectRepoMock.findAllByStatusEscalatedOrOpen(sort))
                .willReturn(expectedProjects);

        // when
        var actualProjects = userProjectService.getProjectsForUser(userMock, sort);

        // then
        assertThat(actualProjects).isEqualTo(expectedProjects);

        verify(projectRepoMock).findAllByStatusEscalatedOrOpen(sort);
    }

    @Test
    public void searchProjectsForUser() {
        // given
        var keyword = "Test!";
        var expectedProjects = Collections.singletonList(projectMock);
        var sort = Sort.unsorted();

        given(projectRepoMock.findAllByStatusEscalatedOrOpenAndKeyword(keyword, sort))
                .willReturn(expectedProjects);

        // when
        var actualProjects = userProjectService.searchProjectsForUser(userMock, keyword, sort);

        // then
        assertThat(actualProjects).isEqualTo(expectedProjects);

        verify(projectRepoMock).findAllByStatusEscalatedOrOpenAndKeyword(keyword, sort);
    }

    @Test
    public void userOwnsProject() {
        // given
        given(userMock.getId()).willReturn(USER_ID);

        // when
        userProjectService.userOwnsProject(userMock, projectMock);

        // then
        verify(userRepoMock).existsByIdAndOwnedProjectsContaining(USER_ID, projectMock);
    }

    @Test
    public void createProjectForUser() {
        // given
        var expectedStatus = "Status";
        var expectedIssueType = "Issue Type";
        var expectedTitle = "Title";
        var expectedLabels = Arrays.asList("Label 1", "Label 2");
        var expectedJob = "Job";
        var expectedSkills = "Skills";
        var expectedDescription = "Description";
        var expectedLob = "LOB Test";
        var expectedCustomer = "Customer";
        var expectedLocation = "Anywhere";
        var expectedOperationStart = "Maybe tomorrow";
        var expectedOperationEnd = "Maybe never";
        var expectedEffort = "100h per week";
        var expectedFreelancer = "Yup";
        var expectedElongation = "Nope";
        var expectedOther = "Other stuff";
        var expectedCreated = LocalDateTime.now(clock);
        var expectedUpdated = LocalDateTime.now(clock);
        var expectedDayRate = "Test Rate";
        var expectedTravelCostsCompensated = "Nope";
        var expectedOrigin = Project.Origin.CUSTOM;

        var project = new Project("Other ID", expectedStatus, expectedIssueType, expectedTitle, expectedLabels, expectedJob, expectedSkills,
                expectedDescription, expectedLob, expectedCustomer,
                expectedLocation, expectedOperationStart, expectedOperationEnd,
                expectedEffort, null, null, expectedFreelancer, expectedElongation, expectedOther, expectedDayRate,
                expectedTravelCostsCompensated, Project.Origin.JIRA);

        given(projectServiceMock.createProject(project)).willReturn(project
                .setCreated(expectedCreated)
                .setUpdated(expectedUpdated)
                .setOrigin(Project.Origin.CUSTOM));

        // when
        var createdProject = userProjectService.createProjectForUser(project, userMock);

        // then
        var softly = new SoftAssertions();

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
        verify(userServiceMock).save(userMock);
    }

    @Test
    public void addProjectToUser() {
        // given

        // when
        userProjectService.addProjectToUser(userMock, projectMock);

        // then
        verify(userMock).addOwnedProject(projectMock);
        verify(userServiceMock).save(userMock);
    }

    @Test
    public void getProjectsForUserPaginated() {
        // given
        var expectedProjects = Collections.singletonList(projectMock);
        var pageable = PageRequest.of(0, 100);
        var expectedPage = new PageImpl<>(expectedProjects);

        given(projectRepoMock.findAllByStatusEscalatedOrOpenPageable(pageable))
                .willReturn(expectedPage);

        // when
        var actualProjects = userProjectService.getProjectsForUserPaginated(userMock, pageable);

        // then
        assertThat(actualProjects).isEqualTo(expectedPage);

        verify(projectRepoMock).findAllByStatusEscalatedOrOpenPageable(pageable);
    }

    @Test
    public void searchProjectsForUserPaginated() {
        // given
        var keyword = "Keyword!";
        var expectedProjects = Collections.singletonList(projectMock);
        var pageable = PageRequest.of(0, 100);
        var expectedPage = new PageImpl<>(expectedProjects);

        given(projectRepoMock.findAllByStatusEscalatedOrOpenAndKeywordPageable(keyword, pageable))
                .willReturn(expectedPage);

        // when
        var actualProjects = userProjectService.searchProjectsForUserPaginated(keyword, userMock, pageable);

        // then
        assertThat(actualProjects).isEqualTo(expectedPage);

        verify(projectRepoMock).findAllByStatusEscalatedOrOpenAndKeywordPageable(keyword, pageable);
    }

}