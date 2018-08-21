package de.adesso.projectboard;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.adesso.projectboard.core.project.persistence.JiraProject;
import de.adesso.projectboard.core.reader.JiraProjectReader;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class JiraIssueJsonSerializationTests {

    @Test
    public void testDeserializationFromJson() throws IOException {
        List<JiraProjectReader.JiraIssue> issueList = getIssueListFromFile();

        assertEquals(2, issueList.size());

        List<JiraProject> projectList = issueList.stream()
                .map(JiraProjectReader.JiraIssue::getProjectWithIdAndKey)
                .collect(Collectors.toList());

        JiraProject firstProject = projectList.get(0);

        assertEquals(1L, firstProject.getId());
        assertEquals("Teststatus 1", firstProject.getStatus());
        assertEquals("Testissuetype 1", firstProject.getIssuetype());
        assertEquals("Testkey 1", firstProject.getKey());
        assertEquals("Testsummary 1", firstProject.getTitle());
        assertEquals("Testexcercise 1", firstProject.getExercise());
        assertEquals("Testskills 1", firstProject.getSkills());
        assertEquals("Testdescription 1", firstProject.getDescription());
        assertEquals("Testlob 1", firstProject.getLob());
        assertEquals("Testcustomer 1", firstProject.getCustomer());
        assertEquals("Testlocation 1", firstProject.getLocation());
        assertEquals("01.01.2018", firstProject.getOperationStart());
        assertEquals("01.02.2018", firstProject.getOperationEnd());
        assertEquals("Testwork 1", firstProject.getWork());
        assertEquals(LocalDateTime.of(2018, 1, 1, 13, 37, 0), firstProject.getCreated());
        assertEquals(LocalDateTime.of(2018, 1, 2, 13, 37, 0), firstProject.getUpdated());
        assertEquals("Testfreelancer 1", firstProject.getFreelancer());


        JiraProject second = projectList.get(1);

        assertEquals(2L, second.getId());
        assertEquals("Teststatus 2", second.getStatus());
        assertEquals("Testissuetype 2", second.getIssuetype());
        assertEquals("Testkey 2", second.getKey());
        assertEquals("Testsummary 2", second.getTitle());
        assertEquals("Testexcercise 2", second.getExercise());
        assertEquals("Testskills 2", second.getSkills());
        assertEquals("Testdescription 2", second.getDescription());
        assertEquals("Testlob 2", second.getLob());
        assertEquals("Testcustomer 2", second.getCustomer());
        assertEquals("Testlocation 2", second.getLocation());
        assertEquals("02.01.2018", second.getOperationStart());
        assertEquals("02.02.2018", second.getOperationEnd());
        assertEquals("Testwork 2", second.getWork());
        assertEquals(LocalDateTime.of(2018, 1, 1, 13, 37, 0), second.getCreated());
        assertEquals(LocalDateTime.of(2018, 1, 2, 13, 37, 0), second.getUpdated());
        assertEquals("Testfreelancer 2", second.getFreelancer());
    }

    private List<JiraProjectReader.JiraIssue> getIssueListFromFile() throws IOException {
        URL url = this.getClass().getResource("/JiraJsonResponse.txt");
        File testJsonFile = new File(url.getFile());

        String text = new String(Files.readAllBytes(testJsonFile.toPath()), StandardCharsets.UTF_8);

        ObjectMapper mapper = new ObjectMapper();
        JsonParser parser = mapper.getFactory().createParser(text);
        JsonNode parsedNode = mapper.readTree(parser);

        JsonNode issueNode = parsedNode.get("issues");
        String issueNodeText = mapper.writeValueAsString(issueNode);

        return Arrays.asList(mapper.readValue(issueNodeText, JiraProjectReader.JiraIssue[].class));
    }

}
