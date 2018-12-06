package de.adesso.projectboard.rest.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * {@link WebMvcConfigurer} implementation that enables CORS on all
 * paths.
 */
@Configuration
@EnableWebMvc
public class CorsWebConfig implements WebMvcConfigurer {

    // enable CORS on all paths
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**");
    }

}

