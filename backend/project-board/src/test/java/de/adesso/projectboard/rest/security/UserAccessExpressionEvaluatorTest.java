package de.adesso.projectboard.rest.security;

import de.adesso.projectboard.base.access.persistence.AccessInfo;
import de.adesso.projectboard.base.access.service.UserAccessService;
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
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.Authentication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class UserAccessExpressionEvaluatorTest {

    private final String PROJECT_ID = "project";

    private final String USER_ID = "user";

    @Mock
    UserService userService;

    @Mock
    ProjectService projectService;

    @Mock
    UserProjectService userProjectService;

    @Mock
    ApplicationService applicationService;

    @Mock
    UserAccessService userAccessService;

    @Mock
    Authentication authenticationMock;

    @Mock
    User userMock;

    @Mock
    AccessInfo accessInfoMock;

    @Mock
    Project projectMock;

    @Mock
    UserData userDataMock;

    UserAccessExpressionEvaluator evaluator;

    @Before
    public void setUp() {
        this.evaluator = new UserAccessExpressionEvaluator(userService, userAccessService, projectService, userProjectService, applicationService);
    }

    @Test
    public void hasAccessToProjectsReturnsTrueWhenUserIsManager() {
        // given
        given(userService.userIsManager(userMock)).willReturn(true);

        // when
        boolean actualHasAccess = evaluator.hasAccessToProjects(authenticationMock, userMock);

        // then
        assertThat(actualHasAccess).isTrue();
    }

    @Test
    public void hasAccessToProjectsReturnsTrueWhenUserIsNoManagerButAccessIsActive() {
        // given
        given(userAccessService.userHasActiveAccessInfo(userMock)).willReturn(true);

        // when
        boolean actualHasAccess = evaluator.hasAccessToProjects(authenticationMock, userMock);

        // then
        assertThat(actualHasAccess).isTrue();
    }

    @Test
    public void hasAccessToProjectsReturnsFalseWhenUserIsNoManagerAndNoAccess() {
        // given
        given(userAccessService.userHasActiveAccessInfo(userMock)).willReturn(false);

        // when
        boolean actualHasAccess = evaluator.hasAccessToProjects(authenticationMock, userMock);

        // then
        assertThat(actualHasAccess).isFalse();
    }

    @Test
    public void hasAccessToProjectReturnsTrueWhenProjectDoesNotExist() {
        // given
        given(projectService.projectExists(PROJECT_ID)).willReturn(false);

        // when
        boolean actualHasAccess = evaluator.hasAccessToProject(authenticationMock, userMock, PROJECT_ID);

        // then
        assertThat(actualHasAccess).isTrue();
    }

    @Test
    public void hasAccessToProjectReturnsTrueWhenUserOwnsProject() {
        // given
        given(projectService.projectExists(PROJECT_ID)).willReturn(true);
        given(projectService.getProjectById(PROJECT_ID)).willReturn(projectMock);

        given(userProjectService.userOwnsProject(userMock, projectMock)).willReturn(true);

        // when
        boolean actualHasAccess = evaluator.hasAccessToProject(authenticationMock, userMock, PROJECT_ID);

        // then
        assertThat(actualHasAccess).isTrue();
    }

    @Test
    public void hasAccessToProjectReturnsTrueWhenUserHasAppliedForProject() {
        // given
        given(projectService.projectExists(PROJECT_ID)).willReturn(true);
        given(projectService.getProjectById(PROJECT_ID)).willReturn(projectMock);

        given(userProjectService.userOwnsProject(userMock, projectMock)).willReturn(false);
        given(applicationService.userHasAppliedForProject(userMock, projectMock)).willReturn(true);

        // when
        boolean actualHasAccess = evaluator.hasAccessToProject(authenticationMock, userMock, PROJECT_ID);

        // then
        assertThat(actualHasAccess).isTrue();
    }

    @Test
    public void hasAccessToProjectReturnsTrueWhenUserIsManagerAndStatusIsOpen() {
        // given
        String managerLob = "LOB Test";
        given(userDataMock.getLob()).willReturn(managerLob);

        given(projectService.projectExists(PROJECT_ID)).willReturn(true);
        given(projectService.getProjectById(PROJECT_ID)).willReturn(projectMock);
        given(applicationService.userHasAppliedForProject(userMock, projectMock)).willReturn(false);
        given(userProjectService.userOwnsProject(userMock, projectMock)).willReturn(false);

        given(userService.userIsManager(userMock)).willReturn(true);
        given(userService.getUserData(userMock)).willReturn(userDataMock);

        given(projectMock.getStatus()).willReturn("open");

        // when
        boolean actualHasAccess = evaluator.hasAccessToProject(authenticationMock, userMock, PROJECT_ID);

        // then
        assertThat(actualHasAccess).isTrue();
    }

    @Test
    public void hasAccessToProjectReturnsTrueWhenUserIsManagerAndStatusIsEscalated() {
        // given
        String managerLob = "LOB Test";
        given(userDataMock.getLob()).willReturn(managerLob);

        given(projectService.projectExists(PROJECT_ID)).willReturn(true);
        given(projectService.getProjectById(PROJECT_ID)).willReturn(projectMock);
        given(applicationService.userHasAppliedForProject(userMock, projectMock)).willReturn(false);
        given(userProjectService.userOwnsProject(userMock, projectMock)).willReturn(false);

        given(userService.userIsManager(userMock)).willReturn(true);
        given(userService.getUserData(userMock)).willReturn(userDataMock);

        given(projectMock.getStatus()).willReturn("eskaliert");

        // when
        boolean actualHasAccess = evaluator.hasAccessToProject(authenticationMock, userMock, PROJECT_ID);

        // then
        assertThat(actualHasAccess).isTrue();
    }

    @Test
    public void hasAccessToProjectReturnsFalseWhenUserIsManagerAndStatusIsNeitherOpenNorEscalated() {
        // given
        String managerLob = "LOB Test";
        given(userDataMock.getLob()).willReturn(managerLob);

        given(projectService.projectExists(PROJECT_ID)).willReturn(true);
        given(projectService.getProjectById(PROJECT_ID)).willReturn(projectMock);
        given(applicationService.userHasAppliedForProject(userMock, projectMock)).willReturn(false);
        given(userProjectService.userOwnsProject(userMock, projectMock)).willReturn(false);

        given(userService.userIsManager(userMock)).willReturn(true);
        given(userService.getUserData(userMock)).willReturn(userDataMock);

        given(projectMock.getStatus()).willReturn("something else");

        // when
        boolean actualHasAccess = evaluator.hasAccessToProject(authenticationMock, userMock, PROJECT_ID);

        // then
        assertThat(actualHasAccess).isFalse();
    }

    @Test
    public void hasAccessToProjectReturnsTrueWhenUserHasAccessAndStatusIsEscalated() {
        // given
        String userLob = "LOB Test";

        given(projectService.projectExists(PROJECT_ID)).willReturn(true);
        given(projectService.getProjectById(PROJECT_ID)).willReturn(projectMock);
        given(applicationService.userHasAppliedForProject(userMock, projectMock)).willReturn(false);
        given(userProjectService.userOwnsProject(userMock, projectMock)).willReturn(false);

        given(userService.userIsManager(userMock)).willReturn(false);
        given(userService.getUserData(userMock)).willReturn(userDataMock);
        given(userDataMock.getLob()).willReturn(userLob);

        given(userAccessService.userHasActiveAccessInfo(userMock)).willReturn(true);

        given(projectMock.getStatus()).willReturn("eskaliert");

        // when
        boolean actualHasAccess = evaluator.hasAccessToProject(authenticationMock, userMock, PROJECT_ID);

        // then
        assertThat(actualHasAccess).isTrue();
    }

    @Test
    public void hasAccessToProjectReturnsTrueWhenUserHasAccessAndStatusIsOpenAndSameLob() {
        // given
        String userLob = "LOB Test";

        given(projectService.projectExists(PROJECT_ID)).willReturn(true);
        given(projectService.getProjectById(PROJECT_ID)).willReturn(projectMock);
        given(applicationService.userHasAppliedForProject(userMock, projectMock)).willReturn(false);
        given(userProjectService.userOwnsProject(userMock, projectMock)).willReturn(false);

        given(userService.userIsManager(userMock)).willReturn(false);
        given(userService.getUserData(userMock)).willReturn(userDataMock);
        given(userDataMock.getLob()).willReturn(userLob);

        given(userAccessService.userHasActiveAccessInfo(userMock)).willReturn(true);

        given(projectMock.getStatus()).willReturn("open");
        given(projectMock.getLob()).willReturn(userLob);

        // when
        boolean actualHasAccess = evaluator.hasAccessToProject(authenticationMock, userMock, PROJECT_ID);

        // then
        assertThat(actualHasAccess).isTrue();
    }

    @Test
    public void hasAccessToProjectReturnsTrueWhenUserHasAccessAndStatusIsOpenAndNoLob() {
        // given
        String userLob = "LOB Test";

        given(projectService.projectExists(PROJECT_ID)).willReturn(true);
        given(projectService.getProjectById(PROJECT_ID)).willReturn(projectMock);
        given(applicationService.userHasAppliedForProject(userMock, projectMock)).willReturn(false);
        given(userProjectService.userOwnsProject(userMock, projectMock)).willReturn(false);

        given(userService.userIsManager(userMock)).willReturn(false);
        given(userService.getUserData(userMock)).willReturn(userDataMock);
        given(userDataMock.getLob()).willReturn(userLob);

        given(userAccessService.userHasActiveAccessInfo(userMock)).willReturn(true);

        given(projectMock.getStatus()).willReturn("open");
        given(projectMock.getLob()).willReturn(null);

        // when
        boolean actualHasAccess = evaluator.hasAccessToProject(authenticationMock, userMock, PROJECT_ID);

        // then
        assertThat(actualHasAccess).isTrue();
    }

    @Test
    public void hasAccessToProjectReturnsFalseWhenUserHasAccessAndStatusIsNeitherOpenNorEscalated() {
        // given
        String userLob = "LOB Test";

        given(projectService.projectExists(PROJECT_ID)).willReturn(true);
        given(projectService.getProjectById(PROJECT_ID)).willReturn(projectMock);
        given(applicationService.userHasAppliedForProject(userMock, projectMock)).willReturn(false);
        given(userProjectService.userOwnsProject(userMock, projectMock)).willReturn(false);

        given(userService.userIsManager(userMock)).willReturn(false);
        given(userService.getUserData(userMock)).willReturn(userDataMock);
        given(userDataMock.getLob()).willReturn(userLob);

        given(userAccessService.userHasActiveAccessInfo(userMock)).willReturn(true);

        given(projectMock.getStatus()).willReturn("something else");
        given(projectMock.getLob()).willReturn(userLob);

        // when
        boolean actualHasAccess = evaluator.hasAccessToProject(authenticationMock, userMock, PROJECT_ID);

        // then
        assertThat(actualHasAccess).isFalse();
    }

    @Test
    public void hasAccessToProjectReturnsFalseWhenUserHasAccessAndStatusIsOpenButDifferentLob() {
        // given
        String userLob = "LOB Test";

        given(projectService.projectExists(PROJECT_ID)).willReturn(true);
        given(projectService.getProjectById(PROJECT_ID)).willReturn(projectMock);
        given(applicationService.userHasAppliedForProject(userMock, projectMock)).willReturn(false);
        given(userProjectService.userOwnsProject(userMock, projectMock)).willReturn(false);

        given(userService.userIsManager(userMock)).willReturn(false);
        given(userService.getUserData(userMock)).willReturn(userDataMock);
        given(userDataMock.getLob()).willReturn(userLob);

        given(userAccessService.userHasActiveAccessInfo(userMock)).willReturn(true);

        given(projectMock.getStatus()).willReturn("open");
        given(projectMock.getLob()).willReturn("something else");

        // when
        boolean actualHasAccess = evaluator.hasAccessToProject(authenticationMock, userMock, PROJECT_ID);

        // then
        assertThat(actualHasAccess).isFalse();
    }

    @Test
    public void hasAccessToProjectReturnsFalseWhenNoAccess() {
        // given
        given(projectService.projectExists(PROJECT_ID)).willReturn(true);
        given(projectService.getProjectById(PROJECT_ID)).willReturn(projectMock);
        given(applicationService.userHasAppliedForProject(userMock, projectMock)).willReturn(false);
        given(userProjectService.userOwnsProject(userMock, projectMock)).willReturn(false);

        given(userAccessService.userHasActiveAccessInfo(userMock)).willReturn(false);

        // when
        boolean actualHasAccess = evaluator.hasAccessToProject(authenticationMock, userMock, PROJECT_ID);

        // then
        assertThat(actualHasAccess).isFalse();
    }

    @Test
    public void hasPermissionToAccessUserReturnsTrueWhenSameId() {
        // given
        given(userMock.getId()).willReturn(USER_ID);

        // when
        boolean actualHasPermission = evaluator.hasPermissionToAccessUser(authenticationMock, userMock, USER_ID);

        // then
        assertThat(actualHasPermission).isTrue();
    }

    @Test
    public void hasPermissionToAccessUserReturnsTrueWhenUserIsStaffMember() {
        // given
        String accessedUserId = "other";
        User accessedUserMock = mock(User.class);

        given(userMock.getId()).willReturn(USER_ID);

        given(userService.userExists(accessedUserId)).willReturn(true);
        given(userService.getUserById(accessedUserId)).willReturn(accessedUserMock);
        given(userService.userHasStaffMember(userMock, accessedUserMock)).willReturn(true);

        // when
        boolean actualHasPermission = evaluator.hasPermissionToAccessUser(authenticationMock, userMock, accessedUserId);

        // then
        assertThat(actualHasPermission).isTrue();
    }

    @Test
    public void hasPermissionToAccessUserReturnsTrueWhenNoSameIdAndUserNotExists() {
        // given
        String accessedUserId = "other";

        given(userMock.getId()).willReturn(USER_ID);

        given(userService.userExists(accessedUserId)).willReturn(false);

        // when
        boolean actualHasPermission = evaluator.hasPermissionToAccessUser(authenticationMock, userMock, accessedUserId);

        // then
        assertThat(actualHasPermission).isTrue();
    }

    @Test
    public void hasPermissionToAccessUserReturnsFalseWhenNoSameIdAndNoStaffMember() {
        // given
        String accessedUserId = "other";
        User accessedUserMock = mock(User.class);

        given(userMock.getId()).willReturn(USER_ID);

        given(userService.userExists(accessedUserId)).willReturn(true);
        given(userService.getUserById(accessedUserId)).willReturn(accessedUserMock);

        given(userService.userHasStaffMember(userMock, accessedUserMock)).willReturn(false);

        // when
        boolean actualHasPermission = evaluator.hasPermissionToAccessUser(authenticationMock, userMock, accessedUserId);

        // then
        assertThat(actualHasPermission).isFalse();
    }

    @Test
    public void hasPermissionToEditProjectReturnsTrueWhenUserOwnsProject() {
        // given
        given(projectService.projectExists(PROJECT_ID)).willReturn(true);
        given(projectService.getProjectById(PROJECT_ID)).willReturn(projectMock);

        given(userProjectService.userOwnsProject(userMock, projectMock)).willReturn(true);

        // when
        boolean actualHasPermission = evaluator.hasPermissionToEditProject(authenticationMock, userMock, PROJECT_ID);

        // then
        assertThat(actualHasPermission).isTrue();
    }

    @Test
    public void hasPermissionToEditProjectReturnsTrueWhenProjectNotExists() {
        // given
        given(projectService.projectExists(PROJECT_ID)).willReturn(false);

        // when
        boolean actualHasPermission = evaluator.hasPermissionToEditProject(authenticationMock, userMock, PROJECT_ID);

        // then
        assertThat(actualHasPermission).isTrue();
    }

    @Test
    public void hasPermissionToEditProjectReturnsFalseWhenUserDoesNotOwnProject() {
        // given
        given(projectService.projectExists(PROJECT_ID)).willReturn(true);
        given(projectService.getProjectById(PROJECT_ID)).willReturn(projectMock);

        given(userProjectService.userOwnsProject(userMock, projectMock)).willReturn(false);

        // when
        boolean actualHasPermission = evaluator.hasPermissionToEditProject(authenticationMock, userMock, PROJECT_ID);

        // then
        assertThat(actualHasPermission).isFalse();
    }

    @Test
    public void hasElevatedAccessToUserReturnsTrueWhenUserDoesNotExist() {
        // given
        given(userService.userExists(USER_ID)).willReturn(false);

        // when
        boolean actualHasAccess = evaluator.hasElevatedAccessToUser(authenticationMock, userMock, USER_ID);

        // then
        assertThat(actualHasAccess).isTrue();
    }

    @Test
    public void hasElevatedAccessToUserReturnsTrueWhenUserIsStaffMember() {
        // given
        String accessedUserId = "other";
        User accessedUserMock = mock(User.class);

        given(userService.getUserById(accessedUserId)).willReturn(accessedUserMock);
        given(userService.userExists(accessedUserId)).willReturn(true);
        given(userService.userHasStaffMember(userMock, accessedUserMock)).willReturn(true);

        // when
        boolean actualHasPermission = evaluator.hasElevatedAccessToUser(authenticationMock, userMock, accessedUserId);

        // then
        assertThat(actualHasPermission).isTrue();
    }

    @Test
    public void hasElevatedAccessToUserReturnsFalseWhenUserIsNoStaffMember() {
        // given
        String accessedUserId = "other";
        User accessedUserMock = mock(User.class);

        given(userService.getUserById(accessedUserId)).willReturn(accessedUserMock);
        given(userService.userExists(accessedUserId)).willReturn(true);
        given(userService.userHasStaffMember(userMock, accessedUserMock)).willReturn(false);

        // when
        boolean actualHasPermission = evaluator.hasElevatedAccessToUser(authenticationMock, userMock, accessedUserId);

        // then
        assertThat(actualHasPermission).isFalse();
    }

    @Test
    public void hasPermissionToCreateProjectsReturnsFalseWhenNoManager() {
        // given
        given(userService.userIsManager(userMock)).willReturn(false);

        // when
        boolean actualHasPermission = evaluator.hasPermissionToCreateProjects(authenticationMock, userMock);

        // then
        assertThat(actualHasPermission).isFalse();
    }

    @Test
    public void hasPermissionToCreateProjectsReturnsTrueWhenUserIsManager() {
        // given
        given(userService.userIsManager(userMock)).willReturn(true);

        // when
        boolean actualHasPermission = evaluator.hasPermissionToCreateProjects(authenticationMock, userMock);

        // then
        assertThat(actualHasPermission).isTrue();
    }

}