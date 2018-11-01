package de.adesso.projectboard.base.project.deserializer.field;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import de.adesso.projectboard.base.project.persistence.Project;

import java.io.IOException;

/**
 * A {@link StdDeserializer} that returns a string value of a field inside a json object.
 *
 * @see Project
 */
public abstract class ObjectFieldDeserializer extends StdDeserializer<String> {

    private final String fieldName;

    public ObjectFieldDeserializer(String fieldName) {
        this(null, fieldName);
    }

    public ObjectFieldDeserializer(Class<?> vc, String fieldName) {
        super(vc);

        this.fieldName = fieldName;
    }

    @Override
    public String deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonNode node = jp.getCodec().readTree(jp);

        return node.get(fieldName).textValue();
    }

}
