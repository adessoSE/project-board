package de.adesso.projectboard.reader;

import de.adesso.projectboard.reader.configuration.JiraConfigurationProperties;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class JiraProjectReaderTest {

    @Mock
    private JiraConfigurationProperties propertiesMock;

    @Mock
    private RestTemplateBuilder builderMock;

    @Mock
    private RestTemplate restTemplateMock;

    private JiraProjectReader jiraProjectReader;

    @Before
    public void setUp() {
        given(builderMock.basicAuthentication(anyString(), anyString())).willReturn(builderMock);
        given(builderMock.build()).willReturn(restTemplateMock);

        given(propertiesMock.getUsername()).willReturn("");
        given(propertiesMock.getPassword()).willReturn("");

        this.jiraProjectReader = new JiraProjectReader(builderMock, propertiesMock);
    }

    @Test
    public void getUpdateJqlQueryStringReturnsExpectedQuery() {
        // given
        var expectedDateString = "2018-01-01 13:37";
        var datePatternString = "yyyy-MM-dd HH:mm";
        var dateTime = LocalDateTime.parse(expectedDateString, DateTimeFormatter.ofPattern(datePatternString));

        var expectedQueryString = String.format(" issuetype = \"Staffinganfrage\" AND project = \"Staffing\" AND ( updated >= \"%s\" OR created >= \"%s\" )",
                expectedDateString, expectedDateString);

        // when
        var actualQueryString = jiraProjectReader.getUpdateJqlQueryString(dateTime);

        // then
        assertThat(expectedQueryString).isEqualTo(actualQueryString);
    }

    @Test
    public void getInitialJqlQueryReturnsExpectedQuery() {
        // given
        var expectedQueryString = " issuetype = \"Staffinganfrage\" AND project = \"Staffing\" AND ( status = \"eskaliert\" OR status = \"open\" )";

        // when
        var actualQueryString = jiraProjectReader.getInitialJqlQueryString();

        // then
        assertThat(actualQueryString).isEqualTo(expectedQueryString);
    }

}
