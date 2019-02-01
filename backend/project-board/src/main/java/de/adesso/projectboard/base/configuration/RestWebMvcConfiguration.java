package de.adesso.projectboard.base.configuration;

import de.adesso.projectboard.base.projection.ProjectionTypeArgumentResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class RestWebMvcConfiguration implements WebMvcConfigurer {

    private final ProjectionTypeArgumentResolver resolver;

    @Autowired
    public RestWebMvcConfiguration(ProjectionTypeArgumentResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // allow CORS on all paths
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "DELETE");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(resolver);
    }

}
