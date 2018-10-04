package de.adesso.projectboard.core.base.rest.project.persistence;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
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

    @Before
    public void setUp() {
        projectRepository.deleteAll();
    }

    @Test
    public void testSave_OK() {
        Project project = Project.builder()
                .id("STF-1")
                .status("Teststatus")
                .issuetype("Testissuetype")
                .title("Testtitle")
                .labels(Arrays.asList("Label 1", "Label 2", "Label 3"))
                .job("Testjob")
                .skills("Testskills")
                .description("Testdescription")
                .lob("Testlob")
                .customer("Testcustomer")
                .location("Testlocation")
                .operationStart("Teststart")
                .operationEnd("Testend")
                .effort("Testeffort")
                .created(LocalDateTime.of(2018, 1, 1, 12, 0))
                .updated(LocalDateTime.of(2018, 1, 2, 12, 0))
                .freelancer("Testfreelancer")
                .elongation("Testelongation")
                .other("Testother")
                .editable(false)
                .build();

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
    public void testGetAllForUserOfLob() {
        projectRepository.saveAll(getProjectList());

        // get a list of all projects for a user of the lob "LOB Test"
        List<Project> allForUser = projectRepository.getAllForUserOfLob("LOB Test");

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
    public void testGetAllForSuperUser() {
        projectRepository.saveAll(getProjectList());

        // get a list of all projects for a superuser
        List<Project> allForUser = projectRepository.getAllForSuperUser();

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
        Project firstProject = Project.builder()
                .id("STF-1")
                .status("eskaliert")
                .lob("LOB Test")
                .build();

        Project secondProject = Project.builder()
                .id("STF-2")
                .status("Abgeschlossen")
                .lob("LOB Test")
                .build();

        Project thirdProject = Project.builder()
                .id("STF-3")
                .status("eskaliert")
                .lob("LOB Prod")
                .build();

        Project fourthProject = Project.builder()
                .id("STF-4")
                .status("Offen")
                .lob(null)
                .build();

        Project fifthProject = Project.builder()
                .id("STF-5")
                .status("eskaliert")
                .lob(null)
                .build();

        Project sixthProject = Project.builder()
                .id("STF-6")
                .status("Abgeschlossen")
                .lob(null)
                .build();

        Project seventhProject = Project.builder()
                .id("STF-7")
                .status("Something weird")
                .lob(null)
                .build();

        Project eighthProject = Project.builder()
                .id("STF-8")
                .status("Offen")
                .lob("LOB Test")
                .build();

        Project ninthProject = Project.builder()
                .id("STF-9")
                .status("Offen")
                .lob("LOB Prod")
                .build();

        return Arrays.asList(firstProject, secondProject, thirdProject,
                fourthProject, fifthProject, sixthProject,
                seventhProject, eighthProject, ninthProject);
    }

}