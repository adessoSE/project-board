package de.adesso.projectboard.base.project.persistence;

import org.assertj.core.api.SoftAssertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

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
                .setDayRate("Day Rate")
                .setTravelCostsCompensated("Travel Costs Compensated")
                .setOrigin(Project.Origin.CUSTOM);

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

    @Test
    @Sql("classpath:de/adesso/projectboard/persistence/Projects.sql")
    public void findAllByStatusEscalatedOrOpenPageable() {
        // given
        var pageable = PageRequest.of(0, 10);

        // when
        var projects = projectRepository.findAllByStatusEscalatedOrOpenPageable(pageable)
                .getContent();

        // then
        testProjects(projects);
    }

    @Test
    @Sql("classpath:de/adesso/projectboard/persistence/Projects.sql")
    public void findAllByStatusEscalatedOrOpen() {
        // given
        var sort = Sort.unsorted();

        // when
        var projects = projectRepository.findAllByStatusEscalatedOrOpen(sort);

        // then
        testProjects(projects);
    }

    @Test
    @Sql("classpath:de/adesso/projectboard/persistence/Projects.sql")
    public void findAllByStatusEscalatedOrOpenAndKeywordPageable() {
        // given
        var keyword = "Special";
        var pageable = PageRequest.of(0, 10);

        // when
        var projects = projectRepository.findAllByStatusEscalatedOrOpenAndKeywordPageable(keyword, pageable)
                .getContent();

        // then
        testProjectsByKeyword(projects, keyword);
    }

    @Test
    @Sql("classpath:de/adesso/projectboard/persistence/Projects.sql")
    public void findAllByStatusEscalatedOrOpenAndKeyword() {
        // given
        var keyword = "Special";
        var sort = Sort.unsorted();

        // when
        var projects = projectRepository.findAllByStatusEscalatedOrOpenAndKeyword(keyword, sort);

        // then
        testProjectsByKeyword(projects, keyword);
    }

    void testProjects(List<Project> projects) {
        boolean allEscalatedOrOpen = allEscalatedOrOpen(projects);

        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(allEscalatedOrOpen).isTrue();
        softly.assertThat(projects).hasSize(7);

        softly.assertAll();
    }

    void testProjectsByKeyword(List<Project> projects, String keyword) {
        boolean allEscalatedOrOpen = allEscalatedOrOpen(projects);
        boolean allContainingKeywordInAnyField = allContainingKeywordInField(projects, keyword);

        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(allEscalatedOrOpen).isTrue();
        softly.assertThat(allContainingKeywordInAnyField).isTrue();

        softly.assertThat(projects).hasSize(4);

        softly.assertAll();
    }

    boolean allEscalatedOrOpen(List<Project> projects) {
        return projects.stream()
                .allMatch(project -> {
                    boolean isOpen = "open".equalsIgnoreCase(project.getStatus()) ||
                            "offen".equalsIgnoreCase(project.getStatus());

                    boolean isEscalated = "eskaliert".equalsIgnoreCase(project.getStatus()) ||
                            "escalated".equalsIgnoreCase(project.getStatus());

                    return isOpen || isEscalated;
                });
    }

    boolean allContainingKeywordInField(List<Project> projects, String keyword) {
        return projects.stream()
                .allMatch(project -> {
                    boolean titleMatches = project.getTitle().matches(".*" + keyword + ".*");
                    boolean jobMatches = project.getJob().matches(".*" + keyword + ".*");
                    boolean skillsMatches = project.getSkills().matches(".*" + keyword + ".*");
                    boolean descriptionMatches = project.getDescription().matches(".*" + keyword + ".*");

                    return titleMatches || jobMatches || skillsMatches || descriptionMatches;
                });
    }

}