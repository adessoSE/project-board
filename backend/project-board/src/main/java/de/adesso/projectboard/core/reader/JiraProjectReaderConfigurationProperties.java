package de.adesso.projectboard.core.reader;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * The configuration properties for the {@link JiraProjectReader}.
 *
 * @see JiraProjectReader
 */
@ConfigurationProperties(prefix = "jira")
@Configuration
@Getter
@Setter
public class JiraProjectReaderConfigurationProperties {

    /**
     * The request URL for jira. The JQL query gets appended
     * to this URL.
     */
    private String jiraRequestUrl = "";

    /**
     * The username to use for authorization.
     */
    private String username = "";

    /**
     * The password to use for authorization.
     */
    private String password = "";

}
