package de.adesso.projectboard.base.configuration;

import de.adesso.projectboard.base.projection.ProjectionTypeArgumentResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@Configuration
@EnableSpringDataWebSupport
public class ProjectBoardRestConfiguration {

    @Profile("prod")
    @Bean
    @Autowired
    public ProjectBoardWebMvcConfigurer projectBoardWebMvcConfigurer(ProjectionTypeArgumentResolver resolver,
                                                                     ProjectBoardConfigurationProperties pbConfigProperties) {
        return new ProjectBoardWebMvcConfigurer(resolver, pbConfigProperties);
    }

    @Profile("!prod")
    @Bean
    @Autowired
    public AnyOriginWebMvcConfigurer anyOriginWebMvcConfigurer(ProjectionTypeArgumentResolver resolver) {
        return new AnyOriginWebMvcConfigurer(resolver);
    }

}

