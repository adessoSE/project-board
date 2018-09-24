package de.adesso.projectboard.core.base.rest.project.deserializer.field;

import de.adesso.projectboard.core.base.rest.project.persistence.Project;

/**
 * A {@link ObjectFieldDeserializer} that returns the string value of a field named
 * {@value #FIELD_NAME} inside a json object.
 *
 * @see Project
 */
public class ObjectValueDeserializer extends ObjectFieldDeserializer {

    private static final String FIELD_NAME = "value";

    public ObjectValueDeserializer() {
        super(FIELD_NAME);
    }

}
