package de.adesso.projectboard.core.reader;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.adesso.projectboard.core.base.reader.ProjectReader;
import de.adesso.projectboard.core.base.rest.project.deserializer.JiraIssue;
import de.adesso.projectboard.core.base.rest.project.persistence.Project;
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
 * A {@link ProjectReader} implementation that reads {@link Project}s from
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
     *          A List of {@link Project}s that were created/modified since {@code dateTime}.
     *
     * @throws Exception
     *          When a error occurs.
     */
    @Override
    public List<? extends Project> getAllProjectsSince(LocalDateTime dateTime) throws Exception {
        return getProjectsByQuery(getJqlUpdateQueryString(dateTime));
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
    public List<? extends Project> getInitialProjects() throws Exception {
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
     *          When error occurs when deserializing the response body.
     */
    private List<Project> getProjectsByQuery(String jqlQuery) throws IOException {
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(properties.getRequestUrl(), String.class, jqlQuery);

        // parse the json in the response body
        ObjectMapper mapper = new ObjectMapper();
        JsonParser parser = mapper.getFactory().createParser(responseEntity.getBody());
        JsonNode parsedNode = mapper.readTree(parser);
        JsonNode issueNode = parsedNode.get("issues");
        String issueNodeText = mapper.writeValueAsString(issueNode);

        // deserialize the json in the "issues" field to a list of JiraIssues
        List<JiraIssue> jiraIssueList = Arrays.asList(mapper.readValue(issueNodeText, JiraIssue[].class));

        return jiraIssueList.stream()
                .map(JiraIssue::getProjectWithId)
                .map(this::cutStrings)
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

    /**
     *
     * @return
     *          The JQL query used to get the initial projects when
     *          the first update is performed.
     */
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

    /**
     * Method used to cut string values of a project to a persistable length.
     *
     * @param project
     *          The {@link Project}.
     *
     * @return
     *          The {@link Project} with a persistable string length.
     *
     */
    private Project cutStrings(Project project) {
        // 256 character limit
        project
                .setStatus(cutAndAppendDotsIfRequired(project.getStatus(), 256))
                .setTitle(cutAndAppendDotsIfRequired(project.getTitle(), 256))
                .setLob(cutAndAppendDotsIfRequired(project.getLob(), 256))
                .setCustomer(cutAndAppendDotsIfRequired(project.getCustomer(), 256))
                .setLocation(cutAndAppendDotsIfRequired(project.getLocation(), 256))
                .setOperationStart(cutAndAppendDotsIfRequired(project.getOperationStart(), 256))
                .setOperationEnd(cutAndAppendDotsIfRequired(project.getOperationEnd(), 256))
                .setEffort(cutAndAppendDotsIfRequired(project.getEffort(), 256))
                .setFreelancer(cutAndAppendDotsIfRequired(project.getFreelancer(), 256))
                .setElongation(cutAndAppendDotsIfRequired(project.getElongation(), 256));

        List<String> cutLabels = project.getLabels().stream()
                .map(label -> cutAndAppendDotsIfRequired(label, 256))
                .collect(Collectors.toList());
        project.setLabels(cutLabels);

        // 8192 character limit
        project
                .setJob(cutAndAppendDotsIfRequired(project.getJob(), 8192))
                .setSkills(cutAndAppendDotsIfRequired(project.getSkills(), 8192))
                .setDescription(cutAndAppendDotsIfRequired(project.getDescription(), 8192))
                .setOther(cutAndAppendDotsIfRequired(project.getOther(), 8192));

        return project;
    }

    /**
     *
     * @param string
     *          The string to cut.
     *
     * @param maxLength
     *          The max maxLength of the string.
     *
     * @return
     *          The string as it was passed in case it was shorter than
     *          {@code maxLength} characters or {@code null}, a string with the given max
     *          length with <i>"..."</i> appended to it to indicate that it
     *          was cut or a empty string when {@code maxLength < 3}.
     */
    private String cutAndAppendDotsIfRequired(String string, int maxLength) {
        if(string == null) {
            return null;
        } else if(maxLength < 3) {
            return "";
        }

        if(string.length() > maxLength) {
            return string.subSequence(0, Math.max(0, maxLength - 4)) + "...";
        } else {
            return string;
        }
    }
}
