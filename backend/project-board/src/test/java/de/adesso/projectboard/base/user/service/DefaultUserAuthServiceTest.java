package de.adesso.projectboard.base.user.service;

import de.adesso.projectboard.base.security.AuthenticationInfoRetriever;
import de.adesso.projectboard.base.user.persistence.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class DefaultUserAuthServiceTest {

    private final String USER_ID = "user";

    @Mock
    private UserService userServiceMock;

    @Mock
    private AuthenticationInfoRetriever retrieverMock;

    @Mock
    private User userMock;

    @Mock
    private User otherUserMock;

    private DefaultUserAuthService userAuthService;

    @Before
    public void setUp() {
        this.userAuthService = new DefaultUserAuthService(userServiceMock, retrieverMock);
    }

    @Test
    public void getAuthenticatedUserReturnsUserWithIdOfAuthenticatedUser() {
        // given
        given(retrieverMock.getUserId()).willReturn(USER_ID);
        given(userServiceMock.getUserById(USER_ID)).willReturn(userMock);

        // when
        var actualUser = userAuthService.getAuthenticatedUser();

        // then
        assertThat(actualUser).isEqualTo(userMock);
    }

    @Test
    public void getAuthenticatedUserIdReturnsIdOfAuthenticatedUser() {
        // given
        given(retrieverMock.getUserId()).willReturn(USER_ID);

        // when
        var actualId = userAuthService.getAuthenticatedUserId();

        // then
        assertThat(actualId).isEqualTo(USER_ID);
    }

    @Test
    public void authenticatedUserIsAdminReturnsTrueWhenUserHasAdminRole() {
        // given
        given(retrieverMock.hasAdminRole()).willReturn(true);

        // when
        boolean actualIsAdmin = userAuthService.authenticatedUserIsAdmin();

        // then
        assertThat(actualIsAdmin).isTrue();
    }

    @Test
    public void authenticatedUserIsAdminReturnsFalseWhenDoesNotHaveAdminRole() {
        // given
        given(retrieverMock.hasAdminRole()).willReturn(false);

        // when
        boolean actualIsAdmin = userAuthService.authenticatedUserIsAdmin();

        // then
        assertThat(actualIsAdmin).isFalse();
    }

    @Test
    public void userHasAccessToAllProjectFieldsReturnsFalseWhenUserIsNoManagerAndNoAdmin() {
        // given
        given(retrieverMock.getUserId()).willReturn(USER_ID);

        given(userServiceMock.userIsManager(userMock)).willReturn(false);
        given(userServiceMock.getUserById(USER_ID)).willReturn(userMock);

        // when
        boolean actualHasAccess = userAuthService.userHasAccessToAllProjectFields(userMock);

        // then
        assertThat(actualHasAccess).isFalse();
    }

    @Test
    public void userHasAccessToAllProjectFieldsReturnsTrueWhenUserIsManager() {
        // given
        given(retrieverMock.getUserId()).willReturn(USER_ID);

        given(userServiceMock.userIsManager(userMock)).willReturn(true);
        given(userServiceMock.getUserById(USER_ID)).willReturn(userMock);

        // when
        boolean actualHasAccess = userAuthService.userHasAccessToAllProjectFields(userMock);

        // then
        assertThat(actualHasAccess).isTrue();
    }

    @Test
    public void userHasAccessToAllProjectFieldsReturnsFalseWhenAuthenticatedUserIsAdminButUserDoesNotEqual() {
        // given
        given(retrieverMock.getUserId()).willReturn(USER_ID);

        given(userServiceMock.getUserById(USER_ID)).willReturn(userMock);

        // when
        boolean actualHasAccess = userAuthService.userHasAccessToAllProjectFields(otherUserMock);

        // then
        assertThat(actualHasAccess).isFalse();
    }

}