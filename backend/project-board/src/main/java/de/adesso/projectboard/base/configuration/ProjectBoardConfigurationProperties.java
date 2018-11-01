package de.adesso.projectboard.base.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;

@Configuration
@ConfigurationProperties(prefix = "projectboard")
@Getter
@Setter
@Validated
public class ProjectBoardConfigurationProperties {

    /**
     * The delay between each database refresh in
     * minutes.
     */
    @Min(1L)
    private long refreshInterval = 30L;


}
