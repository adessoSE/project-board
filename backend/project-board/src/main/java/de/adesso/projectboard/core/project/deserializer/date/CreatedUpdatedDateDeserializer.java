package de.adesso.projectboard.core.project.deserializer.date;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import de.adesso.projectboard.core.project.deserializer.date.DateDeserializer;

import java.io.IOException;
import java.time.LocalDateTime;

public class CreatedUpdatedDateDeserializer extends DateDeserializer<LocalDateTime> {

    private static final String DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

    public CreatedUpdatedDateDeserializer() {
        super(DATE_PATTERN);
    }

    @Override
    public LocalDateTime deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonNode node = jp.getCodec().readTree(jp);

        return LocalDateTime.parse(node.textValue(), getDateTimeFormatter());
    }

}
