package de.adesso.projectboard.core.reader;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "jira")
@Configuration
@Getter
@Setter
public class JiraProjectReaderConfigurationProperties {

    /**
     *
     */
    private String jiraRequestURL = "";

    /**
     * The username to use in the requests.
     */
    private String username = "";

    /**
     * The password to use in the requests.
     */
    private String password = "";

}
