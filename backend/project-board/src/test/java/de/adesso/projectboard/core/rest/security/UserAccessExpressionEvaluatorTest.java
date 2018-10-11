package de.adesso.projectboard.core.rest.security;

import de.adesso.projectboard.core.base.rest.project.persistence.Project;
import de.adesso.projectboard.core.base.rest.project.service.ProjectService;
import de.adesso.projectboard.core.base.rest.user.persistence.SuperUser;
import de.adesso.projectboard.core.base.rest.user.persistence.User;
import de.adesso.projectboard.core.base.rest.user.service.ApplicationService;
import de.adesso.projectboard.core.base.rest.user.service.UserService;
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
    private UserService userService;

    @Mock
    private ProjectService projectService;

    @Mock
    private ApplicationService applicationService;

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