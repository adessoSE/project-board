package de.adesso.projectboard.base.project.persistence;

import org.assertj.core.api.SoftAssertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
@TestPropertySource("classpath:application-persistence-test.properties")
public class ProjectPersistenceTest {

    @Autowired
    ProjectRepository projectRepository;

    private final Sort sort = Sort.unsorted();

    @Test
    public void testSave() {
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
                .setOrigin(ProjectOrigin.CUSTOM);

        // when
        projectRepository.save(expectedProject);
        Project retrievedProject = projectRepository.findById(expectedProjectId).orElseThrow(EntityNotFoundException::new);

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
    public void findAllForUser() {
        // given
        // SQL script

        // when
        List<Project> allForUser = projectRepository.findAllForUser("LOB Test", sort);

        // then
        testProjectsForUser(allForUser);
    }

    @Test
    @Sql("classpath:de/adesso/projectboard/persistence/Projects.sql")
    public void findAllForUserPageable() {
        // given
        // SQL script

        // when
        Page<Project> allForUser = projectRepository.findAllForUserPageable("LOB Test", PageRequest.of(0, 100));

        // then
        testProjectsForUser(allForUser.getContent());
    }

    @Test
    @Sql("classpath:de/adesso/projectboard/persistence/Projects.sql")
    public void findAllForManager() {
        // given
        // SQL script

        // when
        List<Project> allForManager = projectRepository.findAllForManager(sort);

        // then
        testProjectsForManager(allForManager);
    }

    @Test
    @Sql("classpath:de/adesso/projectboard/persistence/Projects.sql")
    public void findAllForManagerPageable() {
        // given
        // SQL script

        // when
        Page<Project> allForManager = projectRepository.findAllForManagerPageable(PageRequest.of(0, 100));

        // then
        testProjectsForManager(allForManager.getContent());
    }

    void testProjectsForUser(List<Project> allForUser) {
        boolean allEscalatedOrFromSameLobOrNoLob = allForUser.stream()
                .allMatch(project -> {
                    boolean isOpen = "open".equalsIgnoreCase(project.getStatus());
                    boolean isEscalated = "eskaliert".equalsIgnoreCase(project.getStatus());
                    boolean sameLobAsUser = "LOB Test".equalsIgnoreCase(project.getLob());
                    boolean noLob = project.getLob() == null;

                    // escalated || isOpen <-> (sameLob || noLob)
                    // equivalence because implication is not enough
                    // when the status is neither "eskaliert" nor "open"
                    return isEscalated || (isOpen && (sameLobAsUser || noLob) || (!isOpen && !(sameLobAsUser || noLob)));
                });

        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(allEscalatedOrFromSameLobOrNoLob).isTrue();
        softly.assertThat(allForUser).hasSize(5);

        softly.assertAll();
    }

    void testProjectsForManager(List<Project> allForManager) {
        // managers can see all open/escalated projects
        boolean allEscalatedOrOpen =
                allForManager.stream()
                        .allMatch(project -> {
                            boolean isOpen = "open".equalsIgnoreCase(project.getStatus());
                            boolean isEscalated = "eskaliert".equalsIgnoreCase(project.getStatus());

                            return isOpen || isEscalated;
                        });

        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(allEscalatedOrOpen).isTrue();
        softly.assertThat(allForManager).hasSize(6);

        softly.assertAll();
    }

}