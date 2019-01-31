package de.adesso.projectboard.base.user.projection;

import de.adesso.projectboard.base.user.persistence.User;
import de.adesso.projectboard.base.user.persistence.data.UserData;
import de.adesso.projectboard.base.user.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.projection.ProjectionFactory;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class UserProjectionFactoryTest {

    @Mock
    private UserService userServiceMock;

    @Mock
    private ProjectionFactory projectionFactoryMock;

    @Mock
    private User userMock;

    @Mock
    private User otherUserMock;

    @Mock
    private UserData userDataMock;

    @Mock
    private UserData otherUserDataMock;

    private UserProjectionFactory userProjectionFactory;

    @Before
    public void setUp() {
        this.userProjectionFactory = new UserProjectionFactory(userServiceMock, projectionFactoryMock);
    }

    @Test
    public void createProjectionsReturnsExpectedProjections() {
        // given
        var projection = String.class;
        var userProjectionSource = new UserProjectionSource(userMock, userDataMock, true);
        var otherUserProjectionSource = new UserProjectionSource(otherUserMock, otherUserDataMock, false);

        var givenUsers = Set.of(userMock, otherUserMock);
        var givenUserData = List.of(userDataMock, otherUserDataMock);

        var expectedProjection = "User";
        var expectedOtherProjection = "Other User";

        given(userDataMock.getUser()).willReturn(userMock);
        given(otherUserDataMock.getUser()).willReturn(otherUserMock);

        given(userServiceMock.usersAreManagers(givenUsers)).willReturn(Map.of(
                userMock, true,
                otherUserMock, false
        ));

        given(projectionFactoryMock.createProjection(projection, userProjectionSource)).willReturn(expectedProjection);
        given(projectionFactoryMock.createProjection(projection, otherUserProjectionSource)).willReturn(expectedOtherProjection);

        // when
        var actualProjections = userProjectionFactory.createProjections(givenUserData, projection);

        // then
        assertThat(actualProjections).containsExactly(expectedProjection, expectedOtherProjection);
    }

    @Test
    public void createProjectionReturnsExpectedProjection() {
        // given
        var projection = Integer.class;
        var expectedProjection = 12345;
        var userProjectionSource = new UserProjectionSource(userMock, userDataMock, true);

        given(projectionFactoryMock.createProjection(projection, userProjectionSource)).willReturn(expectedProjection);

        given(userServiceMock.getUserData(userMock)).willReturn(userDataMock);
        given(userServiceMock.userIsManager(userMock)).willReturn(true);

        // when
        var actualProjection = userProjectionFactory.createProjection(userMock, projection);

        // then
        assertThat(actualProjection).isEqualTo(expectedProjection);
    }

}
