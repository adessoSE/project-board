package de.adesso.projectboard.adapter.velocity;

import helper.adapter.mail.StringWriterArgumentMatcher;
import helper.adapter.mail.VelocityContextArgumentMatcher;
import org.apache.velocity.Template;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.util.Pair;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.StringWriter;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doAnswer;

@RunWith(MockitoJUnitRunner.class)
public class VelocityTemplateServiceTest {

    @Mock
    private VelocityEngine velocityEngineMock;

    @Mock
    private Template velocityTemplateMock;

    private VelocityTemplateService velocityMailTemplateService;

    @Before
    public void setUp() {
        this.velocityMailTemplateService = new VelocityTemplateService(velocityEngineMock);
    }

    @Test
    public void mergeContextMergesExpectedTemplateAndContext() {
        // given
        var templatePath = "TestTemplate.vm";
        var contextMap = Map.<String, Object>of("key1", "value1", "key2", 2L);
        var expectedResult = "Nice context merge!";

        given(velocityEngineMock.getTemplate(templatePath)).willReturn(velocityTemplateMock);
        doAnswer(invocation -> {
            var actualWriter = (StringWriter) invocation.getArgument(1);
            actualWriter.append(expectedResult);

            return null;
        }).when(velocityTemplateMock).merge(
                argThat(new VelocityContextArgumentMatcher(contextMap)),
                argThat(new StringWriterArgumentMatcher(""))
        );

        // when
        var actualResult = velocityMailTemplateService.mergeTemplate(templatePath, contextMap);

        // then
        assertThat(actualResult).isEqualTo(expectedResult);
    }

    @Test
    public void getSubjectAndTextReturnsExpectedSubjectAndText() {
        // given
        var expectedSubject = "Cool subject!";
        var expectedText = "Even cooler text!";
        var expectedResult = new Pair<>(expectedSubject, expectedText);
        var templatePath = "TestTemplate.vm";
        var templateContent = String.format("%s\r\n%s\r\n%s\r\n%s",
                VelocityTemplateService.SUBJECT_DELIMITER, expectedSubject,
                VelocityTemplateService.TEXT_DELIMITER, expectedText);
        var contextMap = Map.<String, Object>of("key", "value");

        given(velocityEngineMock.getTemplate(templatePath)).willReturn(velocityTemplateMock);
        doAnswer(invocation -> {
            var actualWriter = (StringWriter) invocation.getArgument(1);
            actualWriter.append(templateContent);

            return null;
        }).when(velocityTemplateMock).merge(
                argThat(new VelocityContextArgumentMatcher(contextMap)),
                argThat(new StringWriterArgumentMatcher(""))
        );

        // when
        var actualResult = velocityMailTemplateService.getSubjectAndText(templatePath, contextMap);

        // then
        assertThat(actualResult).isEqualTo(expectedResult);
    }

    @Test
    public void getSubstringBetweenDelimitersReturnsNullWhenFirstDelimiterNotFound() {
        // given
        var originString = "String that does not contain the delimiter :O";
        var firstDelimiter = "--DELIMITER--";

        // when / then
        compareExpectedWithActualResultOfGetSubStringBetweenDelimiters(originString, firstDelimiter, null, null);
    }

    @Test
    public void getSubStringBetweenDelimitersReturnsTextWithoutLeadingAndTrailingWhitespaceAfterLastOccurrenceOfFirstWhenSecondNull() {
        // given
        var expectedResult = "ExpectedResult";
        var firstDelimiter = "--DELIMITER--";
        var originString = "--DELIMITER--First --DELIMITER--\r\nSecond\t--DELIMITER--    \r\n ExpectedResult   \n";

        // when / then
        compareExpectedWithActualResultOfGetSubStringBetweenDelimiters(originString, firstDelimiter, null, expectedResult);
    }

    @Test
    public void getSubStringBetweenDelimitersReturnsTextBetweenFirstAndSecondDelimiterWithoutLeadingAndTrailingWhitespaceWhenBothFound() {
        // given
        var expectedResult = "That's right!";
        var firstDelimiter = "--F_DELIMITER--";
        var secondDelimiter = "--S_DELIMITER--";
        var originString = "Something else --F_DELIMITER-- \r\n That's right! \r\n--S_DELIMITER-- Something else --S_DELIMITER--";

        // when / then
        compareExpectedWithActualResultOfGetSubStringBetweenDelimiters(originString, firstDelimiter, secondDelimiter, expectedResult);
    }

    @Test
    public void getSubStringBetweenDelimitersReturnsNullWhenSecondDelimiterNotFound() {
        // given
        var firstDelimiter = "--F_DELIMITER--";
        var secondDelimiter = "--S_DELIMITER--";
        var originString = "--F_DELIMITER-- Nope --OTHER_DELIMITER--";

        // when / then
        compareExpectedWithActualResultOfGetSubStringBetweenDelimiters(originString, firstDelimiter, secondDelimiter, null);
    }

    private void compareExpectedWithActualResultOfGetSubStringBetweenDelimiters(String origin, String firstDelimiter, String secondDelimiter, String expectedResult) {
        // when
        var actualResult = velocityMailTemplateService.getSubStringBetweenDelimiters(origin, firstDelimiter, secondDelimiter);

        // then
        assertThat(actualResult).isEqualTo(expectedResult);
    }

}
