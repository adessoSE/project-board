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
     * The request URL for JIRA. It must contain a string placeholder
     * like <i>{jqlQuery}</i>. The string placeholder is replaced
     * with the actual JQL query.
     */
    private String jiraRequestUrl = "";

    /**
     * The request URL of the serverInfo API endpoint.
     */
    private String jiraServerInfoUrl = "";

    /**
     * The username to use for authorization.
     */
    private String username = "";

    /**
     * The password to use for authorization.
     */
    private String password = "";

}
