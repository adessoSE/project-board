package de.adesso.projectboard.rest.security;

import de.adesso.projectboard.base.application.persistence.ProjectApplication;
import de.adesso.projectboard.base.project.persistence.Project;
import de.adesso.projectboard.base.project.service.ProjectServiceImpl;
import de.adesso.projectboard.base.user.persistence.User;
import de.adesso.projectboard.ldap.user.LdapUserService;
import de.adesso.projectboard.service.ApplicationServiceImpl;
import de.adesso.projectboard.util.ProjectSupplier;
import de.adesso.projectboard.util.UserSupplier;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserAccessExpressionEvaluatorTest {

    @Mock
    private LdapUserService userService;

    @Mock
    private ProjectServiceImpl projectService;

    @Mock
    private ApplicationServiceImpl applicationService;

    @InjectMocks
    private UserAccessExpressionEvaluator expressionEvaluator;

    private UserSupplier userSupplier = new UserSupplier();

    private ProjectSupplier projectSupplier = new ProjectSupplier();

    @Before
    public void setUp() {
        userSupplier.resetUsers();
        projectSupplier.resetProjects();

        SuperUser firstSU = userSupplier.getFirstSuperUser();
        SuperUser secondSU = userSupplier.getSecondSuperUser();
        User firstU = userSupplier.getFirstUser();
        User secondU = userSupplier.getSecondUser();

        Project editableProject = projectSupplier.getEditableProject();
        firstSU.addCreatedProject(editableProject);

        // setup mocks
        when(userService.userHasStaffMember(any(SuperUser.class), anyString())).thenReturn(false);
        when(userService.userHasStaffMember(eq(firstSU), eq(firstU.getId()))).thenReturn(true);
        when(userService.userHasStaffMember(eq(secondSU), eq(secondU.getId()))).thenReturn(true);

        when(projectService.userHasProject(anyString(), anyString())).thenReturn(false);
        when(projectService.userHasProject(eq(firstSU.getId()), eq(editableProject.getId()))).thenReturn(true);
    }

    @Test
    public void testHasAccessToProjects_User() {
        User user = userSupplier.getFirstUser();

        assertFalse(expressionEvaluator.hasAccessToProjects(null, user));
        user.giveAccessUntil(LocalDateTime.now().plus(10L, ChronoUnit.DAYS));
        assertTrue(expressionEvaluator.hasAccessToProjects(null, user));
    }

    @Test
    public void testHasAccessToProjects_SuperUser() {
        SuperUser superUser = userSupplier.getSecondSuperUser();

        assertTrue(expressionEvaluator.hasAccessToProjects(null, superUser));
    }

    @Test
    public void testHasPermissionToAccessUser_SameUser() {
        User user = userSupplier.getFirstUser();

        assertTrue(expressionEvaluator.hasPermissionToAccessUser(null, user, user.getId()));
    }

    @Test
    public void testHasAccessToProject_User_HasAccess_OpenProjectOfSameLob() {
        User user = userSupplier.getFirstUser();
        user.setLob("LOB Test 1")
            .giveAccessUntil(LocalDateTime.now().plus(10L, ChronoUnit.DAYS));

        assertTrue(user.hasAccess());

        Project openTestProjectOfSameLob = projectSupplier.getNonEditableProject()
                .setStatus("Offen")
                .setLob("LOB Test 1");

        // set up mock
        when(projectService.projectExists(eq(openTestProjectOfSameLob.getId()))).thenReturn(true);
        when(projectService.getProjectById(eq(openTestProjectOfSameLob.getId()))).thenReturn(openTestProjectOfSameLob);

        assertTrue(expressionEvaluator.hasAccessToProject(null, user, openTestProjectOfSameLob.getId()));
    }

    @Test
    public void testHasAccessToProject_User_HasAccess_OpenProjectOfDifferentLob() {
        User user = userSupplier.getFirstUser();
        user.setLob("LOB Test 2")
                .giveAccessUntil(LocalDateTime.now().plus(10L, ChronoUnit.DAYS));

        assertTrue(user.hasAccess());

        Project openProjectOfDifferentLob = projectSupplier.getNonEditableProject()
                .setStatus("Offen")
                .setLob("LOB Production");

        // set up mock
        when(projectService.projectExists(eq(openProjectOfDifferentLob.getId()))).thenReturn(true);
        when(projectService.getProjectById(eq(openProjectOfDifferentLob.getId()))).thenReturn(openProjectOfDifferentLob);

        assertFalse(expressionEvaluator.hasAccessToProject(null, user, openProjectOfDifferentLob.getId()));
    }

    @Test
    public void testHasAccessToProject_User_HasAccess_EscalatedProject() {
        User user = userSupplier.getFirstUser();
        user.setLob("LOB Test 3")
                .giveAccessUntil(LocalDateTime.now().plus(10L, ChronoUnit.DAYS));

        assertTrue(user.hasAccess());

        Project escalatedProjectOfDifferentLob = projectSupplier.getNonEditableProject()
                .setStatus("eskaliert")
                .setLob("LOB Production");

        // set up mock
        when(projectService.projectExists(eq(escalatedProjectOfDifferentLob.getId()))).thenReturn(true);
        when(projectService.getProjectById(eq(escalatedProjectOfDifferentLob.getId()))).thenReturn(escalatedProjectOfDifferentLob);

        assertTrue(expressionEvaluator.hasAccessToProject(null, user, escalatedProjectOfDifferentLob.getId()));
    }

    @Test
    public void testHasAccessToProject_User_HasAccess_ProjectNoLob() {
        User user = userSupplier.getFirstUser();
        user.setLob("LOB Test 4")
                .giveAccessUntil(LocalDateTime.now().plus(10L, ChronoUnit.DAYS));

        assertTrue(user.hasAccess());

        Project openProjectWithNoLob = projectSupplier.getNonEditableProject()
                .setStatus("Offen")
                .setLob(null);

        // set up mock
        when(projectService.projectExists(eq(openProjectWithNoLob.getId()))).thenReturn(true);
        when(projectService.getProjectById(eq(openProjectWithNoLob.getId()))).thenReturn(openProjectWithNoLob);

        assertTrue(expressionEvaluator.hasAccessToProject(null, user, openProjectWithNoLob.getId()));
    }

    @Test
    public void testHasAccessToProject_User_HasAccess_ProjectUnusualStatus() {
        User user = userSupplier.getFirstUser();
        user.setLob("LOB Test 5")
                .giveAccessUntil(LocalDateTime.now().plus(10L, ChronoUnit.DAYS));

        assertTrue(user.hasAccess());

        Project projectWithUnusualStatus = projectSupplier.getNonEditableProject()
                .setStatus("Pretty unusual status")
                .setLob("LOB Test 5");

        // set up mock
        when(projectService.projectExists(eq(projectWithUnusualStatus.getId()))).thenReturn(true);
        when(projectService.getProjectById(eq(projectWithUnusualStatus.getId()))).thenReturn(projectWithUnusualStatus);

        assertFalse(expressionEvaluator.hasAccessToProject(null, user, projectWithUnusualStatus.getId()));
    }

    @Test
    public void testHasAccessToProject_User_NoAccess_HasApplied() {
        User user = userSupplier.getFirstUser();
        Project project = projectSupplier.getEditableProject();
        ProjectApplication projectApplication = new ProjectApplication(project, "", user);

        user.getApplications().add(projectApplication);

        // set up mock
        when(projectService.projectExists(eq(project.getId()))).thenReturn(true);
        when(projectService.getProjectById(eq(project.getId()))).thenReturn(project);

        when(applicationService.userHasAppliedForProject(eq(user.getId()), eq(project))).thenReturn(true);

        assertTrue(expressionEvaluator.hasAccessToProject(null, user, project.getId()));
    }

    @Test
    public void testHasAccessToProject_SuperUser() {
        SuperUser superUser = userSupplier.getFirstSuperUser();
        superUser.setLob("LOB Test 6");

        Project project = projectSupplier.getEditableProject();
        project.setStatus("Offen")
                .setLob("LOB Different");

        // set up mock
        when(projectService.projectExists(eq(project.getId()))).thenReturn(true);
        when(projectService.getProjectById(eq(project.getId()))).thenReturn(project);

        assertTrue(expressionEvaluator.hasAccessToProject(null, superUser, project.getId()));
    }

    @Test
    public void testHasElevatedAccessToUser_DifferentUser() {
        SuperUser firstSU = userSupplier.getFirstSuperUser();
        SuperUser secondSU = userSupplier.getSecondSuperUser();
        User firstU = userSupplier.getFirstUser();
        User secondU = userSupplier.getSecondUser();

        assertFalse(expressionEvaluator.hasElevatedAccessToUser(null, firstU, secondU.getId()));
        assertFalse(expressionEvaluator.hasElevatedAccessToUser(null, secondU, firstU.getId()));
        assertFalse(expressionEvaluator.hasElevatedAccessToUser(null, firstSU, secondU.getId()));
        assertFalse(expressionEvaluator.hasElevatedAccessToUser(null, secondSU, firstU.getId()));
        assertFalse(expressionEvaluator.hasElevatedAccessToUser(null, firstU, firstSU.getId()));
        assertFalse(expressionEvaluator.hasElevatedAccessToUser(null, secondU, secondSU.getId()));

        assertTrue(expressionEvaluator.hasElevatedAccessToUser(null, firstSU, firstU.getId()));
        assertTrue(expressionEvaluator.hasElevatedAccessToUser(null, secondSU, secondU.getId()));
    }

    @Test
    public void testHasPermissionToCreateProject() {
        SuperUser superUser = userSupplier.getFirstSuperUser();
        User user = userSupplier.getFirstUser();

        assertTrue(expressionEvaluator.hasPermissionToCreateProjects(null, superUser));
        assertFalse(expressionEvaluator.hasPermissionToCreateProjects(null, user));
    }

    @Test
    public void testHasPermissionToEditProject() {
        SuperUser firstSU = userSupplier.getFirstSuperUser();
        SuperUser secondSU = userSupplier.getSecondSuperUser();
        Project project = projectSupplier.getEditableProject();

        assertTrue(expressionEvaluator.hasPermissionToEditProject(null, firstSU, project.getId()));
        assertFalse(expressionEvaluator.hasPermissionToEditProject(null, secondSU, project.getId()));
    }

}