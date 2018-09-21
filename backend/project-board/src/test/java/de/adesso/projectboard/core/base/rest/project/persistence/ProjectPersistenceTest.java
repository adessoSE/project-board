package de.adesso.projectboard.core.base.rest.project.persistence;

import de.adesso.projectboard.core.project.persistence.Project;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
public class ProjectPersistenceTest {

    @Autowired
    private ProjectRepository projectRepository;

    @Test
    public void testSave_OK() {
        Project firstProject = new Project();

        firstProject.setId(1L);
        firstProject.setKey("Testkey");
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

        projectRepository.save(firstProject);

        List<Project> projects = StreamSupport.stream(projectRepository.findAll().spliterator(), false)
                .map(project -> (Project) project)
                .collect(Collectors.toList());

        assertEquals(1, projects.size());

        // first project
        Project projectRetrieved = projects.get(0);

        assertEquals(1L, projectRetrieved.getId());
        assertEquals("Testkey", projectRetrieved.getKey());
        assertEquals("Teststatus", projectRetrieved.getStatus());
        assertEquals("Testissuetype", projectRetrieved.getIssuetype());
        assertEquals("Testtitle", projectRetrieved.getTitle());
        assertEquals("Testjob", projectRetrieved.getJob());
        assertEquals("Testskills", projectRetrieved.getSkills());
        assertEquals("Testdescription", projectRetrieved.getDescription());
        assertEquals("Testlob", projectRetrieved.getLob());
        assertEquals("Testcustomer", projectRetrieved.getCustomer());
        assertEquals("Testlocation", projectRetrieved.getLocation());
        assertEquals("Teststart", projectRetrieved.getOperationStart());
        assertEquals("Testend", projectRetrieved.getOperationEnd());
        assertEquals("Testeffort", projectRetrieved.getEffort());
        assertEquals(LocalDateTime.of(2018, 1, 1, 12, 0), projectRetrieved.getCreated());
        assertEquals(LocalDateTime.of(2018, 1, 2, 12, 0), projectRetrieved.getUpdated());
        assertEquals("Testfreelancer", projectRetrieved.getFreelancer());
        assertEquals("Testelongation", projectRetrieved.getElongation());
        assertEquals("Testother", projectRetrieved.getOther());


        List<String> firstProjectLabels = projectRetrieved.getLabels();
        assertEquals(3, firstProjectLabels.size());
        assertEquals("Label 1", firstProjectLabels.get(0));
        assertEquals("Label 2", firstProjectLabels.get(1));
        assertEquals("Label 3", firstProjectLabels.get(2));
    }

}