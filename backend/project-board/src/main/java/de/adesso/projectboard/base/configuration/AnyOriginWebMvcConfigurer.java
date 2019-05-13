package de.adesso.projectboard.base.configuration;

import de.adesso.projectboard.base.projection.ProjectionTypeArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

/**
 * {@link BaseWebMvcConfigurer} allowing all request origins on all paths.
 */
class AnyOriginWebMvcConfigurer extends BaseWebMvcConfigurer {

    protected AnyOriginWebMvcConfigurer(ProjectionTypeArgumentResolver resolver) {
        super(resolver);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // allow CORS on all paths
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "DELETE", "OPTIONS", "PUT");
    }

}
