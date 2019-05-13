package de.adesso.projectboard.adapter.jira.reader;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.adesso.projectboard.adapter.jira.configuration.JiraConfigurationProperties;
import de.adesso.projectboard.adapter.jira.reader.jql.JqlComparator;
import de.adesso.projectboard.adapter.jira.reader.jql.JqlQueryStringBuilder;
import de.adesso.projectboard.base.project.deserializer.JiraIssue;
import de.adesso.projectboard.base.project.persistence.Project;
import de.adesso.projectboard.base.reader.ProjectReader;
import de.adesso.projectboard.util.FieldTruncationUtils;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A {@link ProjectReader} implementation that reads {@link Project}s from
 * a Jira REST API.
 */
public class JiraProjectReader implements ProjectReader {

    private final RestTemplate restTemplate;

    private final JiraConfigurationProperties properties;

    public JiraProjectReader(RestTemplateBuilder builder, JiraConfigurationProperties properties) {
        this.restTemplate = builder
                .basicAuthentication(properties.getUsername(), properties.getPassword())
                .build();

        this.properties = properties;
    }

    /**
     *
     * @param dateTime
     *          The time from which to get new/updated projects.
     *
     * @return
     *          A List of {@link Project}s that were created/modified since {@code dateTime}.
     *
     * @throws Exception
     *          When a error occurs.
     */
    @Override
    public List<Project> getAllProjectsSince(LocalDateTime dateTime) throws Exception {
        return getProjectsByQuery(getUpdateJqlQueryString(dateTime));
    }

    /**
     *
     * @return
     *          A List of {@link Project}s.
     *
     * @throws Exception
     *          When a error occurs.
     */
    @Override
    public List<Project> getInitialProjects() throws Exception {
        return getProjectsByQuery(getInitialJqlQueryString());
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
                = restTemplate.getForEntity(properties.getServerInfoUrl(), JiraServerInfo.class);

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
     *          A list of {@link Project}s.
     *
     * @throws IOException
     *          When an error occurs when deserializing the response body.
     */
    private List<Project> getProjectsByQuery(String jqlQuery) throws IOException {
        var jiraRequestUrl = properties.getRequestUrl();
        var responseEntity = restTemplate.getForEntity(jiraRequestUrl, String.class, jqlQuery);
        var jiraIssues = parseJiraIssues(responseEntity.getBody());

        return jiraIssues.stream()
                .map(JiraIssue::getProjectWithId)
                .map(FieldTruncationUtils::truncateStringsToColumnLengths)
                .collect(Collectors.toList());
    }

    /**
     *
     * @param jiraResponse
     *          The API response to parse, not null.
     *
     * @return
     *          A list of all parsed {@link JiraIssue}s.
     *
     * @throws IOException
     *          When an error occurs while deserializing the Jira issues.
     */
    List<JiraIssue> parseJiraIssues(String jiraResponse) throws IOException {
        var mapper = new ObjectMapper();
        var parser = mapper.getFactory().createParser(jiraResponse);
        var parsedNode = mapper.readTree(parser);
        var issueNode = parsedNode.get("issues");
        var issueNodeText = mapper.writeValueAsString(issueNode);

        return Arrays.asList(mapper.readValue(issueNodeText, JiraIssue[].class));
    }

    /**
     *
     * @param dateTime
     *          The time to build the query from.
     *
     * @return
     *          The JQL query to get all modified/created projects since the
     *          given {@code dateTime}.
     */
    String getUpdateJqlQueryString(LocalDateTime dateTime) {
        JqlQueryStringBuilder andQueryBuilder = new JqlQueryStringBuilder();
        JqlQueryStringBuilder orQueryBuilder = new JqlQueryStringBuilder();

        orQueryBuilder
                .newQuery("updated", JqlComparator.GREATER_OR_EQUAL, dateTime)
                .or("created", JqlComparator.GREATER_OR_EQUAL, dateTime);

        return andQueryBuilder
                .newQuery("issuetype", JqlComparator.EQUAL, "Staffinganfrage")
                .and("project", JqlComparator.EQUAL, "Staffing")
                .and(orQueryBuilder.build())
                .build();
    }

    /**
     *
     * @return
     *          The JQL query used to get the initial projects when
     *          the first update is performed.
     */
    String getInitialJqlQueryString() {
        JqlQueryStringBuilder orQueryBuilder = new JqlQueryStringBuilder();
        JqlQueryStringBuilder andQueryBuilder = new JqlQueryStringBuilder();

        orQueryBuilder
                .newQuery("status", JqlComparator.EQUAL, "eskaliert")
                .or("status", JqlComparator.EQUAL, "open");

        return andQueryBuilder
                .newQuery("issuetype", JqlComparator.EQUAL, "Staffinganfrage")
                .and("project", JqlComparator.EQUAL, "Staffing")
                .and(orQueryBuilder.build())
                .build();
    }

}
