package de.adesso.projectboard.reader;

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
    private JiraProjectReaderConfigurationProperties propertiesMock;

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

    @Test
    public void cutAndAppendDotsIfRequiredReturnsNullWhenNullWasPassed() {
        // given

        // when
        var actualString = jiraProjectReader.cutAndAppendDotsIfRequired(null, 10);

        // then
        assertThat(actualString).isNull();
    }

    @Test
    public void cutAndAppendDotsIfRequiredReturnsEmptyStringWhenMaxLengthIsSmallerThan3() {
        // given
        int maxLength = 2;
        var testString = "Test12345";

        // when
        var actualString = jiraProjectReader.cutAndAppendDotsIfRequired(testString, maxLength);

        // then
        assertThat(actualString).isEmpty();
    }

    @Test
    public void cutAndAppendDotsIfRequiredCutsAndAppendsDotsIfStringIstooLong() {
        // given
        int maxLength = 6;
        var testString = "123456789";
        var expectedString = "123...";

        // when
        var actualString = jiraProjectReader.cutAndAppendDotsIfRequired(testString, maxLength);

        // then
        assertThat(actualString).isEqualTo(expectedString);
    }

    @Test
    public void cutAndAppendDotsIfRequiredReturnsPassedStringWhenStringIsShorterThatMaxLength() {
        // given
        int maxLength = 10;
        var expectedString = "Test";

        // when
        var actualString = jiraProjectReader.cutAndAppendDotsIfRequired(expectedString, maxLength);

        // then
        assertThat(actualString).isEqualTo(expectedString);
    }

}
