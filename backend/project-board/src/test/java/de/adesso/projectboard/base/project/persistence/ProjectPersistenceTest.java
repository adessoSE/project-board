package de.adesso.projectboard.base.project.persistence;

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@DataJpaTest
@TestPropertySource("classpath:application-persistence-test.properties")
public class ProjectPersistenceTest {

    @Autowired
    ProjectRepository projectRepository;

    private final Sort sort = Sort.unsorted();

    @Test
    public void testSave() {
        LocalDateTime created = LocalDateTime.of(2018, 2, 1, 13, 37);
        LocalDateTime updated = LocalDateTime.of(2018, 2, 2, 13, 37);

        Project project = new Project()
                .setId("STF-1")
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

        projectRepository.save(project);
        Project retrievedProject = projectRepository.findById("STF-1").orElseThrow(EntityNotFoundException::new);

        assertEquals("STF-1", retrievedProject.getId());
        assertEquals("eskaliert", retrievedProject.getStatus());
        assertEquals("Issuetype", retrievedProject.getIssuetype());
        assertEquals("Title", retrievedProject.getTitle());
        assertEquals("Job", retrievedProject.getJob());
        assertEquals("Skills", retrievedProject.getSkills());
        assertEquals("Description", retrievedProject.getDescription());
        assertEquals("LOB Test", retrievedProject.getLob());
        assertEquals("Customer", retrievedProject.getCustomer());
        assertEquals("Location", retrievedProject.getLocation());
        assertEquals("OperationStart", retrievedProject.getOperationStart());
        assertEquals("OperationEnd", retrievedProject.getOperationEnd());
        assertEquals("Effort", retrievedProject.getEffort());
        assertEquals(created, retrievedProject.getCreated());
        assertEquals(updated, retrievedProject.getUpdated());
        assertEquals("Freelancer", retrievedProject.getFreelancer());
        assertEquals("Elongation", retrievedProject.getElongation());
        assertEquals("Other", retrievedProject.getOther());
        assertEquals(ProjectOrigin.CUSTOM, retrievedProject.getOrigin());

        List<String> firstProjectLabels = retrievedProject.getLabels();
        assertEquals(3, firstProjectLabels.size());
        assertEquals("Label 1", firstProjectLabels.get(0));
        assertEquals("Label 2", firstProjectLabels.get(1));
        assertEquals("Label 3", firstProjectLabels.get(2));
    }

    @Test
    public void testIdGenerator_AlreadySet() {
        Project project = new Project();
        project.setId("STF-1234");

        Project persistedProject = projectRepository.save(project);

        assertEquals("STF-1234", persistedProject.getId());
    }

    @Test
    public void testIdGenerator_NotSet() {
        Project project = new Project();

        Project persistedProject = projectRepository.save(project);

        assertEquals("AD-1", persistedProject.getId());
    }

    @Test
    public void testIdGenerator_Increment() {
        Project firstProject = new Project();
        Project secondProject = new Project();

        Project firstPersisted = projectRepository.save(firstProject);
        Project secondPersisted = projectRepository.save(secondProject);

        assertEquals("AD-1", firstPersisted.getId());
        assertEquals("AD-2", secondPersisted.getId());
    }

    @Test
    public void testIdGenerator_Remove() {
        Project firstProject = new Project();
        Project firstPersisted = projectRepository.save(firstProject);

        assertEquals("AD-1", firstPersisted.getId());

        projectRepository.delete(firstPersisted);

        Project secondProject = new Project();
        Project secondPersisted = projectRepository.save(secondProject);

        assertEquals("AD-1", secondPersisted.getId());
    }

    @Test
    @Sql("classpath:de/adesso/projectboard/persistence/Projects.sql")
    public void testFindAllForUser() {
        // get a list of all projects for a user of the lob "LOB Test"
        List<Project> allForUser = projectRepository.findAllForUser("LOB Test", sort);

        testProjectsForUser(allForUser);
    }

    @Test
    @Sql("classpath:de/adesso/projectboard/persistence/Projects.sql")
    public void testFindAllForUserPageable() {
        Page<Project> allForUser = projectRepository.findAllForUserPageable("LOB Test", PageRequest.of(0, 100));

        testProjectsForUser(allForUser.getContent());
    }

    @Test
    @Sql("classpath:de/adesso/projectboard/persistence/Projects.sql")
    public void testFindAllForManager() {
        // get a list of all projects for a manager
        List<Project> allForManager = projectRepository.findAllForManager(sort);

        testProjectsForManager(allForManager);
    }

    @Test
    @Sql("classpath:de/adesso/projectboard/persistence/Projects.sql")
    public void testFindAllForManagerPageable() {
        Page<Project> allForManager = projectRepository.findAllForManagerPageable(PageRequest.of(0, 100));

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

        assertTrue(allEscalatedOrFromSameLobOrNoLob);
        assertEquals(5L, allForUser.size());
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

        assertTrue(allEscalatedOrOpen);
        assertEquals(6L, allForManager.size());
    }

}