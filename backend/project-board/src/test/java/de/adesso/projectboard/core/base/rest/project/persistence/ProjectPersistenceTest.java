package de.adesso.projectboard.core.base.rest.project.persistence;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
public class ProjectPersistenceTest {

    @Autowired
    private ProjectRepository projectRepository;

    private final Sort sort = Sort.unsorted();

    @Before
    public void setUp() {
        projectRepository.deleteAll();
    }

    @Test
    public void testSave_OK() {
        Project project = new Project()
                .setId("STF-1")
                .setStatus("Teststatus")
                .setIssuetype("Testissuetype")
                .setTitle("Testtitle")
                .setLabels(Arrays.asList("Label 1", "Label 2", "Label 3"))
                .setJob("Testjob")
                .setSkills("Testskills")
                .setDescription("Testdescription")
                .setLob("Testlob")
                .setCustomer("Testcustomer")
                .setLocation("Testlocation")
                .setOperationStart("Teststart")
                .setOperationEnd("Testend")
                .setEffort("Testeffort")
                .setCreated(LocalDateTime.of(2018, 1, 1, 12, 0))
                .setUpdated(LocalDateTime.of(2018, 1, 2, 12, 0))
                .setFreelancer("Testfreelancer")
                .setElongation("Testelongation")
                .setOther("Testother")
                .setEditable(false);

        projectRepository.save(project);

        Optional<Project> projectOptional = projectRepository.findById("STF-1");
        assertTrue(projectOptional.isPresent());

        Project retrievedProject = projectOptional.get();

        assertEquals("STF-1", retrievedProject.getId());
        assertEquals("Teststatus", retrievedProject.getStatus());
        assertEquals("Testissuetype", retrievedProject.getIssuetype());
        assertEquals("Testtitle", retrievedProject.getTitle());
        assertEquals("Testjob", retrievedProject.getJob());
        assertEquals("Testskills", retrievedProject.getSkills());
        assertEquals("Testdescription", retrievedProject.getDescription());
        assertEquals("Testlob", retrievedProject.getLob());
        assertEquals("Testcustomer", retrievedProject.getCustomer());
        assertEquals("Testlocation", retrievedProject.getLocation());
        assertEquals("Teststart", retrievedProject.getOperationStart());
        assertEquals("Testend", retrievedProject.getOperationEnd());
        assertEquals("Testeffort", retrievedProject.getEffort());
        assertEquals(LocalDateTime.of(2018, 1, 1, 12, 0), retrievedProject.getCreated());
        assertEquals(LocalDateTime.of(2018, 1, 2, 12, 0), retrievedProject.getUpdated());
        assertEquals("Testfreelancer", retrievedProject.getFreelancer());
        assertEquals("Testelongation", retrievedProject.getElongation());
        assertEquals("Testother", retrievedProject.getOther());
        assertFalse(retrievedProject.isEditable());

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
    public void testFindAllByEscalatedOrOpenOrSameLob() {
        projectRepository.saveAll(getProjectList());

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
    public void testFindAllByEscalatedOrOpen() {
        projectRepository.saveAll(getProjectList());

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

    private List<Project> getProjectList() {
        Project firstProject = new Project()
                .setId("STF-1")
                .setStatus("eskaliert")
                .setLob("LOB Test");

        Project secondProject = new Project()
                .setId("STF-2")
                .setStatus("Abgeschlossen")
                .setLob("LOB Test");

        Project thirdProject = new Project()
                .setId("STF-3")
                .setStatus("eskaliert")
                .setLob("LOB Prod");

        Project fourthProject = new Project()
                .setId("STF-4")
                .setStatus("Offen")
                .setLob(null);

        Project fifthProject = new Project()
                .setId("STF-5")
                .setStatus("eskaliert")
                .setLob(null);

        Project sixthProject = new Project()
                .setId("STF-6")
                .setStatus("Abgeschlossen")
                .setLob(null);

        Project seventhProject = new Project()
                .setId("STF-7")
                .setStatus("Something weird")
                .setLob(null);

        Project eighthProject = new Project()
                .setId("STF-8")
                .setStatus("Offen")
                .setLob("LOB Test");

        Project ninthProject = new Project()
                .setId("STF-9")
                .setStatus("Offen")
                .setLob("LOB Prod");

        return Arrays.asList(firstProject, secondProject, thirdProject,
                fourthProject, fifthProject, sixthProject,
                seventhProject, eighthProject, ninthProject);
    }

}