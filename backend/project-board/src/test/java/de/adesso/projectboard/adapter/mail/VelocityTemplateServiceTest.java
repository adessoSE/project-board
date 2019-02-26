package de.adesso.projectboard.adapter.mail;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.StringWriter;
import java.util.Map;
import java.util.Objects;

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

    private VelocityTemplateService velocityTemplateService;

    @Before
    public void setUp() {
        this.velocityTemplateService = new VelocityTemplateService(velocityEngineMock);
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
        var actualResult = velocityTemplateService.mergeTemplate(templatePath, contextMap);

        // then
        assertThat(actualResult).isEqualTo(expectedResult);
    }

    class VelocityContextArgumentMatcher implements ArgumentMatcher<VelocityContext> {

        private final Map<String, Object> referenceMap;

        VelocityContextArgumentMatcher(Map<String, Object> referenceMap) {
            this.referenceMap = referenceMap;
        }

        @Override
        public boolean matches(VelocityContext ctx) {
            return referenceMap.entrySet().stream()
                    .allMatch(entry -> Objects.nonNull(ctx.internalGet(entry.getKey()))) &&
                    ctx.internalGetKeys().length == referenceMap.entrySet().size();
        }

    }

    class StringWriterArgumentMatcher implements ArgumentMatcher<StringWriter> {

        private final String referenceString;

        StringWriterArgumentMatcher(String referenceString) {
            this.referenceString = referenceString;
        }

        @Override
        public boolean matches(StringWriter writer) {
            return writer.toString().equals(referenceString);
        }

    }

}