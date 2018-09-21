package de.adesso.projectboard.core.base.configuration;

import de.adesso.projectboard.core.base.rest.project.persistence.AbstractProject;
import de.adesso.projectboard.core.project.persistence.Project;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

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

    /**
     * The project class.
     */
    @NotNull
    private Class<? extends AbstractProject> projectClass = Project.class;

}
