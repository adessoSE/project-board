package de.adesso.projectboard.ad.project.service;

import de.adesso.projectboard.base.application.persistence.ProjectApplicationRepository;
import de.adesso.projectboard.base.exceptions.ProjectNotFoundException;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RepositoryProjectServiceTest {

    private final String PROJECT_ID = "project";

    @Mock
    private ProjectRepository projectRepo;

    @Mock
    private ProjectApplicationRepository applicationRepo;

    @Mock
    private UserRepository userRepo;

    @Mock
    private UserService userService;

    @Mock
    private Project projectMock;

    @Mock
    private User userMock;

    private Clock clock;

    private RepositoryProjectService projectService;

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
    public void save() {
        // given
        given(projectRepo.save(projectMock)).willReturn(projectMock);

        // when
        Project savedProject = projectService.save(projectMock);

        // then
        assertThat(savedProject).isEqualTo(projectMock);

        verify(projectRepo).save(projectMock);
    }

    @Test
    public void saveAll() {
        // given
        List<Project> expectedProjects = Collections.singletonList(projectMock);

        given(projectRepo.saveAll(expectedProjects)).willReturn(expectedProjects);

        // when
        List<Project> savedProjects = projectService.saveAll(expectedProjects);

        // then
        assertThat(savedProjects).isEqualTo(expectedProjects);

        verify(projectRepo).saveAll(expectedProjects);
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
        String expectedDayRate = "Day Rate";
        String expectedTravelCostsCompensated = "Compensated";

        Project project = new Project("Other ID", expectedStatus, expectedIssueType, expectedTitle, expectedLabels, expectedJob, expectedSkills,
                expectedDescription, expectedLob, expectedCustomer,
                expectedLocation, expectedOperationStart, expectedOperationEnd,
                expectedEffort, null, null, expectedFreelancer, expectedElongation, expectedOther, expectedDayRate, expectedTravelCostsCompensated);

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

        softly.assertAll();
    }

}
