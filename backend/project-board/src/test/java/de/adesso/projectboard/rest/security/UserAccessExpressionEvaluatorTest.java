package de.adesso.projectboard.rest.security;

import de.adesso.projectboard.base.access.service.UserAccessService;
import de.adesso.projectboard.base.application.service.ApplicationService;
import de.adesso.projectboard.base.configuration.ProjectBoardConfigurationProperties;
import de.adesso.projectboard.base.project.persistence.Project;
import de.adesso.projectboard.base.project.service.ProjectService;
import de.adesso.projectboard.base.user.persistence.User;
import de.adesso.projectboard.base.user.persistence.data.UserData;
import de.adesso.projectboard.base.user.service.BookmarkService;
import de.adesso.projectboard.base.user.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.Authentication;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class UserAccessExpressionEvaluatorTest {

    private final String PROJECT_ID = "project";

    private final String USER_ID = "user";

    private final String LOB_DEPENDENT_STATUS = "open";

    private final String LOB_INDEPENDENT_STATUS = "escalated";

    @Mock
    private UserService userServiceMock;

    @Mock
    private ProjectService projectServiceMock;

    @Mock
    private ApplicationService applicationServiceMock;

    @Mock
    private UserAccessService userAccessServiceMock;

    @Mock
    private BookmarkService bookmarkServiceMock;

    @Mock
    private Authentication authenticationMock;

    @Mock
    private User userMock;

    @Mock
    private Project projectMock;

    @Mock
    private UserData userDataMock;

    @Mock
    private ProjectBoardConfigurationProperties propertiesMock;

    private UserAccessExpressionEvaluator evaluator;

    @Before
    public void setUp() {
        given(propertiesMock.getLobDependentStatus()).willReturn(List.of(LOB_DEPENDENT_STATUS));
        given(propertiesMock.getLobIndependentStatus()).willReturn(List.of(LOB_INDEPENDENT_STATUS));

        this.evaluator = new UserAccessExpressionEvaluator(userServiceMock, userAccessServiceMock,
                projectServiceMock, applicationServiceMock, bookmarkServiceMock, propertiesMock);
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
        given(projectServiceMock.projectExists(PROJECT_ID)).willReturn(false);

        // when
        boolean actualHasAccess = evaluator.hasAccessToProject(authenticationMock, userMock, PROJECT_ID);

        // then
        assertThat(actualHasAccess).isTrue();
    }

    @Test
    public void hasAccessToProjectReturnsTrueWhenUserHasBookmarkedProject() {
        // given
        var projectStatus = "anything";

        given(projectServiceMock.projectExists(PROJECT_ID)).willReturn(true);
        given(projectServiceMock.getProjectById(PROJECT_ID)).willReturn(projectMock);
        given(projectMock.getStatus()).willReturn(projectStatus);

        given(bookmarkServiceMock.userHasBookmark(userMock, projectMock)).willReturn(true);

        // when
        boolean actualHasAccess = evaluator.hasAccessToProject(authenticationMock, userMock, PROJECT_ID);

        // then
        assertThat(actualHasAccess).isTrue();
    }

    @Test
    public void hasAccessToProjectReturnsTrueWhenStatusIsLobIndependent() {
        // given
        given(projectServiceMock.projectExists(PROJECT_ID)).willReturn(true);
        given(projectServiceMock.getProjectById(PROJECT_ID)).willReturn(projectMock);
        given(projectMock.getStatus()).willReturn(LOB_INDEPENDENT_STATUS.toUpperCase());

        // when
        boolean actualHasAccess = evaluator.hasAccessToProject(authenticationMock, userMock, PROJECT_ID);

        // then
        assertThat(actualHasAccess).isTrue();
    }

    @Test
    public void hasAccessToProjectReturnsTrueWhenStatusIsLobDependentAndUserHasSameLobAndHasAccess() {
        // given
        var lob = "LOB Prod";

        given(projectServiceMock.projectExists(PROJECT_ID)).willReturn(true);
        given(projectServiceMock.getProjectById(PROJECT_ID)).willReturn(projectMock);
        given(projectMock.getStatus()).willReturn(LOB_DEPENDENT_STATUS.toUpperCase());
        given(projectMock.getLob()).willReturn(lob);

        given(userServiceMock.getUserData(userMock)).willReturn(userDataMock);
        given(userDataMock.getLob()).willReturn(lob);

        given(userAccessServiceMock.userHasActiveAccessInterval(userMock)).willReturn(true);

        // when
        boolean actualHasAccess = evaluator.hasAccessToProject(authenticationMock, userMock, PROJECT_ID);

        // then
        assertThat(actualHasAccess).isTrue();
    }

    @Test
    public void hasAccessToProjectReturnsTrueWhenStatusIsLobDependentAndUserIsManager() {
        // given
        given(projectServiceMock.projectExists(PROJECT_ID)).willReturn(true);
        given(projectServiceMock.getProjectById(PROJECT_ID)).willReturn(projectMock);
        given(projectMock.getStatus()).willReturn(LOB_DEPENDENT_STATUS.toUpperCase());

        given(userServiceMock.userIsManager(userMock)).willReturn(true);

        // when
        boolean actualHasAccess = evaluator.hasAccessToProject(authenticationMock, userMock, PROJECT_ID);

        // then
        assertThat(actualHasAccess).isTrue();
    }

    @Test
    public void hasAccessToProjectReturnsTrueWhenStatusIsLobDependentAndLobIsNullAndUserHasAccess() {
        // given
        given(projectServiceMock.projectExists(PROJECT_ID)).willReturn(true);
        given(projectServiceMock.getProjectById(PROJECT_ID)).willReturn(projectMock);
        given(projectMock.getStatus()).willReturn(LOB_DEPENDENT_STATUS.toUpperCase());
        given(projectMock.getLob()).willReturn(null);

        given(userServiceMock.getUserData(userMock)).willReturn(userDataMock);
        given(userDataMock.getLob()).willReturn("Anything");

        given(userAccessServiceMock.userHasActiveAccessInterval(userMock)).willReturn(true);

        // when
        boolean actualHasAccess = evaluator.hasAccessToProject(authenticationMock, userMock, PROJECT_ID);

        // then
        assertThat(actualHasAccess).isTrue();
    }

    @Test
    public void hasAccessToProjectReturnsFalseWhenStatusIsLobDependentAndUserHasDifferentLobAndHasAccess() {
        // given
        var userLob = "LOB Test";
        var projectLob = "LOB Prod";

        given(projectServiceMock.projectExists(PROJECT_ID)).willReturn(true);
        given(projectServiceMock.getProjectById(PROJECT_ID)).willReturn(projectMock);
        given(projectMock.getStatus()).willReturn(LOB_DEPENDENT_STATUS);
        given(projectMock.getLob()).willReturn(projectLob);

        given(userServiceMock.getUserData(userMock)).willReturn(userDataMock);
        given(userDataMock.getLob()).willReturn(userLob);

        given(userAccessServiceMock.userHasActiveAccessInterval(userMock)).willReturn(true);

        // when
        boolean actualHasAccess = evaluator.hasAccessToProject(authenticationMock, userMock, PROJECT_ID);

        // then
        assertThat(actualHasAccess).isFalse();
    }

    @Test
    public void hasAccessToProjectReturnsTrueWhenUserHasDifferentLobAndAppliedForProjectAndHasAccess() {
        // given
        var userLob = "LOB Test";
        var projectLob = "LOB Prod";

        given(projectServiceMock.projectExists(PROJECT_ID)).willReturn(true);
        given(projectServiceMock.getProjectById(PROJECT_ID)).willReturn(projectMock);
        given(projectMock.getStatus()).willReturn(LOB_DEPENDENT_STATUS.toUpperCase());
        given(projectMock.getLob()).willReturn(projectLob);

        given(userServiceMock.getUserData(userMock)).willReturn(userDataMock);
        given(userDataMock.getLob()).willReturn(userLob);

        given(applicationServiceMock.userHasAppliedForProject(userMock, projectMock)).willReturn(true);

        given(userAccessServiceMock.userHasActiveAccessInterval(userMock)).willReturn(true);

        // when
        boolean actualHasAccess = evaluator.hasAccessToProject(authenticationMock, userMock, PROJECT_ID);

        // then
        assertThat(actualHasAccess).isTrue();
    }

    @Test
    public void hasAccessToProjectReturnsTrueWhenUserHasNotAppliedForProjectAndHasNoAccess() {
        // given
        given(projectServiceMock.projectExists(PROJECT_ID)).willReturn(true);
        given(projectServiceMock.getProjectById(PROJECT_ID)).willReturn(projectMock);
        given(projectMock.getStatus()).willReturn(LOB_DEPENDENT_STATUS.toUpperCase());

        // when
        boolean actualHasAccess = evaluator.hasAccessToProject(authenticationMock, userMock, PROJECT_ID);

        // then
        assertThat(actualHasAccess).isFalse();
    }

    @Test
    public void hasPermissionToApplyToProjectReturnsTrueWhenProjectDoesNotExist() {
        // given
        given(projectServiceMock.projectExists(PROJECT_ID)).willReturn(false);

        // when
        boolean actualHasPermission = evaluator.hasPermissionToApplyToProject(authenticationMock, userMock, PROJECT_ID);

        // then
        assertThat(actualHasPermission).isTrue();
    }

    @Test
    public void hasPermissionToApplyToProjectReturnsTrueWhenProjectHasLobIndependentStatus() {
        // given
        given(projectServiceMock.projectExists(PROJECT_ID)).willReturn(true);
        given(projectServiceMock.getProjectById(PROJECT_ID)).willReturn(projectMock);
        given(projectMock.getStatus()).willReturn(LOB_INDEPENDENT_STATUS);

        // when
        boolean actualHasPermission = evaluator.hasPermissionToApplyToProject(authenticationMock, userMock, PROJECT_ID);

        // then
        assertThat(actualHasPermission).isTrue();
    }

    @Test
    public void hasPermissionToApplyToProjectReturnsTrueWhenProjectHasLobDependentStatusAndUserIsManager() {
        // given
        given(projectServiceMock.projectExists(PROJECT_ID)).willReturn(true);
        given(projectServiceMock.getProjectById(PROJECT_ID)).willReturn(projectMock);
        given(projectMock.getStatus()).willReturn(LOB_DEPENDENT_STATUS);

        given(userServiceMock.userIsManager(userMock)).willReturn(true);

        // when
        boolean actualHasPermission = evaluator.hasPermissionToApplyToProject(authenticationMock, userMock, PROJECT_ID);

        // then
        assertThat(actualHasPermission).isTrue();
    }

    @Test
    public void hasPermissionToApplyToProjectReturnsTrueWhenProjectHasLobDependentAndUserHasSameLobAndHasAccess() {
        // given
        var lob = "LOB Test";

        given(projectServiceMock.projectExists(PROJECT_ID)).willReturn(true);
        given(projectServiceMock.getProjectById(PROJECT_ID)).willReturn(projectMock);

        given(projectMock.getStatus()).willReturn(LOB_DEPENDENT_STATUS);
        given(projectMock.getLob()).willReturn(lob);

        given(userServiceMock.getUserData(userMock)).willReturn(userDataMock);
        given(userDataMock.getLob()).willReturn(lob);

        given(userAccessServiceMock.userHasActiveAccessInterval(userMock)).willReturn(true);

        // when
        boolean actualHasPermission = evaluator.hasPermissionToApplyToProject(authenticationMock, userMock, PROJECT_ID);

        // then
        assertThat(actualHasPermission).isTrue();
    }

    @Test
    public void hasPermissionToApplyToProjectReturnsTrueWhenProjectHasLobDependentStatusAndLobIsNullAndUserHasAccess() {
        // given
        given(projectServiceMock.projectExists(PROJECT_ID)).willReturn(true);
        given(projectServiceMock.getProjectById(PROJECT_ID)).willReturn(projectMock);

        given(projectMock.getStatus()).willReturn(LOB_DEPENDENT_STATUS);

        given(userServiceMock.getUserData(userMock)).willReturn(userDataMock);

        given(userAccessServiceMock.userHasActiveAccessInterval(userMock)).willReturn(true);

        // when
        boolean actualHasPermission = evaluator.hasPermissionToApplyToProject(authenticationMock, userMock, PROJECT_ID);

        // then
        assertThat(actualHasPermission).isTrue();
    }

    @Test
    public void hasPermissionToApplyToProjectReturnsTrueWhenProjectHasLobDependentStatusAndUserHasBookmarkedProject() {
        // given
        given(projectServiceMock.projectExists(PROJECT_ID)).willReturn(true);
        given(projectServiceMock.getProjectById(PROJECT_ID)).willReturn(projectMock);

        given(projectMock.getStatus()).willReturn(LOB_DEPENDENT_STATUS);

        given(bookmarkServiceMock.userHasBookmark(userMock, projectMock)).willReturn(true);

        // when
        boolean actualHasPermission = evaluator.hasPermissionToApplyToProject(authenticationMock, userMock, PROJECT_ID);

        // then
        assertThat(actualHasPermission).isTrue();
    }

    @Test
    public void hasPermissionToApplyToProjectReturnsTrueWhenUserHasAppliedForProject() {
        // given
        var lob = "anything";

        given(projectServiceMock.projectExists(PROJECT_ID)).willReturn(true);
        given(projectServiceMock.getProjectById(PROJECT_ID)).willReturn(projectMock);

        given(projectMock.getStatus()).willReturn(lob);

        given(applicationServiceMock.userHasAppliedForProject(userMock, projectMock)).willReturn(true);

        // when
        boolean actualHasPermission = evaluator.hasPermissionToApplyToProject(authenticationMock, userMock, PROJECT_ID);

        // then
        assertThat(actualHasPermission).isTrue();
    }

    @Test
    public void hasPermissionToApplyToProjectReturnsTrueWhenUserHasBookmarkedProject() {
        // given
        var lob = "anything";

        given(projectServiceMock.projectExists(PROJECT_ID)).willReturn(true);
        given(projectServiceMock.getProjectById(PROJECT_ID)).willReturn(projectMock);

        given(projectMock.getStatus()).willReturn(lob);

        given(bookmarkServiceMock.userHasBookmark(userMock, projectMock)).willReturn(true);

        // when
        boolean actualHasPermission = evaluator.hasPermissionToApplyToProject(authenticationMock, userMock, PROJECT_ID);

        // then
        assertThat(actualHasPermission).isTrue();
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
