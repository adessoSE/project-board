package helper.adapter.mail;

import org.mockito.ArgumentMatcher;

import java.io.StringWriter;

public class StringWriterArgumentMatcher implements ArgumentMatcher<StringWriter> {

    private final String referenceString;

    public StringWriterArgumentMatcher(String referenceString) {
        this.referenceString = referenceString;
    }

    @Override
    public boolean matches(StringWriter writer) {
        return writer.toString().equals(referenceString);
    }

}
