package de.adesso.projectboard.core.base.rest.user.service;

import de.adesso.projectboard.core.base.rest.exceptions.AlreadyAppliedException;
import de.adesso.projectboard.core.base.rest.exceptions.ProjectNotFoundException;
import de.adesso.projectboard.core.base.rest.exceptions.UserNotFoundException;
import de.adesso.projectboard.core.base.rest.project.persistence.Project;
import de.adesso.projectboard.core.base.rest.project.service.ProjectService;
import de.adesso.projectboard.core.base.rest.user.application.dto.ProjectApplicationRequestDTO;
import de.adesso.projectboard.core.base.rest.user.application.persistence.ProjectApplication;
import de.adesso.projectboard.core.base.rest.user.application.persistence.ProjectApplicationRepository;
import de.adesso.projectboard.core.base.rest.user.persistence.SuperUser;
import de.adesso.projectboard.core.base.rest.user.persistence.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ApplicationServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private ProjectApplicationRepository applicationRepo;

    @Mock
    private ProjectService projectService;

    @InjectMocks
    private ApplicationService applicationService;

    private SuperUser testUser;

    private Project testProject;

    @Before
    public void setUp() {
        // data setup
        this.testUser = new SuperUser("user");
        this.testUser.setFullName("Test", "User");
        this.testUser.setLob("LOB Test");
        this.testUser.setEmail("test-user@test.com");

        this.testProject = Project.builder()
                .id("STF-1")
                .title("Title")
                .build();

        // mock setup
        when(userService.getUserById(anyString())).thenThrow(UserNotFoundException.class);
        when(userService.getUserById(eq(testUser.getId()))).thenReturn(testUser);

        // just return passed argument
        when(applicationRepo.save(any(ProjectApplication.class))).thenAnswer((Answer<ProjectApplication>) invocation -> {
            Object[] args = invocation.getArguments();
            return (ProjectApplication) args[0];
        });
        when(applicationRepo.existsByUserAndProject(any(User.class), any(Project.class))).thenReturn(false);

        when(projectService.getProjectById(anyString())).thenThrow(ProjectNotFoundException.class);
        when(projectService.getProjectById(eq(testProject.getId()))).thenReturn(testProject);
    }

    @Test
    public void testUserHasAppliedForProject_OK() {
        assertFalse(applicationService.userHasAppliedForProject(testUser.getId(), testProject));

        new ProjectApplication(testProject, "Comment", testUser);
        assertEquals(1L, testUser.getApplications().size());

        // mock setup
        when(applicationRepo.existsByUserAndProject(eq(testUser), eq(testProject))).thenReturn(true);

        assertTrue(applicationService.userHasAppliedForProject(testUser.getId(), testProject));
        verify(applicationRepo, times(2)).existsByUserAndProject(testUser, testProject);
    }

    @Test(expected = UserNotFoundException.class)
    public void testUserHasAppliedForProject_UserNotExists() {
        applicationService.userHasAppliedForProject("non-existent-user", testProject);
    }

    @Test
    public void testCreateApplicationForUser_OK() {
        assertEquals(0L, testUser.getApplications().size());

        ProjectApplicationRequestDTO dto = new ProjectApplicationRequestDTO();
        dto.setProjectId(testProject.getId());
        dto.setComment("Comment");

        ProjectApplication application = applicationService.createApplicationForUser(dto, testUser.getId());
        assertEquals(testUser, application.getUser());
        assertEquals(testProject, application.getProject());
        assertEquals("Comment", application.getComment());

        assertEquals(1L, testUser.getApplications().size());
    }

    @Test(expected = AlreadyAppliedException.class)
    public void testCreateApplicationForUser_AlreadyApplied() {
        ProjectApplication application
                = new ProjectApplication(testProject, "Comment", testUser);

        testUser.addApplication(application);
        assertEquals(1L, testUser.getApplications().size());
        assertTrue(testUser.getApplications().contains(application));

        // mock setup
        when(applicationRepo.existsByUserAndProject(eq(testUser), eq(testProject))).thenReturn(true);

        ProjectApplicationRequestDTO dto
                = new ProjectApplicationRequestDTO(testProject.getId(), "Comment");

        applicationService.createApplicationForUser(dto, testUser.getId());
    }

    @Test(expected = ProjectNotFoundException.class)
    public void testCreateApplicationForUser_ProjectNotExists() {
        ProjectApplicationRequestDTO dto
                = new ProjectApplicationRequestDTO("non-existent-project", "Comment");

        applicationService.createApplicationForUser(dto, testUser.getId());
    }

    @Test(expected = UserNotFoundException.class)
    public void testCreateApplicationForUser_UserNotExists() {
        ProjectApplicationRequestDTO dto
                = new ProjectApplicationRequestDTO(testProject.getId(), "Comment");

        applicationService.createApplicationForUser(dto, "non-existent-project");
    }

    @Test
    public void testGetApplicationsOfUser_OK() {
        ProjectApplication application
                = new ProjectApplication(testProject, "Comment", testUser);

        testUser.addApplication(application);
        assertEquals(1L, testUser.getApplications().size());
        assertTrue(testUser.getApplications().contains(application));

        Set<ProjectApplication> applications
                = applicationService.getApplicationsOfUser(testUser.getId());
        assertEquals(1L, applications.size());
        assertTrue(applications.contains(application));
    }

    @Test(expected = UserNotFoundException.class)
    public void testGetApplicationsOfUser_UserNotExists() {
        applicationService.getApplicationsOfUser("non-existent-user");
    }

}