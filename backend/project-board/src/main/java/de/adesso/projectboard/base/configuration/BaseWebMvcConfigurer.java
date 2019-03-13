package de.adesso.projectboard.base.configuration;

import de.adesso.projectboard.base.projection.ProjectionTypeArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * Abstract class implementing the {@link WebMvcConfigurer} interface adding a
 * {@link ProjectionTypeArgumentResolver} to the resolvers via the
 * {@link WebMvcConfigurer#addArgumentResolvers(List)} method.
 */
abstract class BaseWebMvcConfigurer implements WebMvcConfigurer {

    private final ProjectionTypeArgumentResolver resolver;

    protected BaseWebMvcConfigurer(ProjectionTypeArgumentResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(resolver);
    }

}
