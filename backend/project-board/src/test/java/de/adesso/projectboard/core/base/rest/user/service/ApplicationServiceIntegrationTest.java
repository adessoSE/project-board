package de.adesso.projectboard.core.base.rest.user.service;

import de.adesso.projectboard.core.base.rest.exceptions.AlreadyAppliedException;
import de.adesso.projectboard.core.base.rest.exceptions.ProjectNotFoundException;
import de.adesso.projectboard.core.base.rest.exceptions.UserNotFoundException;
import de.adesso.projectboard.core.base.rest.project.persistence.Project;
import de.adesso.projectboard.core.base.rest.project.persistence.ProjectRepository;
import de.adesso.projectboard.core.base.rest.project.service.ProjectService;
import de.adesso.projectboard.core.base.rest.user.application.dto.ProjectApplicationRequestDTO;
import de.adesso.projectboard.core.base.rest.user.application.persistence.ProjectApplication;
import de.adesso.projectboard.core.base.rest.user.application.persistence.ProjectApplicationRepository;
import de.adesso.projectboard.core.base.rest.user.persistence.SuperUser;
import de.adesso.projectboard.core.base.rest.user.persistence.User;
import de.adesso.projectboard.core.base.rest.user.persistence.UserRepository;
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

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Transactional
public class ApplicationServiceIntegrationTest {

    @Autowired
    private ProjectApplicationRepository applicationRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private ProjectRepository projRepo;

    @Autowired
    private ApplicationService applicationService;

    @MockBean
    private ProjectService projectService;

    @MockBean
    private UserService userService;

    @Before
    public void setUp() throws Exception {
        // DB setup
        User user = new SuperUser("user");
        user.setFullName("Test", "User");
        user.setLob("LOB Test");
        user.setEmail("test-user@test.com");

        Project project = Project.builder()
                .id("STF-1")
                .title("Title")
                .build();

        projRepo.save(project);
        userRepo.save(user);

        // mock setup
        when(projectService.getProjectById(anyString())).thenThrow(ProjectNotFoundException.class);
        when(projectService.getProjectById(eq( "STF-1"))).thenReturn(projRepo.findById("STF-1").get());

        when(userService.getUserById(anyString())).thenThrow(UserNotFoundException.class);
        when(userService.getUserById(eq("user"))).thenReturn(userRepo.findById("user").get());
    }

    @Test
    public void testUserHasAppliedForProject() {
        User user = userRepo.findById("user").get();
        Project project = projRepo.findById("STF-1").get();

        assertFalse(applicationService.userHasAppliedForProject("user", project));

        ProjectApplication application = new ProjectApplication(project, "Comment", user);
        applicationRepo.save(application);

        assertTrue(applicationService.userHasAppliedForProject("user", project));
    }

    @Test
    public void testCreateApplicationForUser_OK() {
        User user = userRepo.findById("user").get();
        Project project = projRepo.findById("STF-1").get();

        ProjectApplicationRequestDTO dto = new ProjectApplicationRequestDTO();
        dto.setProjectId(project.getId());
        dto.setComment("Comment");

        assertEquals(0L, applicationRepo.count());

        ProjectApplication application = applicationService.createApplicationForUser(dto, user.getId());
        assertEquals(1L, applicationRepo.count());
        assertEquals(user, application.getUser());
        assertEquals(project, application.getProject());
        assertEquals("Comment", application.getComment());
    }

    @Test(expected = AlreadyAppliedException.class)
    public void testCreateApplicationForUser_AlreadyApplied() {
        User user = userRepo.findById("user").get();
        Project project = projRepo.findById("STF-1").get();

        ProjectApplicationRequestDTO dto = new ProjectApplicationRequestDTO();
        dto.setProjectId(project.getId());
        dto.setComment("Comment");

        assertEquals(0L, applicationRepo.count());

        applicationService.createApplicationForUser(dto, user.getId());
        assertEquals(1L, applicationRepo.count());

        applicationService.createApplicationForUser(dto, user.getId());
    }

    @Test
    public void testGetApplicationsOfUser() {
        User user = userRepo.findById("user").get();
        Project project = projRepo.findById("STF-1").get();

        assertEquals(0L, applicationService.getApplicationsOfUser("user").size());

        ProjectApplication application = new ProjectApplication(project, "Comment", user);
        userRepo.save(user);
        applicationRepo.save(application);

        List<ProjectApplication> applications
                = new ArrayList<>(applicationService.getApplicationsOfUser("user"));

        assertEquals(1L, applications.size());
        ProjectApplication retrievedUserApplication = applications.get(0);
        assertEquals(project, retrievedUserApplication.getProject());
        assertEquals(user, retrievedUserApplication.getUser());
        assertEquals("Comment", retrievedUserApplication.getComment());
    }

}