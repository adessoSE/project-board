package de.adesso.projectboard.core.base.rest.project.service;

import de.adesso.projectboard.core.base.rest.exceptions.ProjectNotEditableException;
import de.adesso.projectboard.core.base.rest.exceptions.ProjectNotFoundException;
import de.adesso.projectboard.core.base.rest.exceptions.UserNotFoundException;
import de.adesso.projectboard.core.base.rest.project.dto.ProjectRequestDTO;
import de.adesso.projectboard.core.base.rest.project.persistence.Project;
import de.adesso.projectboard.core.base.rest.project.persistence.ProjectRepository;
import de.adesso.projectboard.core.base.rest.user.persistence.SuperUser;
import de.adesso.projectboard.core.base.rest.user.persistence.User;
import de.adesso.projectboard.core.base.rest.user.persistence.UserRepository;
import de.adesso.projectboard.core.base.rest.user.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional
public class ProjectServiceIntegrationTest {

    @Autowired
    private ProjectRepository projectRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private ProjectService projectService;

    @MockBean
    private UserService userService;

    @Before
    public void setUp() {
        // DB setup
        projectRepo.saveAll(getProjectList());

        SuperUser superUser = new SuperUser("super-user");
        superUser.setFullName("Super", "User");
        superUser.setLob("LOB Test");
        superUser.setEmail("super-user@test.com");

        User user = new User("normal-user", superUser);
        user.setFullName("Normal", "User");
        user.setLob("LOB Test");
        user.setEmail("normal-user@test.com");

        userRepo.saveAll(Arrays.asList(superUser, user));

        // mock setup
        when(userService.getUserById(anyString())).thenThrow(UserNotFoundException.class);
        when(userService.getUserById(eq("super-user"))).thenReturn(userRepo.findById("super-user").get());
        when(userService.getUserById(eq("normal-user"))).thenReturn(userRepo.findById("normal-user").get());
    }

    @Test
    public void testGetProjectById_OK() {
        Project project = projectService.getProjectById("STF-1");

        assertEquals("STF-1", project.getId());
        assertEquals("Title", project.getTitle());
    }

    @Test(expected = ProjectNotFoundException.class)
    public void testGetProjectById_NotFound() {
        projectService.getProjectById("non-existent-id");
    }

    @Test
    public void testProjectExists() {
        assertTrue(projectService.projectExists("STF-1"));
        assertFalse(projectService.projectExists("non-existent-id"));
    }

    @Test
    public void testUserHasProject() {
        SuperUser superUser = (SuperUser) userRepo.findById("super-user").get();
        Project project = projectRepo.findById("STF-1").get();

        assertFalse(projectService.userHasProject(superUser.getId(), project.getId()));

        superUser.addCreatedProject(project);
        userRepo.save(superUser);

        assertTrue(projectService.userHasProject(superUser.getId(), project.getId()));
    }

    @Test
    public void getProjectsForUser_User() {
        // get a list of all projects for a user of the lob "LOB Test"
        User user = userRepo.findById("normal-user").get();
        List<Project> allForUser = projectService.getProjectsForUser(user);

        boolean allEscalatedOrFromSameLob = allForUser.stream()
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

        assertTrue(allEscalatedOrFromSameLob);

        assertEquals(5L, allForUser.size());
    }

    @Test
    public void getProjectsForUser_SuperUser() {
        // get a list of all projects for a superuser
        User user = userRepo.findById("super-user").get();
        List<Project> allForUser = projectService.getProjectsForUser(user);

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

    @Test
    public void testUpdateProject_OK() {
        Project editableProject = projectRepo.findById("STF-4").get();
        assertTrue(editableProject.isEditable());

        ProjectRequestDTO dto = ProjectRequestDTO.builder()
                .status("eskaliert")
                .issuetype("Edited Issuetype")
                .title("Edited Title")
                .labels(Arrays.asList("Edited Label 1", "Edited Label 2"))
                .job("Edited Job")
                .skills("Edited Skills")
                .description("Edited Description")
                .lob("LOB Prod")
                .customer("Edited Customer")
                .location("Edited Location")
                .operationStart("Edited Start")
                .operationEnd("Edited End")
                .effort("Edited Effort")
                .freelancer("Edited Freelancer")
                .elongation("Edited Elongation")
                .other("Edited Other")
                .build();
        Project updatedProject = projectService.updateProject(dto, editableProject.getId());

        assertTrue(updatedProject.isEditable());
        assertEquals("STF-4", updatedProject.getId());
        assertEquals("eskaliert", updatedProject.getStatus());
        assertEquals("Edited Issuetype", updatedProject.getIssuetype());
        assertEquals("Edited Title", updatedProject.getTitle());
        assertEquals(2L, updatedProject.getLabels().size());
        assertEquals("Edited Label 1", updatedProject.getLabels().get(0));
        assertEquals("Edited Label 2", updatedProject.getLabels().get(1));
        assertEquals("Edited Job", updatedProject.getJob());
        assertEquals("Edited Description", updatedProject.getDescription());
        assertEquals("LOB Prod", updatedProject.getLob());
        assertEquals("Edited Customer", updatedProject.getCustomer());
        assertEquals("Edited Location", updatedProject.getLocation());
        assertEquals("Edited Start", updatedProject.getOperationStart());
        assertEquals("Edited End", updatedProject.getOperationEnd());
        assertEquals("Edited Effort", updatedProject.getEffort());
        assertEquals("Edited Freelancer", updatedProject.getFreelancer());
        assertEquals("Edited Elongation", updatedProject.getElongation());
        assertEquals("Edited Other", updatedProject.getOther());
        assertTrue(updatedProject.isEditable());
    }

    @Test(expected = ProjectNotEditableException.class)
    public void testUpdateProject_NotEditable() {
        Project uneditableProject = projectRepo.findById("STF-1").get();
        assertFalse(uneditableProject.isEditable());

        ProjectRequestDTO dto = ProjectRequestDTO.builder()
                .status("eskaliert")
                .issuetype("Edited Issuetype")
                .title("Edited Title")
                .labels(Arrays.asList("Edited Label 1", "Edited Label 2"))
                .job("Edited Job")
                .skills("Edited Skills")
                .description("Edited Description")
                .lob("LOB Prod")
                .customer("Edited Customer")
                .location("Edited Location")
                .operationStart("Edited Start")
                .operationEnd("Edited End")
                .effort("Edited Effort")
                .freelancer("Edited Freelancer")
                .elongation("Edited Elongation")
                .other("Edited Other")
                .build();
        projectService.updateProject(dto, uneditableProject.getId());
    }

    @Test
    public void testCreateProject() {
        User user = userRepo.findById("super-user").get();

        ProjectRequestDTO dto = ProjectRequestDTO.builder()
                .status("eskaliert")
                .issuetype("Issuetype")
                .title("Title")
                .labels(Arrays.asList("Label 1", "Label 2"))
                .job("Job")
                .skills("Skills")
                .description("Description")
                .lob("LOB Prod")
                .customer("Customer")
                .location("Location")
                .operationStart("Start")
                .operationEnd("End")
                .effort("Effort")
                .freelancer("Freelancer")
                .elongation("Elongation")
                .other("Other")
                .build();

        Project createdProject = projectService.createProject(dto, user.getId());
        assertEquals(9L, projectRepo.count());

        user = userRepo.findById("super-user").get();
        assertEquals(1L, user.getCreatedProjects().size());
        assertTrue(user.getCreatedProjects().contains(createdProject));

        assertNotNull(createdProject.getId());
        assertTrue(createdProject.isEditable());
        assertEquals("eskaliert", createdProject.getStatus());
        assertEquals("Issuetype", createdProject.getIssuetype());
        assertEquals("Title", createdProject.getTitle());
        assertEquals(2L, createdProject.getLabels().size());
        assertEquals("Label 1", createdProject.getLabels().get(0));
        assertEquals("Label 2", createdProject.getLabels().get(1));
        assertEquals("Job", createdProject.getJob());
        assertEquals("Description", createdProject.getDescription());
        assertEquals("LOB Prod", createdProject.getLob());
        assertEquals("Customer", createdProject.getCustomer());
        assertEquals("Location", createdProject.getLocation());
        assertEquals("Start", createdProject.getOperationStart());
        assertEquals("End", createdProject.getOperationEnd());
        assertEquals("Effort", createdProject.getEffort());
        assertEquals("Freelancer", createdProject.getFreelancer());
        assertEquals("Elongation", createdProject.getElongation());
        assertEquals("Other", createdProject.getOther());
        assertNotNull(createdProject.getCreated());
        assertNotNull(createdProject.getUpdated());
    }

    @Test
    public void testDeleteProject_OK() {
        Project editableProject = projectRepo.findById("STF-4").get();
        assertTrue(editableProject.isEditable());

        projectService.deleteProjectById(editableProject.getId());

        assertEquals(7L, projectRepo.count());
        assertFalse(projectRepo.existsById("STF-4"));
    }

    @Test(expected = ProjectNotEditableException.class)
    public void testDeleteProject_NotEditable() {
        Project uneditableProject = projectRepo.findById("STF-1").get();
        assertFalse(uneditableProject.isEditable());

        projectService.deleteProjectById(uneditableProject.getId());
    }

    private List<Project> getProjectList() {
        Project firstProject = Project.builder()
                .id("STF-1")
                .title("Title")
                .status("Offen")
                .lob("LOB Test")
                .build();

        Project secondProject = Project.builder()
                .id("STF-2")
                .status("eskaliert")
                .lob("LOB Test")
                .build();

        Project thirdProject = Project.builder()
                .id("STF-3")
                .status("Abgeschlossen")
                .lob("LOB Test")
                .build();

        Project fourthProject = Project.builder()
                .id("STF-4")
                .status("Offen")
                .issuetype("Original Issuetype")
                .title("Original Title")
                .labels(Arrays.asList("Original Label 1", "Original Label 2"))
                .job("Original Job")
                .skills("Original Skills")
                .description("Original Description")
                .lob("LOB Prod")
                .customer("Original Customer")
                .location("Original Location")
                .operationStart("Original Start")
                .operationEnd("Original End")
                .effort("Original Effort")
                .created(LocalDateTime.now().minus(10L, ChronoUnit.DAYS))
                .updated(LocalDateTime.now().minus(3L, ChronoUnit.DAYS))
                .freelancer("Original Freelancer")
                .elongation("Original Elongation")
                .other("Original Other")
                .editable(true)
                .build();

        Project fifthProject = Project.builder()
                .id("STF-5")
                .status("eskaliert")
                .lob("LOB Prod")
                .build();

        Project sixthProject = Project.builder()
                .id("STF-6")
                .status("Offen")
                .lob(null)
                .build();

        Project seventhProject = Project.builder()
                .id("STF-7")
                .status("eskaliert")
                .lob(null)
                .build();

        Project eighthProject = Project.builder()
                .id("STF-8")
                .status("Abgeschlossen")
                .lob(null)
                .build();

        Project ninthProject = Project.builder()
                .id("STF-8")
                .status("Something weird")
                .lob(null)
                .build();

        return Arrays.asList(firstProject, secondProject, thirdProject,
                fourthProject, fifthProject, sixthProject,
                seventhProject, eighthProject, ninthProject);
    }

}