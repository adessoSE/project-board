package de.adesso.projectboard.base.projection;

import de.adesso.projectboard.base.configuration.ProjectBoardConfigurationProperties;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class ProjectionTypeArgumentResolverTest {

    private final String REQUEST_PARAM_NAME = "projection";

    @Mock
    private ProjectionService projectionServiceMock;

    @Mock
    private ProjectBoardConfigurationProperties propertiesMock;

    @Mock
    private MethodParameter methodParameterMock;

    @Mock
    private NativeWebRequest nativeWebRequestMock;

    @Mock
    private ProjectionType projectionTypeMock;

    private ProjectionTypeArgumentResolver argumentResolver;

    @Before
    public void setUp() {
        given(propertiesMock.getProjectionNameRequestParameter()).willReturn(REQUEST_PARAM_NAME);

        this.argumentResolver = new ProjectionTypeArgumentResolver(projectionServiceMock, propertiesMock);
    }

    @Test
    public void supportsParameterReturnsTrueWhenAnnotationPresent() {
        // given
        given(methodParameterMock.getParameterAnnotation(ProjectionType.class)).willReturn(projectionTypeMock);

        // when / then
        assertThat(argumentResolver.supportsParameter(methodParameterMock)).isTrue();
    }

    @Test
    public void supportsParameterReturnsFalseWhenNotAnnotated() {
        // given
        given(methodParameterMock.getParameterAnnotation(ProjectionType.class)).willReturn(null);

        // when / then
        assertThat(argumentResolver.supportsParameter(methodParameterMock)).isFalse();
    }

    @Test
    public void resolveArgumentReturnsDefaultProjectionWhenProjectionNameNull() {
        // given / when / then
        assertDefaultProjectionReturned(null);
    }

    @Test
    public void resolveArgumentReturnsDefaultProjectionWhenProjectionNameEmpty() {
        // given / when / then
        assertDefaultProjectionReturned("");
    }

    @Test
    public void resolveArgumentReturnsDefaultProjectionWhenProjectionNotNullOrEmpty() {
        // given
        var expectedProjection = (Class) Iterable.class;
        var expectedTarget = (Class) String.class;
        var expectedRequestParamValue = "cool-projection";

        given(nativeWebRequestMock.getParameter(REQUEST_PARAM_NAME)).willReturn(expectedRequestParamValue);
        given(methodParameterMock.getParameterAnnotation(ProjectionType.class)).willReturn(projectionTypeMock);
        given(projectionTypeMock.value()).willReturn(expectedTarget);

        given(projectionServiceMock.getByNameOrDefault(expectedRequestParamValue, expectedTarget))
                .willReturn(expectedProjection);

        // when
        var actualProjection = argumentResolver.resolveArgument(methodParameterMock, null, nativeWebRequestMock, null);

        // then
        assertThat(actualProjection).isEqualTo(expectedProjection);
    }

    private void assertDefaultProjectionReturned(String requestParamValue) {
        // given
        var expectedProjection = (Class) Iterable.class;
        var expectedTarget = (Class) String.class;

        given(nativeWebRequestMock.getParameter(REQUEST_PARAM_NAME)).willReturn(requestParamValue);
        given(methodParameterMock.getParameterAnnotation(ProjectionType.class)).willReturn(projectionTypeMock);
        given(projectionTypeMock.value()).willReturn(expectedTarget);

        given(projectionServiceMock.getDefault(expectedTarget)).willReturn(expectedProjection);

        // when
        var actualProjection = argumentResolver.resolveArgument(methodParameterMock, null, nativeWebRequestMock, null);

        // then
        assertThat(actualProjection).isEqualTo(expectedProjection);
    }

}
