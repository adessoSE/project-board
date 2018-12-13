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
        String expectedLob = "LOB Test";

        // when
        List<Project> allForUser = projectRepository.findAllForUser(expectedLob, sort);

        // then
        testProjectsForUser(allForUser, expectedLob);
    }

    @Test
    @Sql("classpath:de/adesso/projectboard/persistence/Projects.sql")
    public void findAllForUserWhereUserLobDiffersButMeansTheSame() {
        // given
        // SQL script
        String expectedLob = "LOB Test";
        String sameLobButDifferentName = "LOB TEST (LT)";

        // when
        List<Project> allForUser = projectRepository.findAllForUser(sameLobButDifferentName, sort);

        // then
        testProjectsForUser(allForUser, expectedLob);
    }

    @Test
    @Sql("classpath:de/adesso/projectboard/persistence/Projects.sql")
    public void findAllForUserPageable() {
        // given
        // SQL script
        String expectedLob = "LOB Test";

        // when
        Page<Project> allForUser = projectRepository.findAllForUserPageable(expectedLob, PageRequest.of(0, 100));

        // then
        testProjectsForUser(allForUser.getContent(), expectedLob);
    }

    @Test
    @Sql("classpath:de/adesso/projectboard/persistence/Projects.sql")
    public void findAllForUserPageableWhereUserLobDiffersButMeansTheSame() {
        // given
        // SQL script
        String expectedLob = "LOB Test";
        String sameLobButDifferentName = "LOB TEST (LT)";

        // when
        Page<Project> allForUser = projectRepository.findAllForUserPageable(sameLobButDifferentName, PageRequest.of(0, 100));

        // then
        testProjectsForUser(allForUser.getContent(), expectedLob);
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

    @Test
    @Sql("classpath:de/adesso/projectboard/persistence/Projects.sql")
    public void findAllForUserByKeyword() {
        // given
        // SQL script
        String keyword = "Special";
        String expectedLob = "LOB Test";

        // when
        List<Project> allForUserByKeyword =
                projectRepository.findAllForUserByKeyword(expectedLob, keyword, sort);

        // then
        testProjectsForUserByKeyword(allForUserByKeyword, keyword, expectedLob);
    }

    @Test
    @Sql("classpath:de/adesso/projectboard/persistence/Projects.sql")
    public void findAllForUserByKeywordPageable() {
        // given
        // SQL script
        String keyword = "Special";
        String expectedLob = "LOB Test";

        // when
        Page<Project> allForUserByKeyword =
                projectRepository.findAllForUserByKeywordPageable(expectedLob, keyword, PageRequest.of(0, 100));

        // then
        testProjectsForUserByKeyword(allForUserByKeyword.getContent(), keyword, expectedLob);
    }

    @Test
    @Sql("classpath:de/adesso/projectboard/persistence/Projects.sql")
    public void findAllForUserByKeywordPageableWhereUserLobDiffersButMeansTheSame() {
        // given
        // SQL script
        String keyword = "Special";
        String expectedLob = "LOB Test";
        String sameLobButDifferentName = "LOB TEST (LT)";

        // when
        Page<Project> allForUserByKeyword =
                projectRepository.findAllForUserByKeywordPageable(sameLobButDifferentName, keyword, PageRequest.of(0, 100));

        // then
        testProjectsForUserByKeyword(allForUserByKeyword.getContent(), keyword, expectedLob);
    }

    @Test
    @Sql("classpath:de/adesso/projectboard/persistence/Projects.sql")
    public void findAllForManagerByKeyword() {
        // given
        // SQL script
        String keyword = "Special";

        // when
        List<Project> allForManagerByKeyword =
                projectRepository.findAllForManagerByKeyword(keyword, sort);

        // then
        testProjectsForManagerByKeyword(allForManagerByKeyword, keyword);
    }

    @Test
    @Sql("classpath:de/adesso/projectboard/persistence/Projects.sql")
    public void findAllForManagerByKeywordPageable() {
        // given
        // SQL script
        String keyword = "Special";

        // when
        Page<Project> allForManagerByKeyword =
                projectRepository.findAllForManagerByKeywordPageable(keyword, PageRequest.of(0, 100));

        // then
        testProjectsForManagerByKeyword(allForManagerByKeyword.getContent(), keyword);
    }

    void testProjectsForUser(List<Project> allForUser, String expectedLob) {
        boolean allEscalatedOrFromSameLobOrNoLob = allEscalatedOrFromSameLobOrNoLob(allForUser, expectedLob);

        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(allEscalatedOrFromSameLobOrNoLob).isTrue();
        softly.assertThat(allForUser).hasSize(5);

        softly.assertAll();
    }

    void testProjectsForManager(List<Project> allForManager) {
        // managers can see all open/escalated projects
        boolean allEscalatedOrOpen = allEscalatedOrOpen(allForManager);

        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(allEscalatedOrOpen).isTrue();
        softly.assertThat(allForManager).hasSize(6);

        softly.assertAll();
    }

    void testProjectsForUserByKeyword(List<Project> allForUserByKeyword, String keyword, String expectedLob) {
        boolean allEscalatedOrFromSameLobOrNoLob = allEscalatedOrFromSameLobOrNoLob(allForUserByKeyword, expectedLob);
        boolean allContainingKeywordInAnyField = allContainingKeywordInField(allForUserByKeyword, keyword);

        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(allEscalatedOrFromSameLobOrNoLob).isTrue();
        softly.assertThat(allContainingKeywordInAnyField).isTrue();

        softly.assertThat(allForUserByKeyword).hasSize(4);

        softly.assertAll();
    }

    void testProjectsForManagerByKeyword(List<Project> allForManagerByKeyword, String keyword) {
        boolean allEscalatedOrOpen = allEscalatedOrOpen(allForManagerByKeyword);
        boolean allContainingKeywordInAnyField = allContainingKeywordInField(allForManagerByKeyword, keyword);

        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(allEscalatedOrOpen).isTrue();
        softly.assertThat(allContainingKeywordInAnyField).isTrue();

        softly.assertThat(allForManagerByKeyword).hasSize(4);

        softly.assertAll();
    }

    boolean allEscalatedOrFromSameLobOrNoLob(List<Project> projects, String expectedLob) {
        return projects.stream()
                .allMatch(project -> {
                    boolean isOpen = "open".equalsIgnoreCase(project.getStatus());
                    boolean isEscalated = "eskaliert".equalsIgnoreCase(project.getStatus());
                    boolean sameLobAsUser = expectedLob.equalsIgnoreCase(project.getLob());
                    boolean noLob = project.getLob() == null;

                    // escalated || isOpen <-> (sameLob || noLob)
                    // equivalence because implication is not enough
                    // when the status is neither "eskaliert" nor "open"
                    return isEscalated || (isOpen && (sameLobAsUser || noLob) || (!isOpen && !(sameLobAsUser || noLob)));
                });
    }

    boolean allEscalatedOrOpen(List<Project> projects) {
        return projects.stream()
                .allMatch(project -> {
                    boolean isOpen = "open".equalsIgnoreCase(project.getStatus());
                    boolean isEscalated = "eskaliert".equalsIgnoreCase(project.getStatus());

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