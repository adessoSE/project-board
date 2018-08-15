package de.adesso.projectboard.core.project.deserializer.date;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import de.adesso.projectboard.core.project.deserializer.date.DateDeserializer;

import java.io.IOException;
import java.time.LocalDate;

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
