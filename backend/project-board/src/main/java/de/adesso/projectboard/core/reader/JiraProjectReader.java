package de.adesso.projectboard.core.reader;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.adesso.projectboard.core.base.project.persistence.AbstractProject;
import de.adesso.projectboard.core.base.reader.ProjectReader;
import de.adesso.projectboard.core.project.JiraIssue;
import de.adesso.projectboard.core.project.persistence.JiraProject;
import de.adesso.projectboard.core.reader.jql.JqlComparator;
import de.adesso.projectboard.core.reader.jql.JqlQueryStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A {@link ProjectReader} implementation that reads {@link JiraProject}s from
 * a JIRA REST API.
 *
 * <p>
 *     Default implementation that gets created when no other {@link ProjectReader} bean
 *     is defined.
 * </p>
 *
 * @see de.adesso.projectboard.core.base.updater.ProjectDatabaseUpdater
 */
@Profile("adesso-jira")
@Service
public class JiraProjectReader implements ProjectReader {

    private final RestTemplate restTemplate;

    private final JiraProjectReaderConfigurationProperties properties;

    @Autowired
    public JiraProjectReader(RestTemplateBuilder builder, JiraProjectReaderConfigurationProperties properties) {
        this.restTemplate = builder
                .basicAuthorization(properties.getUsername(), properties.getPassword())
                .build();

        this.properties = properties;
    }

    /**
     *
     * @param dateTime
     *          The {@link LocalDateTime} supplied by the
     *          {@link de.adesso.projectboard.core.base.updater.ProjectDatabaseUpdater}.
     *
     * @return
     *          A List of {@link JiraProject}s that were created/modified since {@code dateTime}.
     *
     * @throws Exception
     *          When a error occurs.
     */
    @Override
    public List<? extends AbstractProject> getAllProjectsSince(LocalDateTime dateTime) throws Exception {
        return getProjectsByQuery(getJqlUpdateQueryString(dateTime));
    }

    /**
     *
     * @return
     *          A List of {@link JiraProject}s.
     *
     * @throws Exception
     *          When a error occurs.
     */
    @Override
    public List<? extends AbstractProject> getInitialProjects() throws Exception {
        return getProjectsByQuery(getJqlInitialQueryString());
    }

    /**
     *
     * @return
     *          The {@link Health} of this reader. Depends on the
     *          status code of the request.
     *
     * @see JiraServerInfo
     */
    @Override
    public Health health() {
        ResponseEntity<JiraServerInfo> responseEntity
                = restTemplate.getForEntity(properties.getJiraServerInfoUrl(), JiraServerInfo.class);

        if(responseEntity.getStatusCode().is2xxSuccessful()) {
            JiraServerInfo serverInfo = responseEntity.getBody();

            return Health.up()
                    .withDetail("serverTitle", serverInfo.getServerTitle())
                    .withDetail("serverVersion", serverInfo.getVersion())
                    .build();
        } else {
            return Health.down()
                    .withDetail("status", responseEntity.getStatusCode())
                    .build();
        }
    }

    /**
     *
     * @param jqlQuery
     *          The JQL query to execute.
     *
     * @return
     *          A list of {@link JiraProject}s.
     *
     * @throws IOException
     *          When error occurs when deserializing the response body.
     */
    private List<JiraProject> getProjectsByQuery(String jqlQuery) throws IOException {
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(properties.getJiraRequestUrl(), String.class, jqlQuery);

        // parse the json in the response body
        ObjectMapper mapper = new ObjectMapper();
        JsonParser parser = mapper.getFactory().createParser(responseEntity.getBody());
        JsonNode parsedNode = mapper.readTree(parser);
        JsonNode issueNode = parsedNode.get("issues");
        String issueNodeText = mapper.writeValueAsString(issueNode);

        // deserialize the json in the "issues" field to a list of JiraIssues
        List<JiraIssue> jiraIssueList = Arrays.asList(mapper.readValue(issueNodeText, JiraIssue[].class));

        return jiraIssueList.stream()
                .map(JiraIssue::getProjectWithIdAndKey)
                .collect(Collectors.toList());
    }

    /**
     *
     * @param dateTime
     *          The {@link LocalDateTime} supplied by {@link #getAllProjectsSince(LocalDateTime)}.
     * @return
     *          The JQL query to get all modified/created projects since {@code dateTime}.
     */
    private String getJqlUpdateQueryString(LocalDateTime dateTime) {
        JqlQueryStringBuilder andQueryBuilder = new JqlQueryStringBuilder();
        JqlQueryStringBuilder orQueryBuilder = new JqlQueryStringBuilder();

        orQueryBuilder
                .newQuery("updated", JqlComparator.GREATER_OR_EQUAL, dateTime)
                .or("created", JqlComparator.GREATER_OR_EQUAL, dateTime);

        return andQueryBuilder
                .newQuery("issuetype", JqlComparator.EQUAL, "Staffinganfrage")
                .and("project", JqlComparator.EQUAL, "STF")
                .and(orQueryBuilder.build())
                .build();
    }

    private String getJqlInitialQueryString() {
        JqlQueryStringBuilder orQueryBuilder = new JqlQueryStringBuilder();
        JqlQueryStringBuilder andQueryBuilder = new JqlQueryStringBuilder();

        orQueryBuilder
                .newQuery("status", JqlComparator.EQUAL, "eskaliert")
                .or("status", JqlComparator.EQUAL, "open");

        return andQueryBuilder
                .newQuery("issuetype", JqlComparator.EQUAL, "Staffinganfrage")
                .and("project", JqlComparator.EQUAL, "STF")
                .and(orQueryBuilder.build())
                .build();
    }

}
