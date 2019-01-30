package de.adesso.projectboard.base.projection;

import de.adesso.projectboard.base.user.persistence.User;
import de.adesso.projectboard.base.user.service.UserAuthService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.projection.ProjectionFactory;

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