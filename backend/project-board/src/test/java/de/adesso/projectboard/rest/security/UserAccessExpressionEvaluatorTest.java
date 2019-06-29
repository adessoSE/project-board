package de.adesso.projectboard.rest.security;

import de.adesso.projectboard.base.access.service.UserAccessService;
import de.adesso.projectboard.base.application.service.ApplicationService;
import de.adesso.projectboard.base.project.persistence.Project;
import de.adesso.projectboard.base.project.service.ProjectService;
import de.adesso.projectboard.base.user.persistence.User;
import de.adesso.projectboard.base.user.persistence.data.UserData;
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
    private UserService userServiceMock;

    @Mock
    private ProjectService projectServiceMock;

    @Mock
    private ApplicationService applicationServiceMock;

    @Mock
    private UserAccessService userAccessServiceMock;

    @Mock
    private Authentication authenticationMock;

    @Mock
    private User userMock;

    @Mock
    private Project projectMock;

    @Mock
    private UserData userDataMock;

    private UserAccessExpressionEvaluator evaluator;

    @Before
    public void setUp() {
        this.evaluator = new UserAccessExpressionEvaluator(userServiceMock, userAccessServiceMock, projectServiceMock, applicationServiceMock);
    }

    @Test
    public void hasAccessToProjectsReturnsTrueWhenUserIsManager() {
        // given
        given(userServiceMock.userIsManager(userMock)).willReturn(true);

        // when
        boolean actualHasAccess = evaluator.hasAccessToProjects(authenticationMock, userMock);

        // then
        assertThat(actualHasAccess).isTrue();
    }

    @Test
    public void hasAccessToProjectsReturnsTrueWhenUserIsNoManagerButAccessIsActive() {
        // given
        given(userAccessServiceMock.userHasActiveAccessInterval(userMock)).willReturn(true);

        // when
        boolean actualHasAccess = evaluator.hasAccessToProjects(authenticationMock, userMock);

        // then
        assertThat(actualHasAccess).isTrue();
    }

    @Test
    public void hasAccessToProjectsReturnsFalseWhenUserIsNoManagerAndNoAccess() {
        // given

        // when
        boolean actualHasAccess = evaluator.hasAccessToProjects(authenticationMock, userMock);

        // then
        assertThat(actualHasAccess).isFalse();
    }

    @Test
    public void hasAccessToProjectReturnsTrueWhenProjectDoesNotExist() {
        // given

        // when
        boolean actualHasAccess = evaluator.hasAccessToProject(authenticationMock, userMock, PROJECT_ID);

        // then
        assertThat(actualHasAccess).isTrue();
    }

    @Test
    public void hasAccessToProjectReturnsTrueWhenProjectExistsAndUserIsManager() {
        // given
        given(projectServiceMock.projectExists(PROJECT_ID)).willReturn(true);

        given(userServiceMock.userIsManager(userMock)).willReturn(true);

        // when
        boolean actualHasAccess = evaluator.hasAccessToProject(authenticationMock, userMock, PROJECT_ID);

        // then
        assertThat(actualHasAccess).isTrue();
    }

    @Test
    public void hasAccessToProjectReturnsTrueWhenUserHasAccessAndSameLobAsProject() {
        // given
        var expectedLob = "LOB Test";

        given(userServiceMock.getUserData(userMock)).willReturn(userDataMock);
        given(userDataMock.getLob()).willReturn(expectedLob);

        given(projectServiceMock.projectExists(PROJECT_ID)).willReturn(true);
        given(projectServiceMock.getProjectById(PROJECT_ID)).willReturn(projectMock);
        given(projectMock.getLob()).willReturn(expectedLob);

        given(userAccessServiceMock.userHasActiveAccessInterval(userMock)).willReturn(true);

        // when
        var actualHasAccess = evaluator.hasAccessToProject(authenticationMock, userMock, PROJECT_ID);

        // then
        assertThat(actualHasAccess).isTrue();
    }

    @Test
    public void hasAccessToProjectReturnsTrueWhenUserHasAccessAndBothLobsNull() {
        // given
        given(userServiceMock.getUserData(userMock)).willReturn(userDataMock);

        given(projectServiceMock.projectExists(PROJECT_ID)).willReturn(true);
        given(projectServiceMock.getProjectById(PROJECT_ID)).willReturn(projectMock);

        given(userAccessServiceMock.userHasActiveAccessInterval(userMock)).willReturn(true);

        // when
        var actualHasAccess = evaluator.hasAccessToProject(authenticationMock, userMock, PROJECT_ID);

        // then
        assertThat(actualHasAccess).isTrue();
    }

    @Test
    public void hasAccessToProjectReturnsTrueWhenUserHasAppliedForProject() {
        // given
        given(projectServiceMock.projectExists(PROJECT_ID)).willReturn(true);
        given(projectServiceMock.getProjectById(PROJECT_ID)).willReturn(projectMock);

        given(userServiceMock.getUserData(userMock)).willReturn(userDataMock);

        given(applicationServiceMock.userHasAppliedForProject(userMock, projectMock)).willReturn(true);

        // when
        boolean actualHasAccess = evaluator.hasAccessToProject(authenticationMock, userMock, PROJECT_ID);

        // then
        assertThat(actualHasAccess).isTrue();
    }

    @Test
    public void hasAccessToProjectReturnsFalseWhenProjectExistsAndNoManagerAndNoAccessAndNotApplied() {
        // given
        given(projectServiceMock.projectExists(PROJECT_ID)).willReturn(true);
        given(projectServiceMock.getProjectById(PROJECT_ID)).willReturn(projectMock);

        given(userServiceMock.getUserData(userMock)).willReturn(userDataMock);

        // when
        boolean actualHasAccess = evaluator.hasAccessToProject(authenticationMock, userMock, PROJECT_ID);

        // then
        assertThat(actualHasAccess).isFalse();
    }

    @Test
    public void hasPermissionToApplyReturnsTrueWhenUserIsManager() {
        // given
        given(userServiceMock.userIsManager(userMock)).willReturn(true);

        // when
        boolean actualHasPermission = evaluator.hasPermissionToApply(authenticationMock, userMock);

        // then
        assertThat(actualHasPermission).isTrue();
    }

    @Test
    public void hasPermissionToApplyReturnsTrueWhenAccessIsActive() {
        // given
        given(userAccessServiceMock.userHasActiveAccessInterval(userMock)).willReturn(true);

        // when
        boolean actualHasPermission = evaluator.hasPermissionToApply(authenticationMock, userMock);

        // then
        assertThat(actualHasPermission).isTrue();
    }

    @Test
    public void hasPermissionToApplyReturnsFalseWhenNoManagerAndAccessNotActive() {
        // given

        // when
        boolean actualHasPermission = evaluator.hasPermissionToApply(authenticationMock, userMock);

        // then
        assertThat(actualHasPermission).isFalse();
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

        given(userServiceMock.userExists(accessedUserId)).willReturn(true);
        given(userServiceMock.getUserById(accessedUserId)).willReturn(accessedUserMock);
        given(userServiceMock.userHasStaffMember(userMock, accessedUserMock)).willReturn(true);

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

        given(userServiceMock.userExists(accessedUserId)).willReturn(false);

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

        given(userServiceMock.userExists(accessedUserId)).willReturn(true);
        given(userServiceMock.getUserById(accessedUserId)).willReturn(accessedUserMock);

        given(userServiceMock.userHasStaffMember(userMock, accessedUserMock)).willReturn(false);

        // when
        boolean actualHasPermission = evaluator.hasPermissionToAccessUser(authenticationMock, userMock, accessedUserId);

        // then
        assertThat(actualHasPermission).isFalse();
    }

    @Test
    public void hasElevatedAccessToUserReturnsTrueWhenUserDoesNotExist() {
        // given
        given(userServiceMock.userExists(USER_ID)).willReturn(false);

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

        given(userServiceMock.getUserById(accessedUserId)).willReturn(accessedUserMock);
        given(userServiceMock.userExists(accessedUserId)).willReturn(true);
        given(userServiceMock.userHasStaffMember(userMock, accessedUserMock)).willReturn(true);

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

        given(userServiceMock.getUserById(accessedUserId)).willReturn(accessedUserMock);
        given(userServiceMock.userExists(accessedUserId)).willReturn(true);
        given(userServiceMock.userHasStaffMember(userMock, accessedUserMock)).willReturn(false);

        // when
        boolean actualHasPermission = evaluator.hasElevatedAccessToUser(authenticationMock, userMock, accessedUserId);

        // then
        assertThat(actualHasPermission).isFalse();
    }

}
