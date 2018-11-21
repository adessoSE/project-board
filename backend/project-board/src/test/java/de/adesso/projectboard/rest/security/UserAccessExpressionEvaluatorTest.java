package de.adesso.projectboard.rest.security;

import de.adesso.projectboard.base.access.persistence.AccessInfo;
import de.adesso.projectboard.base.application.service.ApplicationService;
import de.adesso.projectboard.base.project.persistence.Project;
import de.adesso.projectboard.base.project.service.ProjectService;
import de.adesso.projectboard.base.user.persistence.User;
import de.adesso.projectboard.base.user.persistence.data.UserData;
import de.adesso.projectboard.base.user.service.UserProjectService;
import de.adesso.projectboard.base.user.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.Authentication;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserAccessExpressionEvaluatorTest {

    @Mock
    UserService userService;

    @Mock
    ProjectService projectService;

    @Mock
    UserProjectService userProjectService;

    @Mock
    ApplicationService applicationService;

    @InjectMocks
    UserAccessExpressionEvaluator evaluator;

    @Mock
    Authentication authentication;

    @Mock
    User user;

    @Mock
    User staffMember;

    @Mock
    AccessInfo accessInfo;

    @Mock
    Project project;

    @Mock
    UserData userData;

    @Before
    public void setUp() {
        // set up UserData mock
        when(userService.getUserData(user)).thenReturn(userData);
        when(userData.getLob()).thenReturn("LOB Test");

        // set up user mock
        when(user.getId()).thenReturn("user");

        // set up staff member mock
        when(userService.userExists("staff")).thenReturn(true);
        when(userService.getUserById("staff")).thenReturn(staffMember);
    }

    @Test
    public void testHasAccessToProjects_Manager() {
        // set up service mock
        when(userService.userIsManager(user)).thenReturn(true);

        assertTrue(evaluator.hasAccessToProjects(authentication, user));
    }

    @Test
    public void testHasAccessToProjects_NoManager_Access() {
        // set up service/entity mock
        when(user.getLatestAccessInfo()).thenReturn(accessInfo);
        when(accessInfo.isCurrentlyActive()).thenReturn(true);

        assertTrue(evaluator.hasAccessToProjects(authentication, user));
    }

    @Test
    public void testHasAccessToProjects_NoManager_NoAccess() {
        // set up service
        when(userService.userIsManager(user)).thenReturn(false);
        when(user.getLatestAccessInfo()).thenReturn(null);

        assertFalse(evaluator.hasAccessToProjects(authentication, user));
    }

    @Test
    public void testHasAccessToProject_ProjectNotExists() {
        // set up service mock
        when(projectService.projectExists("project")).thenReturn(false);

        assertTrue(evaluator.hasAccessToProject(authentication, user, "project"));
    }

    @Test
    public void testHasAccessToProject_UserHasApplied() {
        // set up service mocks
        when(projectService.projectExists("project")).thenReturn(true);
        when(projectService.getProjectById("project")).thenReturn(project);
        when(applicationService.userHasAppliedForProject(user, project)).thenReturn(true);

        assertTrue(evaluator.hasAccessToProject(authentication, user, "project"));
    }

    @Test
    public void testHasAccessToProject_UserOwnsProject() {
        // set up service mocks
        when(projectService.projectExists("project")).thenReturn(true);
        when(projectService.getProjectById("project")).thenReturn(project);
        when(userProjectService.userOwnsProject(user, project)).thenReturn(true);

        assertTrue(evaluator.hasAccessToProject(authentication, user, "project"));
    }

    @Test
    public void testHasAccessToProject_Manager() {
        // set up service mocks
        when(projectService.projectExists("project")).thenReturn(true);
        when(projectService.getProjectById("project")).thenReturn(project);
        when(applicationService.userHasAppliedForProject(user, project)).thenReturn(false);
        when(userProjectService.userOwnsProject(user, project)).thenReturn(false);
        when(userService.userIsManager(user)).thenReturn(true);

        when(project.getStatus()).thenReturn("open");
        assertTrue(evaluator.hasAccessToProject(authentication, user, "project"));

        when(project.getStatus()).thenReturn("eskaliert");
        assertTrue(evaluator.hasAccessToProject(authentication, user, "project"));

        when(project.getStatus()).thenReturn("something weird");
        assertFalse(evaluator.hasAccessToProject(authentication, user, "project"));
    }

    @Test
    public void testHasAccessToProject_NoManager() {
        // set up service/entity mocks
        when(projectService.projectExists("project")).thenReturn(true);
        when(projectService.getProjectById("project")).thenReturn(project);
        when(applicationService.userHasAppliedForProject(user, project)).thenReturn(false);
        when(userProjectService.userOwnsProject(user, project)).thenReturn(false);
        when(userService.userIsManager(user)).thenReturn(false);

        // user has access, but is not a manager
        when(user.getLatestAccessInfo()).thenReturn(accessInfo);
        when(accessInfo.isCurrentlyActive()).thenReturn(true);

        when(project.getStatus()).thenReturn("eskaliert");
        assertTrue(evaluator.hasAccessToProject(authentication, user, "project"));

        when(project.getStatus()).thenReturn("open");
        when(project.getLob()).thenReturn("LOB Test");
        assertTrue(evaluator.hasAccessToProject(authentication, user, "project"));

        when(project.getStatus()).thenReturn("something weird");
        assertFalse(evaluator.hasAccessToProject(authentication, user, "project"));

        when(project.getStatus()).thenReturn("open");
        when(project.getLob()).thenReturn("A different LOB");
        assertFalse(evaluator.hasAccessToProject(authentication, user, "project"));
    }

    @Test
    public void testHasPermissionToAccessUser() {
        // same ID
        assertTrue(evaluator.hasPermissionToAccessUser(authentication, user, "user"));

        // different ID, and no elevated access
        assertFalse(evaluator.hasPermissionToAccessUser(authentication, user, "non-existing-user"));
    }

    @Test
    public void testHasElevatedAccessToUser_StaffMemberAndNotExists() {
        // exists and is a staff member
        when(userService.userHasStaffMember(user, staffMember)).thenReturn(true);
        assertTrue(evaluator.hasElevatedAccessToUser(authentication, user, "staff"));

        // does not exist
        assertFalse(evaluator.hasElevatedAccessToUser(authentication, user, "non-existent-user"));
    }

    @Test
    public void testHasElevatedAccessToUser_NotAStaffMember() {
        // exists but no staff member
        assertFalse(evaluator.hasElevatedAccessToUser(authentication, user, "staff"));
    }

}