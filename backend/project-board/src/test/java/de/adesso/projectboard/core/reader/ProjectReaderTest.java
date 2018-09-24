package de.adesso.projectboard.core.reader;

import de.adesso.projectboard.core.base.rest.project.persistence.Project;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClientException;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RunWith(SpringRunner.class)
@RestClientTest(JiraProjectReader.class)
@ActiveProfiles("adesso-jira")
public class ProjectReaderTest {

    @MockBean
    private JiraProjectReaderConfigurationProperties properties;

    @Autowired
    private JiraProjectReader reader;

    @Autowired
    private MockRestServiceServer server;

    @Before
    public void setUp() {
        given(properties.getRequestUrl()).willReturn("/test");
    }

    @Test
    public void testGetProjects_Success() throws Exception {
        server.expect(requestTo("/test"))
                .andRespond(withSuccess(getJiraJsonResponse(), MediaType.APPLICATION_JSON));

        List<? extends Project> projectList = reader.getInitialProjects();

        assertEquals(2, projectList.size());

        Project firstProject = (Project) projectList.get(0);

        assertEquals(1L, firstProject.getId());
        assertEquals("Teststatus 1", firstProject.getStatus());
        assertEquals("Testissuetype 1", firstProject.getIssuetype());
        assertEquals("Testkey 1", firstProject.getKey());
        assertEquals("Testsummary 1", firstProject.getTitle());
        assertEquals(2, firstProject.getLabels().size());
        assertEquals("Testlabel 1", firstProject.getLabels().get(0));
        assertEquals("Testlabel 2", firstProject.getLabels().get(1));
        assertEquals("Testjob 1", firstProject.getJob());
        assertEquals("Testskills 1", firstProject.getSkills());
        assertEquals("Testdescription 1", firstProject.getDescription());
        assertEquals("Testlob 1", firstProject.getLob());
        assertEquals("Testcustomer 1", firstProject.getCustomer());
        assertEquals("Testlocation 1", firstProject.getLocation());
        assertEquals("01.01.2018", firstProject.getOperationStart());
        assertEquals("01.02.2018", firstProject.getOperationEnd());
        assertEquals("Testeffort 1", firstProject.getEffort());
        assertEquals(LocalDateTime.of(2018, 1, 1, 13, 37, 0), firstProject.getCreated());
        assertEquals(LocalDateTime.of(2018, 1, 2, 13, 37, 0), firstProject.getUpdated());
        assertEquals("Testfreelancer 1", firstProject.getFreelancer());
        assertEquals("Testelongation 1", firstProject.getElongation());
        assertEquals("Testother 1", firstProject.getOther());


        Project secondProject = (Project) projectList.get(1);

        assertEquals(2L, secondProject.getId());
        assertEquals("Teststatus 2", secondProject.getStatus());
        assertEquals("Testissuetype 2", secondProject.getIssuetype());
        assertEquals("Testkey 2", secondProject.getKey());
        assertEquals("Testsummary 2", secondProject.getTitle());
        assertEquals(0, secondProject.getLabels().size());
        assertEquals("Testjob 2", secondProject.getJob());
        assertEquals("Testskills 2", secondProject.getSkills());
        assertEquals("Testdescription 2", secondProject.getDescription());
        assertEquals("Testlob 2", secondProject.getLob());
        assertEquals("Testcustomer 2", secondProject.getCustomer());
        assertEquals("Testlocation 2", secondProject.getLocation());
        assertEquals("02.01.2018", secondProject.getOperationStart());
        assertEquals("02.02.2018", secondProject.getOperationEnd());
        assertEquals("Testeffort 2", secondProject.getEffort());
        assertEquals(LocalDateTime.of(2018, 1, 1, 13, 37, 0), secondProject.getCreated());
        assertEquals(LocalDateTime.of(2018, 1, 2, 13, 37, 0), secondProject.getUpdated());
        assertEquals("Testfreelancer 2", secondProject.getFreelancer());
        assertEquals("Testelongation 2", secondProject.getElongation());
        assertEquals("Testother 2", secondProject.getOther());
    }

    @Test(expected = RestClientException.class)
    public void testGetProjects_5xxStatus() throws Exception {
        server.expect(requestTo("/test"))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

        reader.getInitialProjects();
    }

    private String getJiraJsonResponse() throws IOException {
        URL url = this.getClass().getResource("/de/adesso/projectboard/core/JiraJsonResponse.txt");
        File testJsonFile = new File(url.getFile());

        return new String(Files.readAllBytes(testJsonFile.toPath()), StandardCharsets.UTF_8);
    }

}