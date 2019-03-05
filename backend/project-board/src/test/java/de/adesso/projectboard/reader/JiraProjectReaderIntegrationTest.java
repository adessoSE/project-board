package de.adesso.projectboard.reader;

import de.adesso.projectboard.base.project.persistence.Project;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@ActiveProfiles("adesso-jira")
@RunWith(SpringRunner.class)
public class JiraProjectReaderIntegrationTest {

    private final String REQUEST_PATH = "/test";

    @MockBean
    private JiraConfigurationProperties properties;

    @MockBean
    private RestTemplateBuilder builder;

    private MockRestServiceServer server;

    private JiraProjectReader reader;

    @Before
    public void setUp() {
        // set up properties mock
        given(properties.getRequestUrl()).willReturn(REQUEST_PATH);
        given(properties.getUsername()).willReturn("");
        given(properties.getPassword()).willReturn("");

        // spring's RestClientTest annotation causes problems
        // because the username/password for basic authentication can't
        // be null since spring 5.1, but the properties mock bean injected returns
        // null when retrieving the username/password
        // -> configure manually

        // initialize a new rest template that should be used by the
        // reader
        var restTemplate = new RestTemplate();

        // configure the builder to return the template
        given(builder.basicAuthentication(anyString(), anyString())).willReturn(builder);
        given(builder.build()).willReturn(restTemplate);

        // bind the server to the given rest template
        this.server = MockRestServiceServer.bindTo(restTemplate)
                .build();

        this.reader = new JiraProjectReader(builder, properties);
    }

    @Test
    public void getProjectsReturnsProjectsWhenRequestIsSuccessful() throws Exception {
        // given
        server.expect(requestTo(REQUEST_PATH))
                .andRespond(withSuccess(getJiraJsonResponse(), MediaType.APPLICATION_JSON));

        LocalDateTime expectedCreated = LocalDateTime.of(2018, 1, 1, 13, 37);
        LocalDateTime expectedUpdated = LocalDateTime.of(2018, 1, 2, 13, 37);

        Project expectedFirstProject = new Project("Testkey 1", "Teststatus 1", "Testissuetype 1", "Testsummary 1", Arrays.asList("Testlabel 1", "Testlabel 2"),
                "Testjob 1", "Testskills 1", "Testdescription 1", "Testlob 1", "Testcustomer 1", "Testlocation 1", "01.01.2018", "01.02.2018", "Testeffort 1", expectedCreated, expectedUpdated,
                "Testfreelancer 1", "Testelongation 1", "Testother 1",  "Testrate 1", "Testcompensated 1", Project.Origin.JIRA);

        Project expectedSecondProject = new Project("Testkey 2", "Teststatus 2", "Testissuetype 2", "Testsummary 2", Collections.emptyList(),
                "Testjob 2", "Testskills 2", "Testdescription 2", "Testlob 2", "Testcustomer 2", "Testlocation 2", "02.01.2018", "02.02.2018", "Testeffort 2", expectedCreated, expectedUpdated,
                "Testfreelancer 2", "Testelongation 2", "Testother 2",  "Testrate 2", "Testcompensated 2", Project.Origin.JIRA);

        // when
        List<Project> projectList = reader.getInitialProjects();

        // then
        assertThat(projectList).containsExactly(expectedFirstProject, expectedSecondProject);
    }

    @Test
    public void getProjectsThrowsExceptionWhenRequestNotSuccessful() {
        // given
        server.expect(requestTo("/test"))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

        // when
        assertThatThrownBy(() -> reader.getInitialProjects())
                .isInstanceOf(RestClientException.class);
    }

    private String getJiraJsonResponse() throws IOException, URISyntaxException {
        URL url = this.getClass().getResource("JiraJsonResponse.txt");
        File testJsonFile = new File(url.toURI());

        return new String(Files.readAllBytes(testJsonFile.toPath().toAbsolutePath()), StandardCharsets.UTF_8);
    }

}
