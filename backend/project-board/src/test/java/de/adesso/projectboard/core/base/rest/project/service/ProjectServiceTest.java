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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepo;

    @Mock
    private UserRepository userRepo;

    @Mock
    private UserService userService;

    @InjectMocks
    private ProjectService projectService;

    private SuperUser superUser;

    private User user;

    private Project editableProject;

    private Project nonEditableProject;

    @Before
    public void setUp() {
        // data setup
        setUpUserMockData();
        setUpProjectMockData();

        // mock setup
        when(userService.getUserById(anyString())).thenThrow(UserNotFoundException.class);
        when(userService.getUserById(eq(superUser.getId()))).thenReturn(superUser);

        when(projectRepo.findById(anyString())).thenReturn(Optional.empty());
        when(projectRepo.findById(eq(editableProject.getId()))).thenReturn(Optional.of(editableProject));
        when(projectRepo.findById(eq(nonEditableProject.getId()))).thenReturn(Optional.of(nonEditableProject));

        when(projectRepo.existsById(anyString())).thenReturn(false);
        when(projectRepo.existsById(eq(editableProject.getId()))).thenReturn(true);
        when(projectRepo.existsById(eq(nonEditableProject.getId()))).thenReturn(true);

        when(projectRepo.getAllForSuperUser()).thenReturn(Collections.emptyList());
        when(projectRepo.getAllForUserOfLob(anyString())).thenReturn(Collections.emptyList());

        // just return passed argument
        when(projectRepo.save(any(Project.class))).thenAnswer((Answer<Project>) invocation -> {
            Object[] args = invocation.getArguments();
            return (Project) args[0];
        });

        when(userRepo.existsByIdAndCreatedProjectsContaining(anyString(), any(Project.class))).thenReturn(false);

        when(userRepo.findByCreatedProjectsContaining(any(Project.class))).thenReturn(Optional.empty());
    }

    @Test
    public void testGetProjectById_OK() {
        Project retrievedProject = projectService.getProjectById(editableProject.getId());

        assertEquals(editableProject, retrievedProject);
    }

    @Test(expected = ProjectNotFoundException.class)
    public void testGetProjectById_NotFound() {
        projectService.getProjectById("non-existent-project");
    }

    @Test
    public void testProjectExists() {
        assertTrue(projectService.projectExists(editableProject.getId()));
        assertTrue(projectService.projectExists(nonEditableProject.getId()));
        assertFalse(projectService.projectExists("non-existent-id"));
    }

    @Test
    public void testUserHasProject() {
        assertFalse(projectService.userHasProject(superUser.getId(), editableProject.getId()));

        superUser.addCreatedProject(editableProject);
        assertTrue(superUser.getCreatedProjects().contains(editableProject));

        // override mock behaviour
        when(userRepo.existsByIdAndCreatedProjectsContaining(eq(superUser.getId()), eq(editableProject)))
                .thenReturn(true);

        assertTrue(projectService.userHasProject(superUser.getId(), editableProject.getId()));
    }

    @Test
    public void getProjectsForUser_User() {
        projectService.getProjectsForUser(user);

        verify(projectRepo).getAllForUserOfLob(user.getLob());
    }

    @Test
    public void getProjectsForUser_SuperUser() {
        projectService.getProjectsForUser(superUser);

        verify(projectRepo).getAllForSuperUser();
    }

    @Test
    public void testUpdateProject_OK() {
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

        verify(projectRepo).save(any(Project.class));
        assertTrue(updatedProject.isEditable());
        assertEquals(editableProject.getId(), updatedProject.getId());
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
        projectService.updateProject(dto, nonEditableProject.getId());
    }

    @Test
    public void testCreateProject_OK() {
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

        Project createdProject = projectService.createProject(dto, superUser.getId());

        verify(projectRepo).save(any(Project.class));
        verify(userService).save(superUser);

        assertEquals(1L, superUser.getCreatedProjects().size());
        assertTrue(superUser.getCreatedProjects().contains(createdProject));

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

    @Test(expected = UserNotFoundException.class)
    public void testCreateProject_UserNotExists() {
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

        projectService.createProject(dto, "non-existent-project");
    }

    @Test
    public void testDeleteProjectById_OK() {
        superUser.addCreatedProject(editableProject);
        assertTrue(superUser.getCreatedProjects().contains(editableProject));

        // override mock
        when(userRepo.findByCreatedProjectsContaining(editableProject)).thenReturn(Optional.of(superUser));

        projectService.deleteProjectById(editableProject.getId());

        verify(projectRepo).delete(editableProject);
        assertFalse(superUser.getCreatedProjects().contains(editableProject));
    }

    @Test(expected = ProjectNotEditableException.class)
    public void testDeleteProjectById_ProjectNotEditable() {
        projectService.deleteProjectById(nonEditableProject.getId());
    }

    @Test(expected = ProjectNotFoundException.class)
    public void testDeleteProjectById_ProjectNotExists() {
        projectService.deleteProjectById("non-existing-project");
    }

    private void setUpUserMockData() {
        this.superUser = new SuperUser("super-user");
        this.superUser.setFullName("Super", "User");
        this.superUser.setLob("LOB Test");
        this.superUser.setEmail("super-user@test.com");

        this.user = new User("normal-user", superUser);
        this.user.setFullName("Normal", "User");
        this.user.setLob("LOB Test");
        this.user.setEmail("normal-user@test.com");
    }

    private void setUpProjectMockData() {
        this.nonEditableProject = Project.builder()
                .id("STF-8")
                .title("Title")
                .status("Offen")
                .lob("LOB Test")
                .build();

        this.editableProject = Project.builder()
                .id("STF-9")
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
    }

}