package de.adesso.projectboard.base.projection;

import de.adesso.projectboard.base.configuration.ProjectBoardConfigurationProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Objects;

/**
 * {@link HandlerMethodArgumentResolver} implementation to resolve method parameters of type
 * {@code Class<?>} annotated with the {@link ProjectionType} annotation.
 */
@Component
public class ProjectionTypeArgumentResolver implements HandlerMethodArgumentResolver {

    /**
     * Projection service to retrieve projections.
     */
    private final ProjectionService projectionService;

    /**
     * The parameter name the desired projection name
     * is retrieved from.
     */
    private final String projectionParamName;

    @Autowired
    public ProjectionTypeArgumentResolver(ProjectionService projectionService, ProjectBoardConfigurationProperties properties) {
        this.projectionService = projectionService;

        this.projectionParamName = properties.getProjectionNameRequestParameter();
    }

    /**
     *
     * @param parameter
     *          The parameter to check, not null.
     *
     * @return
     *          {@code true}, iff the parameter is annotated with @{@link ProjectionType}.
     */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return !Objects.isNull(parameter.getParameterAnnotation(ProjectionType.class));
    }

    /**
     *
     * @param parameter
     *          The parameter to resolve, not null.
     *
     * @param mavContainer
     *          The {@code ModelAndViewContainer}, may be null.
     *
     * @param webRequest
     *          The {@code WebRequest}, not null.
     *
     * @param binderFactory
     *          The {@code WebDataBinderFactory}, may be null.
     *
     * @return
     *          A projection matching the name supplied via the
     *          request parameter and the given target class.
     *
     * @see ProjectionService#getDefault(Class)
     * @see ProjectionService#getByNameOrDefault(String, Class)
     */
    @Override
    public Class<?> resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        var desiredProjectionName = webRequest.getParameter(projectionParamName);
        var projectionTarget = parameter.getParameterAnnotation(ProjectionType.class).value();

        if(Objects.isNull(desiredProjectionName) || desiredProjectionName.isEmpty()) {
            return projectionService.getDefault(projectionTarget);
        }

        return projectionService.getByNameOrDefault(desiredProjectionName, projectionTarget);
    }

}
