package de.adesso.projectboard.base.projection;

import de.adesso.projectboard.base.user.persistence.User;
import de.adesso.projectboard.base.user.service.UserAuthService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.projection.ProjectionFactory;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class BaseProjectionFactoryTest {

    @Mock
    private UserAuthService userAuthServiceMock;

    @Mock
    private ProjectionFactory projectionFactoryMock;

    @Mock
    private User userMock;

    private BaseProjectionFactory baseProjectionFactory;

    @Before
    public void setUp() {
        this.baseProjectionFactory = new BaseProjectionFactory(projectionFactoryMock, userAuthServiceMock);
    }

    @Test
    public void createProjectionReturnsProjectionOfArgument() {
        // given
        var projection = Integer.class;

        var given = "Given";
        var expected = 1337;

        given(projectionFactoryMock.createProjection(projection, given)).willReturn(expected);

        // when
        var actual = baseProjectionFactory.createProjection(given, projection);

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void createProjectionsReturnsProjectionAllElement() {
        // given
        var projection = Integer.class;

        var firstGiven = "First Given";
        var secondGiven = "Second Given";
        var givenValues = List.of(firstGiven, secondGiven);

        var firstExpected = 1234;
        var secondExpected = 5678;
        var expectedValues = List.of(firstExpected, secondExpected);

        given(projectionFactoryMock.createProjection(projection, firstGiven)).willReturn(firstExpected);
        given(projectionFactoryMock.createProjection(projection, secondGiven)).willReturn(secondExpected);

        // when
        var actual = baseProjectionFactory.createProjections(givenValues, projection);

        // then
        assertThat(actual).containsExactlyElementsOf(expectedValues);
    }

    @Test
    public void createProjectionForAuthenticatedUserReturnsNormalProjectionWhenUserNotAManager() {
        // given
        var normalProjection = String.class;
        var managerProjection = Integer.class;

        var given = "Given";
        var expected = "Expected";

        given(userAuthServiceMock.getAuthenticatedUser()).willReturn(userMock);
        given(userAuthServiceMock.userIsEffectivelyAManager(userMock)).willReturn(false);

        given(projectionFactoryMock.createProjection(normalProjection, given)).willReturn(expected);

        // when
        var actual = baseProjectionFactory.createProjectionForAuthenticatedUser(given,
                normalProjection, managerProjection);

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void createProjectionForAuthenticatedUserReturnsManagerProjectionWhenUserIsAManager() {
        // given
        var normalProjection = String.class;
        var managerProjection = Integer.class;

        var given = "Given";
        var expected = 123;

        given(userAuthServiceMock.getAuthenticatedUser()).willReturn(userMock);
        given(userAuthServiceMock.userIsEffectivelyAManager(userMock)).willReturn(true);

        given(projectionFactoryMock.createProjection(managerProjection, given)).willReturn(expected);

        // when
        var actual = baseProjectionFactory.createProjectionForAuthenticatedUser(given,
                normalProjection, managerProjection);

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void createProjectionsForAuthenticatedUserReturnsNormalProjectionsWhenUserNotAManager_Collection() {
        // given
        var normalProjection = String.class;
        var managerProjection = Integer.class;

        var firstExpected = "Expected First";
        var secondExpected = "Expected Second";
        var expectedValues = List.of(firstExpected, secondExpected);

        var firstGiven = "Given First";
        var secondGiven = "Given Second";
        var givenValues = List.of(firstGiven, secondGiven);

        given(userAuthServiceMock.getAuthenticatedUser()).willReturn(userMock);
        given(userAuthServiceMock.userIsEffectivelyAManager(userMock)).willReturn(false);

        given(projectionFactoryMock.createProjection(normalProjection, firstGiven)).willReturn(firstExpected);
        given(projectionFactoryMock.createProjection(normalProjection, secondGiven)).willReturn(secondExpected);

        // when
        var actualValues = baseProjectionFactory.createProjectionsForAuthenticatedUser(givenValues,
                normalProjection, managerProjection);

        // then
        assertThat(actualValues).isEqualTo(expectedValues);
    }

    @Test
    public void createProjectionsForAuthenticatedUserReturnsManagerProjectionsWhenUserIsAManager_Collection() {
        // given
        var normalProjection = String.class;
        var managerProjection = Integer.class;

        var firstExpected = 1;
        var secondExpected = 2;
        var expectedValues = List.of(firstExpected, secondExpected);

        var firstGiven = "Given First";
        var secondGiven = "Given Second";
        var givenValues = List.of(firstGiven, secondGiven);

        given(userAuthServiceMock.getAuthenticatedUser()).willReturn(userMock);
        given(userAuthServiceMock.userIsEffectivelyAManager(userMock)).willReturn(true);

        given(projectionFactoryMock.createProjection(managerProjection, firstGiven)).willReturn(firstExpected);
        given(projectionFactoryMock.createProjection(managerProjection, secondGiven)).willReturn(secondExpected);

        // when
        var actualValues = baseProjectionFactory.createProjectionsForAuthenticatedUser(givenValues,
                normalProjection, managerProjection);

        // then
        assertThat(actualValues).isEqualTo(expectedValues);
    }

    @Test
    public void createProjectionsForAuthenticatedUserReturnsNormalProjectionsWhenUserNotAManager_Page() {
        // given
        var normalProjection = String.class;
        var managerProjection = Integer.class;

        var firstExpected = "First Expected";
        var secondExpected = "Second Expected";
        var expectedPage = new PageImpl<>(List.of(firstExpected, secondExpected));

        var firstGiven = "Given First";
        var secondGiven = "Given Second";
        var givenPage = new PageImpl<>(List.of(firstGiven, secondGiven));

        given(userAuthServiceMock.getAuthenticatedUser()).willReturn(userMock);
        given(userAuthServiceMock.userIsEffectivelyAManager(userMock)).willReturn(false);

        given(projectionFactoryMock.createProjection(normalProjection, firstGiven)).willReturn(firstExpected);
        given(projectionFactoryMock.createProjection(normalProjection, secondGiven)).willReturn(secondExpected);

        // when
        var actualPage = baseProjectionFactory.createProjectionsForAuthenticatedUser(givenPage,
                normalProjection, managerProjection);

        // then
        assertThat((Object) actualPage).isEqualTo(expectedPage);
    }

    @Test
    public void createProjectionsForAuthenticatedUserReturnsManagerProjectionsWhenUserIsAManager_Page() {
        // given
        var normalProjection = String.class;
        var managerProjection = Integer.class;

        var firstExpected = 1;
        var secondExpected = 2;
        var expectedPage = new PageImpl<>(List.of(firstExpected, secondExpected));

        var firstGiven = "Given First";
        var secondGiven = "Given Second";
        var givenPage = new PageImpl<>(List.of(firstGiven, secondGiven));

        given(userAuthServiceMock.getAuthenticatedUser()).willReturn(userMock);
        given(userAuthServiceMock.userIsEffectivelyAManager(userMock)).willReturn(true);

        given(projectionFactoryMock.createProjection(managerProjection, firstGiven)).willReturn(firstExpected);
        given(projectionFactoryMock.createProjection(managerProjection, secondGiven)).willReturn(secondExpected);

        // when
        var actualPage = baseProjectionFactory.createProjectionsForAuthenticatedUser(givenPage,
                normalProjection, managerProjection);

        // then
        assertThat((Object) actualPage).isEqualTo(expectedPage);
    }

    @Test
    public void getProjectionTypeReturnsNormalTypeWhenUserNotAManager() {
        // given
        var normalProjection = Integer.class;
        var managerProjection = String.class;

        given(userAuthServiceMock.getAuthenticatedUser()).willReturn(userMock);
        given(userAuthServiceMock.userIsEffectivelyAManager(userMock)).willReturn(false);

        // when
        var actualProjection = baseProjectionFactory.getProjectionType(normalProjection, managerProjection);

        // then
        assertThat(actualProjection).isEqualTo(normalProjection);
    }

    @Test
    public void getProjectionTypeReturnsManagerTypeWhenUserIsAManager() {
        // given
        var normalProjection = Integer.class;
        var managerProjection = String.class;

        given(userAuthServiceMock.getAuthenticatedUser()).willReturn(userMock);
        given(userAuthServiceMock.userIsEffectivelyAManager(userMock)).willReturn(true);

        // when
        var actualProjection = baseProjectionFactory.getProjectionType(normalProjection, managerProjection);

        // then
        assertThat(actualProjection).isEqualTo(managerProjection);
    }

}