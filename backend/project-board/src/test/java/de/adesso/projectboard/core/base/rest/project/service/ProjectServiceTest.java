package de.adesso.projectboard.core.base.rest.project.service;

import de.adesso.projectboard.core.base.rest.exceptions.ProjectNotEditableException;
import de.adesso.projectboard.core.base.rest.exceptions.ProjectNotFoundException;
import de.adesso.projectboard.core.base.rest.exceptions.UserNotFoundException;
import de.adesso.projectboard.core.base.rest.project.dto.ProjectRequestDTO;
import de.adesso.projectboard.core.base.rest.project.persistence.Project;
import de.adesso.projectboard.core.base.rest.project.persistence.ProjectRepository;
import de.adesso.projectboard.core.base.rest.user.application.persistence.ProjectApplicationRepository;
import de.adesso.projectboard.core.base.rest.user.persistence.SuperUser;
import de.adesso.projectboard.core.base.rest.user.persistence.User;
import de.adesso.projectboard.core.base.rest.user.persistence.UserRepository;
import de.adesso.projectboard.core.base.rest.user.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

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
    private ProjectApplicationRepository applicationRepo;

    @Mock
    private UserService userService;

    @InjectMocks
    private ProjectService projectService;

    private final Sort sort = Sort.unsorted();

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

        when(projectRepo.findAllByStatusEscalatedOrOpen(any(Sort.class))).thenReturn(Collections.emptyList());
        when(projectRepo.findAllByStatusEscalatedOrOpenOrSameLob(anyString(), any(Sort.class))).thenReturn(Collections.emptyList());

        // just return passed argument
        when(projectRepo.save(any(Project.class))).thenAnswer((Answer<Project>) invocation -> {
            Object[] args = invocation.getArguments();
            return (Project) args[0];
        });

        when(userRepo.existsByIdAndCreatedProjectsContaining(anyString(), any(Project.class))).thenReturn(false);

        when(userRepo.findAllByCreatedProjectsContaining(any(Project.class))).thenReturn(Collections.emptyList());
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
        projectService.getProjectsForUser(user, sort);

        verify(projectRepo).findAllByStatusEscalatedOrOpenOrSameLob(user.getLob(), sort);
    }

    @Test
    public void getProjectsForUser_SuperUser() {
        projectService.getProjectsForUser(superUser, sort);

        verify(projectRepo).findAllByStatusEscalatedOrOpen(sort);
    }

    @Test
    public void testUpdateProject_OK() {
        ProjectRequestDTO dto = new ProjectRequestDTO()
                .setStatus("eskaliert")
                .setIssuetype("Edited Issuetype")
                .setTitle("Edited Title")
                .setLabels(Arrays.asList("Edited Label 1", "Edited Label 2"))
                .setJob("Edited Job")
                .setSkills("Edited Skills")
                .setDescription("Edited Description")
                .setLob("LOB Prod")
                .setCustomer("Edited Customer")
                .setLocation("Edited Location")
                .setOperationStart("Edited Start")
                .setOperationEnd("Edited End")
                .setEffort("Edited Effort")
                .setFreelancer("Edited Freelancer")
                .setElongation("Edited Elongation")
                .setOther("Edited Other");
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
        ProjectRequestDTO dto = new ProjectRequestDTO()
                .setStatus("eskaliert")
                .setIssuetype("Edited Issuetype")
                .setTitle("Edited Title")
                .setLabels(Arrays.asList("Edited Label 1", "Edited Label 2"))
                .setJob("Edited Job")
                .setSkills("Edited Skills")
                .setDescription("Edited Description")
                .setLob("LOB Prod")
                .setCustomer("Edited Customer")
                .setLocation("Edited Location")
                .setOperationStart("Edited Start")
                .setOperationEnd("Edited End")
                .setEffort("Edited Effort")
                .setFreelancer("Edited Freelancer")
                .setElongation("Edited Elongation")
                .setOther("Edited Other");
        projectService.updateProject(dto, nonEditableProject.getId());
    }

    @Test
    public void testCreateProject_OK() {
        ProjectRequestDTO dto = new ProjectRequestDTO()
                .setStatus("eskaliert")
                .setIssuetype("Issuetype")
                .setTitle("Title")
                .setLabels(Arrays.asList("Label 1", "Label 2"))
                .setJob("Job")
                .setSkills("Skills")
                .setDescription("Description")
                .setLob("LOB Prod")
                .setCustomer("Customer")
                .setLocation("Location")
                .setOperationStart("Start")
                .setOperationEnd("End")
                .setEffort("Effort")
                .setFreelancer("Freelancer")
                .setElongation("Elongation")
                .setOther("Other");

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
        ProjectRequestDTO dto = new ProjectRequestDTO()
                .setStatus("eskaliert")
                .setIssuetype("Issuetype")
                .setTitle("Title")
                .setLabels(Arrays.asList("Label 1", "Label 2"))
                .setJob("Job")
                .setSkills("Skills")
                .setDescription("Description")
                .setLob("LOB Prod")
                .setCustomer("Customer")
                .setLocation("Location")
                .setOperationStart("Start")
                .setOperationEnd("End")
                .setEffort("Effort")
                .setFreelancer("Freelancer")
                .setElongation("Elongation")
                .setOther("Other");

        projectService.createProject(dto, "non-existent-project");
    }

    @Test
    public void testDeleteProjectById_OK() {
        superUser.addCreatedProject(editableProject);
        assertTrue(superUser.getCreatedProjects().contains(editableProject));

        // override mock
        when(userRepo.findAllByCreatedProjectsContaining(editableProject))
                .thenReturn(Collections.singletonList(superUser));

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
        this.nonEditableProject = new Project()
                .setId("STF-8")
                .setTitle("Title")
                .setStatus("Offen")
                .setLob("LOB Test");

        this.editableProject = new Project()
                .setId("STF-9")
                .setStatus("Offen")
                .setIssuetype("Original Issuetype")
                .setTitle("Original Title")
                .setLabels(Arrays.asList("Original Label 1", "Original Label 2"))
                .setJob("Original Job")
                .setSkills("Original Skills")
                .setDescription("Original Description")
                .setLob("LOB Prod")
                .setCustomer("Original Customer")
                .setLocation("Original Location")
                .setOperationStart("Original Start")
                .setOperationEnd("Original End")
                .setEffort("Original Effort")
                .setCreated(LocalDateTime.now().minus(10L, ChronoUnit.DAYS))
                .setUpdated(LocalDateTime.now().minus(3L, ChronoUnit.DAYS))
                .setFreelancer("Original Freelancer")
                .setElongation("Original Elongation")
                .setOther("Original Other")
                .setEditable(true);
    }

}