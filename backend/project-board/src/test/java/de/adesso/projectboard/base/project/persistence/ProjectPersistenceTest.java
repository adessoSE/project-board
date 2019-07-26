package de.adesso.projectboard.base.project.persistence;

import org.assertj.core.api.SoftAssertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
@TestPropertySource("classpath:application-persistence-test.properties")
public class ProjectPersistenceTest {

    @Autowired
    private ProjectRepository projectRepository;

    @Test
    public void save() {
        // given
        String expectedProjectId = "STF-1";

        LocalDateTime created = LocalDateTime.of(2018, 2, 1, 13, 37);
        LocalDateTime updated = LocalDateTime.of(2018, 2, 2, 13, 37);

        Project expectedProject = new Project()
                .setId(expectedProjectId)
                .setStatus("eskaliert")
                .setIssuetype("Issuetype")
                .setTitle("Title")
                .setLabels(Arrays.asList("Label 1", "Label 2", "Label 3"))
                .setJob("Job")
                .setSkills("Skills")
                .setDescription("Description")
                .setLob("LOB Test")
                .setCustomer("Customer")
                .setLocation("Location")
                .setOperationStart("OperationStart")
                .setOperationEnd("OperationEnd")
                .setEffort("Effort")
                .setCreated(created)
                .setUpdated(updated)
                .setFreelancer("Freelancer")
                .setElongation("Elongation")
                .setOther("Other")
                .setDailyRate("Daily Rate")
                .setTravelCostsCompensated("Travel Costs Compensated");

        // when
        projectRepository.save(expectedProject);
        Project retrievedProject = projectRepository.findById(expectedProjectId).orElseThrow();

        // then
        assertThat(retrievedProject).isEqualTo(expectedProject);
    }

    @Test
    public void idGeneratorDoesNotSetIdIfAlreadySet() {
        // given
        String expectedProjectId = "STF-1234";

        Project project = new Project()
                .setId(expectedProjectId);

        // when
        Project persistedProject = projectRepository.save(project);

        // then
        assertThat(persistedProject.getId()).isEqualTo(expectedProjectId);
    }

    @Test
    public void idGeneratorSetsIdIfNotAlreadySet() {
        // given
        String expectedProjectId = "AD-1";

        Project project = new Project();

        // when
        Project persistedProject = projectRepository.save(project);

        // then
        assertThat(persistedProject.getId()).isEqualTo(expectedProjectId);
    }

    @Test
    public void idGeneratorAutoIncrementsIds() {
        // given
        String expectedFirstId = "AD-1";
        String expectedSecondId = "AD-2";

        Project firstProject = new Project();
        Project secondProject = new Project();

        // when
        Project firstPersisted = projectRepository.save(firstProject);
        Project secondPersisted = projectRepository.save(secondProject);

        // then
        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(firstPersisted.getId()).isEqualTo(expectedFirstId);
        softly.assertThat(secondPersisted.getId()).isEqualTo(expectedSecondId);

        softly.assertAll();
    }

    @Test
    public void idGeneratorReusesIds() {
        // given
        String expectedProjectId = "AD-1";

        Project firstProject = new Project();
        Project secondProject = new Project();

        // when
        Project firstPersisted = projectRepository.save(firstProject);
        projectRepository.delete(firstPersisted);

        Project secondPersisted = projectRepository.save(secondProject);

        // then
        assertThat(secondPersisted.getId()).isEqualTo(expectedProjectId);
    }

    @Test
    public void idGeneratorIgnoresNonMatchingIds() {
        String firstProjectId = "AD-T";
        String expectedProjectId = "AD-1";

        Project firstProject = new Project()
                .setId(firstProjectId);

        Project secondProject = new Project();

        // when
        projectRepository.save(firstProject);
        Project persistedProject = projectRepository.save(secondProject);

        // then
        assertThat(persistedProject.getId()).isEqualTo(expectedProjectId);
    }

}
