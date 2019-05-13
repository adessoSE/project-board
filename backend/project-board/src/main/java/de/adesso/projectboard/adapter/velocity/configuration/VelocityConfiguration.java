package de.adesso.projectboard.adapter.velocity.configuration;

import de.adesso.projectboard.adapter.velocity.VelocityTemplateService;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.StandardCharsets;
import java.util.Properties;

@Configuration
public class VelocityConfiguration {

    @Bean
    public VelocityEngine velocityEngine() {
        // set the classpath resource loader as the default velocity resource loader
        var properties = new Properties();
        properties.put(RuntimeConstants.INPUT_ENCODING, StandardCharsets.UTF_8.name());
        properties.put(RuntimeConstants.ENCODING_DEFAULT, StandardCharsets.UTF_8.name());
        properties.put(RuntimeConstants.RESOURCE_LOADER, "classpath");
        properties.put("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        properties.put("classpath.resource.loader.description", "Velocity Classpath Resource Loader");

        var velocityEngine = new VelocityEngine();
        velocityEngine.init(properties);
        return velocityEngine;
    }

    @Autowired
    @Bean
    public VelocityTemplateService velocityTemplateService(VelocityEngine velocityEngine) {
        return new VelocityTemplateService(velocityEngine);
    }

}
