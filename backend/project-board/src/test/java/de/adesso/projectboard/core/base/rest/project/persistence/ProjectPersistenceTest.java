package de.adesso.projectboard.core.base.rest.project.persistence;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@DataJpaTest
public class ProjectPersistenceTest {

    @Autowired
    private ProjectRepository projectRepository;

    private final Sort sort = Sort.unsorted();

    @Test
    @Sql("classpath:de/adesso/projectboard/core/base/persistence/Projects.sql")
    public void testSave_OK() {
        Optional<Project> projectOptional = projectRepository.findById("STF-1");
        assertTrue(projectOptional.isPresent());

        Project retrievedProject = projectOptional.get();

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
        assertEquals(LocalDateTime.of(2018, 2, 1, 13, 37), retrievedProject.getCreated());
        assertEquals(LocalDateTime.of(2018, 2, 2, 13, 37), retrievedProject.getUpdated());
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
    @Sql("classpath:de/adesso/projectboard/core/base/persistence/Projects.sql")
    public void testFindAllByEscalatedOrOpenOrSameLob() {
        // get a list of all projects for a user of the lob "LOB Test"
        List<Project> allForUser = projectRepository.findAllByStatusEscalatedOrOpenOrSameLob("LOB Test", sort);

        boolean allEscalatedOrFromSameLobOrNoLob = allForUser.stream()
                .allMatch(project -> {
                    boolean isOpen = "offen".equalsIgnoreCase(project.getStatus());
                    boolean isEscalated = "eskaliert".equalsIgnoreCase(project.getStatus());
                    boolean sameLobAsUser = "LOB Test".equalsIgnoreCase(project.getLob());
                    boolean noLob = project.getLob() == null;

                    // escalated || isOpen <-> (sameLob || noLob)
                    // equivalence because implication is not enough
                    // when the status is neither "eskaliert" nor "offen"
                    return isEscalated || (isOpen && (sameLobAsUser || noLob) || (!isOpen && !(sameLobAsUser || noLob)));
                });

        assertTrue(allEscalatedOrFromSameLobOrNoLob);

        assertEquals(5L, allForUser.size());
    }

    @Test
    @Sql("classpath:de/adesso/projectboard/core/base/persistence/Projects.sql")
    public void testFindAllByEscalatedOrOpen() {
        // get a list of all projects for a superuser
        List<Project> allForUser = projectRepository.findAllByStatusEscalatedOrOpen(sort);

        // superusers can see all open/escalated projects
        boolean allEscalatedOrOpen =
                allForUser.stream()
                        .allMatch(project -> {
                            boolean isOpen = "offen".equalsIgnoreCase(project.getStatus());
                            boolean isEscalated = "eskaliert".equalsIgnoreCase(project.getStatus());

                            return isOpen || isEscalated;
                        });
        assertTrue(allEscalatedOrOpen);

        assertEquals(6L, allForUser.size());
    }


}