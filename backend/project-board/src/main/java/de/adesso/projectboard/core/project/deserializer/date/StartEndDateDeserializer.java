package de.adesso.projectboard.core.project.deserializer.date;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.time.LocalDate;

/**
 * A {@link DateDeserializer} that uses a {@value DATE_PATTERN} pattern to parse
 * a {@link LocalDate} from a string.
 *
 * @see de.adesso.projectboard.core.project.persistence.JiraProject
 */
public class StartEndDateDeserializer extends DateDeserializer<LocalDate> {

    private static final String DATE_PATTERN = "dd.MM.yy";

    public StartEndDateDeserializer() {
        super(DATE_PATTERN);
    }

    @Override
    public LocalDate deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonNode node = jp.getCodec().readTree(jp);

        return LocalDate.parse(node.textValue(), getDateTimeFormatter());
    }

}
