package de.adesso.projectboard.core.base.configuration;

import de.adesso.projectboard.core.base.project.persistence.AbstractProject;
import de.adesso.projectboard.core.project.persistence.JiraProject;
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

    /**
     * The maximum number of days to update.
     */
    private long maxUpdateDays = 30L;

    private Class<? extends AbstractProject> projectClass = JiraProject.class;

}
