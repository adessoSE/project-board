package de.adesso.projectboard.base.projection.configuration;

import de.adesso.projectboard.base.projection.NamedProjection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.projection.SpelAwareProxyProjectionFactory;
import org.springframework.data.util.AnnotatedTypeScanner;

@Configuration
public class ProjectionConfiguration {

    @Bean
    public ProjectionFactory projectionFactory() {
        return new SpelAwareProxyProjectionFactory();
    }

    @Bean
    public AnnotatedTypeScanner typeScanner() {
        return new AnnotatedTypeScanner(true, NamedProjection.class);
    }

}
