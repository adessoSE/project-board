package de.adesso.projectboard.base.configuration;


import de.adesso.projectboard.base.projection.ProjectionTypeArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

/**
 * {@link BaseWebMvcConfigurer} only allowing the {@link ProjectBoardConfigurationProperties#getUrl()} URL
 * configured in the {@link ProjectBoardConfigurationProperties} as the allowed request origin on all paths.
 */
class ProjectBoardWebMvcConfigurer extends BaseWebMvcConfigurer {

    private final ProjectBoardConfigurationProperties properties;

    protected ProjectBoardWebMvcConfigurer(ProjectionTypeArgumentResolver resolver,
                                           ProjectBoardConfigurationProperties properties) {
        super(resolver);
        this.properties = properties;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // allow CORS on all paths
        registry.addMapping("/**")
                .allowedOrigins(properties.getUrl())
                .allowedMethods("GET", "POST", "DELETE", "OPTIONS", "PUT");
    }

}
