package de.adesso.projectboard.core.reader;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.adesso.projectboard.core.base.project.persistence.AbstractProject;
import de.adesso.projectboard.core.base.reader.AbstractProjectReader;
import de.adesso.projectboard.core.project.persistence.JiraProject;
import de.adesso.projectboard.core.reader.jql.JqlComparator;
import de.adesso.projectboard.core.reader.jql.JqlQueryStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

public class JiraProjectReader implements AbstractProjectReader {

    private final RestTemplate restTemplate;

    private final JiraProjectReaderConfigurationProperties properties;

    public JiraProjectReader(RestTemplateBuilder builder, JiraProjectReaderConfigurationProperties properties) {
        this.restTemplate = builder
                .basicAuthorization(properties.getUsername(), properties.getPassword())
                .build();

        this.properties = properties;
    }

    @Override
    public List<? extends AbstractProject> getAllProjectsSince(LocalDateTime dateTime) throws Exception {
        Logger logger = LoggerFactory.getLogger(getClass());

        String data = restTemplate.getForObject(properties.getJiraRequestURL() + getJqlQueryString(dateTime), String.class);

        return Collections.emptyList();
    }

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

}
