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
        Project firstProject = new Project();

        firstProject.setId("STF-1");
        firstProject.setStatus("Teststatus");
        firstProject.setIssuetype("Testissuetype");
        firstProject.setTitle("Testtitle");
        firstProject.setLabels(Arrays.asList("Label 1", "Label 2", "Label 3"));
        firstProject.setJob("Testjob");
        firstProject.setSkills("Testskills");
        firstProject.setDescription("Testdescription");
        firstProject.setLob("Testlob");
        firstProject.setCustomer("Testcustomer");
        firstProject.setLocation("Testlocation");
        firstProject.setOperationStart("Teststart");
        firstProject.setOperationEnd("Testend");
        firstProject.setEffort("Testeffort");
        firstProject.setCreated(LocalDateTime.of(2018, 1, 1, 12, 0));
        firstProject.setUpdated(LocalDateTime.of(2018, 1, 2, 12, 0));
        firstProject.setFreelancer("Testfreelancer");
        firstProject.setElongation("Testelongation");
        firstProject.setOther("Testother");
        firstProject.setEditable(false);

        projectRepository.save(firstProject);

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

        assertEquals("STD-1", persistedProject.getId());
    }

    @Test
    public void testIdGenerator_Increment() {
        Project firstProject = new Project();
        Project secondProject = new Project();

        Project firstPersisted = projectRepository.save(firstProject);
        Project secondPersisted = projectRepository.save(secondProject);

        assertEquals("STD-1", firstPersisted.getId());
        assertEquals("STD-2", secondPersisted.getId());
    }

    @Test
    public void testIdGenerator_Remove() {
        Project firstProject = new Project();
        Project firstPersisted = projectRepository.save(firstProject);

        assertEquals("STD-1", firstPersisted.getId());

        projectRepository.delete(firstPersisted);

        Project secondProject = new Project();
        Project secondPersisted = projectRepository.save(secondProject);

        assertEquals("STD-1", secondPersisted.getId());
    }

}