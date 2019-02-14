package de.adesso.projectboard.base.project.deserializer.field;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import de.adesso.projectboard.base.project.persistence.Project;

import java.io.IOException;

/**
 * A {@link StdDeserializer<Boolean>} that returns the true if attachment contains a string, false otherwise.
 *
 * @see Project
 */
public class ObjectAttachmentDeserializer extends StdDeserializer<Boolean> {
    private ObjectAttachmentDeserializer(Class<?> vc) {
        super(vc);
    }

    public ObjectAttachmentDeserializer() {
        this(null);
    }

    @Override
    public Boolean deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonNode node = p.getCodec().readTree(p);
        return !node.toString().equals("[]");
    }
}