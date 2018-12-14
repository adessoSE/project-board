package de.adesso.projectboard.base.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@Configuration
@EnableSpringDataWebSupport
public class RestConfiguration {
    @Bean
    public WebMvcConfigurer corsConfigurer(){
        return new WebMvcConfigurerAdapter(){
            @Override
            private void addCorsMappings(CorsRegistry registry){
                registry.addMapping("/").allowedOrigins("*");
            }
        }
    }
}

