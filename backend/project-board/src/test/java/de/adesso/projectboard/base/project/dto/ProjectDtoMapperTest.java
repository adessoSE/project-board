package de.adesso.projectboard.base.project.dto;

import de.adesso.projectboard.base.project.persistence.Project;
import org.assertj.core.api.SoftAssertions;
import org.junit.Before;
import org.junit.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;

public class ProjectDtoMapperTest {

    private ProjectDtoMapper mapper;

    private Clock clock;

    @Before
    public void setUp() {
        Instant instant = Instant.parse("2018-01-01T10:00:00.00Z");
        ZoneId zoneId = ZoneId.systemDefault();

        this.clock = Clock.fixed(instant, zoneId);
        this.mapper = new ProjectDtoMapper(clock);
    }

    @Test
    public void toProject() {
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

        ProjectRequestDTO dto = new ProjectRequestDTO(expectedStatus, expectedIssueType, expectedTitle,
                expectedLabels, expectedJob, expectedSkills,
                expectedDescription, expectedLob, expectedCustomer,
                expectedLocation, expectedOperationStart, expectedOperationEnd,
                expectedEffort, expectedFreelancer, expectedElongation, expectedOther);

        // when
        Project createdProject = mapper.toProject(dto);

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