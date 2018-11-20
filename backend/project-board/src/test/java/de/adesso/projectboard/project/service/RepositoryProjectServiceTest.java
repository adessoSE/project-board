package de.adesso.projectboard.project.service;

import de.adesso.projectboard.base.application.persistence.ProjectApplication;
import de.adesso.projectboard.base.application.persistence.ProjectApplicationRepository;
import de.adesso.projectboard.base.exceptions.ProjectNotEditableException;
import de.adesso.projectboard.base.exceptions.ProjectNotFoundException;
import de.adesso.projectboard.base.project.dto.ProjectRequestDTO;
import de.adesso.projectboard.base.project.persistence.Project;
import de.adesso.projectboard.base.project.persistence.ProjectOrigin;
import de.adesso.projectboard.base.project.persistence.ProjectRepository;
import de.adesso.projectboard.base.user.persistence.User;
import de.adesso.projectboard.base.user.persistence.UserRepository;
import de.adesso.projectboard.base.user.persistence.data.UserData;
import de.adesso.projectboard.base.user.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RepositoryProjectServiceTest {

    @Mock
    ProjectRepository projectRepo;

    @Mock
    ProjectApplicationRepository applicationRepo;

    @Mock
    UserRepository userRepo;

    @Mock
    UserService userService;

    @InjectMocks
    RepositoryProjectService projectService;

    @Mock
    Project project;

    @Mock
    User user;

    @Before
    public void setUp() {
        // set up repo mock
        when(projectRepo.findById("project")).thenReturn(Optional.of(project));
        when(projectRepo.existsById("project")).thenReturn(true);

        when(projectRepo.save(any(Project.class))).thenAnswer((Answer<Project>) invocation -> {
            Object[] args = invocation.getArguments();

            return (Project) args[0];
        });
    }

    @Test
    public void testGetProjectById_OK() {
        // mock already set up in setUp method
        Project returnedProject = projectService.getProjectById("project");

        assertEquals(project, returnedProject);
    }

    @Test(expected = ProjectNotFoundException.class)
    public void testGetProjectById_NotFound() {
        projectService.getProjectById("non-existent-project");
    }

    @Test
    public void testProjectExists() {
        // mock already set up in setUp method
        assertTrue(projectService.projectExists("project"));
        assertFalse(projectService.projectExists("non-existent-project"));
    }

    @Test
    public void testUpdateProject_Editable() {
        ProjectRequestDTO dto = getRequestDtoMock();

        // set up entity mock
        LocalDateTime created = LocalDateTime.now().minus(10L, ChronoUnit.DAYS);
        when(project.getCreated()).thenReturn(created);

        // set up entity mock
        when(project.getOrigin()).thenReturn(ProjectOrigin.CUSTOM);

        Project returnedProject = projectService.updateProject(dto, "project");

        assertEquals("project", returnedProject.getId());
        assertEquals(ProjectOrigin.CUSTOM, returnedProject.getOrigin());
        assertEquals(created, returnedProject.getCreated());
        assertTrue(returnedProject.getUpdated().isAfter(created));

        testProject_General(returnedProject);
    }

    @Test(expected = ProjectNotEditableException.class)
    public void testUpdateProject_NotEditable() {
        // create new mock
        ProjectRequestDTO dto = mock(ProjectRequestDTO.class);

        // set up entity mock
        when(project.getOrigin()).thenReturn(ProjectOrigin.JIRA);

        projectService.updateProject(dto, "project");
    }

    @Test
    public void testDeleteProjectById_OK() {
        // create new mock
        ProjectApplication application = mock(ProjectApplication.class);
        when(application.getUser()).thenReturn(user);

        // set up repo mocks
        when(userRepo.findAllByBookmarksContaining(project)).thenReturn(Collections.singletonList(user));
        when(userRepo.findAllByOwnedProjectsContaining(project)).thenReturn(Collections.singletonList(user));
        when(applicationRepo.findAllByProjectEquals(project)).thenReturn(Collections.singletonList(application));

        projectService.deleteProjectById("project");

        verify(user).removeBookmark(project);
        verify(user).removeApplication(application);
        verify(userService, atLeastOnce()).save(user);
        verify(projectRepo).delete(project);
    }

    @Test
    public void testCreateProject() {
        testCreateOrUpdateProject_DoesNotExist();
    }

    @Test
    public void testCreateOrUpdateProject_AlreadyExisting() {
        // create new mock
        ProjectRequestDTO dto = getRequestDtoMock();

        // set up entity mock
        LocalDateTime created = LocalDateTime.now().minus(10L, ChronoUnit.DAYS);
        when(project.getCreated()).thenReturn(created);

        Project returnedProject = projectService.createOrUpdateProject(dto, "project");

        assertEquals("project", returnedProject.getId());
        assertEquals(ProjectOrigin.CUSTOM, returnedProject.getOrigin());
        assertEquals(created, returnedProject.getCreated());
        assertTrue(returnedProject.getUpdated().isAfter(created));

        testProject_General(returnedProject);
    }

    @Test
    public void testCreateOrUpdateProject_DoesNotExist() {
        // create new mock
        ProjectRequestDTO dto = getRequestDtoMock();

        Project returnedProject = projectService.createOrUpdateProject(dto, null);

        assertEquals(ProjectOrigin.CUSTOM, returnedProject.getOrigin());
        assertNotNull(returnedProject.getCreated());
        assertNotNull(returnedProject.getUpdated());
        assertEquals(returnedProject.getUpdated(), returnedProject.getCreated());
    }

    @Test
    public void testGetAllProjectsForUser_Manager() {
        // create new mock
        Sort sorting = mock(Sort.class);

        // set up service mock
        when(userService.userIsManager(user)).thenReturn(true);

        projectService.getProjectsForUser(user, sorting);

        verify(projectRepo).findAllForManager(any());
    }

    @Test
    public void testGetAllProjectsForUser_NoManager() {
        // create new mock
        Sort sorting = mock(Sort.class);
        UserData userData = mock(UserData.class);

        // set up mocks
        when(userData.getLob()).thenReturn("LoB");

        // set up service mock
        when(userService.userIsManager(user)).thenReturn(false);
        when(userService.getUserData(user)).thenReturn(userData);

        projectService.getProjectsForUser(user, sorting);

        verify(projectRepo).findAllForUser(matches("LoB"), any());
    }

    @Test
    public void testSearchForProjectsForUser_Manager() {
        // set up service mock
        when(userService.userIsManager(user)).thenReturn(true);

        Sort sort = Sort.unsorted();

        projectService.searchProjectsForUser(user, "Test", sort);

        verify(projectRepo).findAllForManagerByKeyword("Test", sort);
    }

    @Test
    public void testSearchProjectsForUser_NoManager() {
        // create new mock
        UserData userData = mock(UserData.class);
        when(userData.getLob()).thenReturn("LOB Test");

        // set up service mock
        when(userService.userIsManager(user)).thenReturn(false);
        when(userService.getUserData(user)).thenReturn(userData);

        Sort sort = Sort.unsorted();

        projectService.searchProjectsForUser(user, "Test", sort);

        verify(projectRepo).findAllForUserByKeyword("LOB Test", "Test", sort);
    }

    @Test
    public void testSearchProjectsForUserPaginated_Manager() {
        // set up service mock
        when(userService.userIsManager(user)).thenReturn(true);

        PageRequest pageable = PageRequest.of(0, 10);

        projectService.searchProjectsForUserPaginated("Test", user, pageable);

        verify(projectRepo).findAllForManagerByKeywordPageable("Test", pageable);
    }

    @Test
    public void testSearchProjectsForUserPaginated_NoManager() {
        // create new mock
        UserData userData = mock(UserData.class);
        when(userData.getLob()).thenReturn("LOB Test");

        // set up service mock
        when(userService.userIsManager(user)).thenReturn(false);
        when(userService.getUserData(user)).thenReturn(userData);

        PageRequest pageable = PageRequest.of(0, 10);

        projectService.searchProjectsForUserPaginated("Test", user, pageable);

        verify(projectRepo).findAllForUserByKeywordPageable("LOB Test", "Test", pageable);
    }

    @Test
    public void testAddProjectToUser() {
        Project returnedProject = projectService.addProjectToUser(user, this.project);

        assertEquals(project, returnedProject);
        verify(userService).save(user);
        verify(user).addOwnedProject(this.project);
    }

    private ProjectRequestDTO getRequestDtoMock() {
        // create new mock
        ProjectRequestDTO dto = mock(ProjectRequestDTO.class);
        when(dto.getCustomer()).thenReturn("Customer");
        when(dto.getDescription()).thenReturn("Description");
        when(dto.getEffort()).thenReturn("Effort");
        when(dto.getElongation()).thenReturn("Elongation");
        when(dto.getFreelancer()).thenReturn("Freelancer");
        when(dto.getIssuetype()).thenReturn("Issuetype");
        when(dto.getJob()).thenReturn("Job");
        when(dto.getLabels()).thenReturn(Arrays.asList("Label 1", "Label 2"));
        when(dto.getOperationStart()).thenReturn("Operation Start");
        when(dto.getOperationEnd()).thenReturn("Operation End");
        when(dto.getLob()).thenReturn("LoB");
        when(dto.getLocation()).thenReturn("Location");
        when(dto.getOther()).thenReturn("Other");
        when(dto.getSkills()).thenReturn("Skills");
        when(dto.getStatus()).thenReturn("Status");
        when(dto.getTitle()).thenReturn("Title");

        return dto;
    }

    private void testProject_General(Project p) {
        assertEquals("Customer", p.getCustomer());
        assertEquals("Description", p.getDescription());
        assertEquals("Effort", p.getEffort());
        assertEquals("Elongation", p.getElongation());
        assertEquals("Freelancer", p.getFreelancer());
        assertEquals("Issuetype", p.getIssuetype());
        assertEquals("Job", p.getJob());
        assertEquals(Arrays.asList("Label 1", "Label 2"), p.getLabels());
        assertEquals("Operation Start", p.getOperationStart());
        assertEquals("LoB", p.getLob());
        assertEquals("Location", p.getLocation());
        assertEquals("Other", p.getOther());
        assertEquals("Skills", p.getSkills());
        assertEquals("Status", p.getStatus());
        assertEquals("Title", p.getTitle());
    }

}