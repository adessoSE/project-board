package de.adesso.projectboard.core.reader;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.adesso.projectboard.core.base.project.persistence.AbstractProject;
import de.adesso.projectboard.core.base.reader.AbstractProjectReader;
import de.adesso.projectboard.core.project.persistence.JiraProject;
import de.adesso.projectboard.core.reader.jql.JqlComparator;
import de.adesso.projectboard.core.reader.jql.JqlQueryStringBuilder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A {@link AbstractProjectReader} implementation that reads {@link JiraProject}s from
 * a JIRA REST API.
 *
 * <p>
 *     Default implementation that gets created when no other {@link AbstractProjectReader} bean
 *     is defined.
 * </p>
 *
 * @see de.adesso.projectboard.core.base.updater.ProjectDatabaseUpdater
 */
public class JiraProjectReader implements AbstractProjectReader {

    private final RestTemplate restTemplate;

    private final JiraProjectReaderConfigurationProperties properties;

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
     *          When a exception occurs.
     */
    @Override
    public List<? extends AbstractProject> getAllProjectsSince(LocalDateTime dateTime) throws Exception {

        ResponseEntity<String> responseEntity = restTemplate.getForEntity(properties.getJiraRequestUrl() + getJqlQueryString(dateTime), String.class);

        if(!responseEntity.getStatusCode().is2xxSuccessful()) {
            // TODO: throw appropriate exception
            throw new IllegalStateException(String.format("Status code was %d", responseEntity.getStatusCode().value()));
        }

        ObjectMapper mapper = new ObjectMapper();
        JsonParser parser = mapper.getFactory().createParser(responseEntity.getBody());
        JsonNode parsedNode = mapper.readTree(parser);
        JsonNode issueNode = parsedNode.get("issues");
        String issueNodeText = mapper.writeValueAsString(issueNode);

        // TODO: only 50 projects retrieved because of JIRA paging limit
        List<JiraIssue> jiraIssueList = Arrays.asList(mapper.readValue(issueNodeText, JiraIssue[].class));

        return jiraIssueList.stream()
                .map(JiraIssue::getProjectWithId)
                .collect(Collectors.toList());
    }

    /**
     *
     * @param dateTime
     *          The {@link LocalDateTime} supplied by {@link #getAllProjectsSince(LocalDateTime)}.
     * @return
     *          The JQL query to get all modified/created projects since {@code dateTime}.
     */
    private String getJqlQueryString(LocalDateTime dateTime) {
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


    @JsonIgnoreProperties(ignoreUnknown = true)
    @Getter
    @Setter
    public static class JiraIssue {

        @JsonAlias("id")
        private String id;

        @JsonAlias("fields")
        private JiraProject project;

        public JiraProject getProjectWithId() {
            project.setId(Long.parseLong(id));
            return project;
        }

    }

    /*String text = new String(Files.readAllBytes(Paths.get("C:/Users/dmeier/Desktop/JIRA-JSON-Response.txt")), StandardCharsets.UTF_8);

        ObjectMapper mapper = new ObjectMapper();
        JsonParser parser = mapper.getFactory().createParser(text);
        JsonNode parsedNode = mapper.readTree(parser);

        JsonNode issueNode = parsedNode.get("issues");*/
}
