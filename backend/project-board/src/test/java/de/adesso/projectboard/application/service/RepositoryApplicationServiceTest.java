package de.adesso.projectboard.application.service;

import de.adesso.projectboard.base.application.dto.ProjectApplicationRequestDTO;
import de.adesso.projectboard.base.application.persistence.ProjectApplication;
import de.adesso.projectboard.base.application.persistence.ProjectApplicationRepository;
import de.adesso.projectboard.base.exceptions.AlreadyAppliedException;
import de.adesso.projectboard.base.project.persistence.Project;
import de.adesso.projectboard.base.project.service.ProjectService;
import de.adesso.projectboard.base.user.persistence.User;
import de.adesso.projectboard.base.user.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RepositoryApplicationServiceTest {

    @Mock
    ProjectService projectService;

    @Mock
    UserService userService;

    @Mock
    ProjectApplicationRepository applicationRepo;

    @InjectMocks
    RepositoryApplicationService applicationService;

    @Mock
    User user;

    @Mock
    Project project;

    @Before
    public void setUp() {
        // set up repo service mock
        when(projectService.getProjectById("ID")).thenReturn(project);

        when(applicationRepo.save(any(ProjectApplication.class))).thenAnswer((Answer<ProjectApplication>) invocation -> {
            Object[] args = invocation.getArguments();

            return (ProjectApplication) args[0];
        });
        when(userService.save(any(User.class))).thenAnswer((Answer<User>) invocation -> {
            Object[] args = invocation.getArguments();

            return (User) args[0];
        });
    }

    @Test
    public void testUserHasAppliedForProject_HasApplied() {
        // set up repo mock
        when(applicationRepo.existsByUserAndProject(user, project)).thenReturn(true);

        assertTrue(applicationService.userHasAppliedForProject(user, project));
    }

    @Test
    public void testUserHasAppliedForProject_HasNotApplied() {
        // set up repo mock
        when(applicationRepo.existsByUserAndProject(user, project)).thenReturn(false);

        assertFalse(applicationService.userHasAppliedForProject(user, project));
    }

    @Test
    public void testCreateApplicationForUser_OK() {
        // create and set up new mock
        ProjectApplicationRequestDTO dto = mock(ProjectApplicationRequestDTO.class);
        when(dto.getComment()).thenReturn("Comment!");
        when(dto.getProjectId()).thenReturn("ID");

        // set up repo mock
        when(applicationRepo.existsByUserAndProject(user, project)).thenReturn(false);

        ProjectApplication applicationForUser = applicationService.createApplicationForUser(user, dto);

        assertEquals("Comment!", applicationForUser.getComment());
        assertEquals(user, applicationForUser.getUser());
        assertEquals(project, applicationForUser.getProject());

        verify(userService).save(user);
        verify(applicationRepo).save(any());
    }

    @Test(expected = AlreadyAppliedException.class)
    public void testCreateApplicationForUser_AlreadyApplied() {
        // create new mock
        ProjectApplicationRequestDTO dto = mock(ProjectApplicationRequestDTO.class);
        when(dto.getProjectId()).thenReturn("ID");

        // set up repo mock
        when(applicationRepo.existsByUserAndProject(user, project)).thenReturn(true);

        applicationService.createApplicationForUser(user, dto);
    }

    @Test
    public void testGetApplicationsOfUser() {
        // create new mocks
        ProjectApplication firstApplication = mock(ProjectApplication.class);
        ProjectApplication secondApplication = mock(ProjectApplication.class);
        Set<ProjectApplication> applications = Stream.of(firstApplication, secondApplication).collect(Collectors.toSet());

        // set up user mock
        when(user.getApplications()).thenReturn(applications);

        assertTrue(applicationService.getApplicationsOfUser(user).containsAll(applications));
        assertTrue(applications.containsAll(applicationService.getApplicationsOfUser(user)));
    }



}