package de.adesso.projectboard.base.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

@ConfigurationProperties(prefix = "projectboard")
@Data
@Validated
public class ProjectBoardConfigurationProperties {

    /**
     * The delay between every project refresh.
     */
    @Min(1L)
    private long refreshInterval = 30L;

    /**
     * The name of the request parameter to get the
     * desired projection's name of.
     */
    @NotEmpty
    private String projectionNameRequestParameter = "projection";

    /**
     * The URL of the project board. Used to set the allowed origins
     * when the prod profile is activated.
     */
    @NotEmpty
    private String url = "http://localhost:4200/";

}
