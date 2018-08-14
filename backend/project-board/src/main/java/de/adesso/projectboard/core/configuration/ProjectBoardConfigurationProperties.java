package de.adesso.projectboard.core.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "projectboard")
@Getter
@Setter
public class ProjectBoardConfigurationProperties {

    /**
     * The delay between each database refresh in
     * minutes.
     */
    private long refreshInterval = 30L;

}
