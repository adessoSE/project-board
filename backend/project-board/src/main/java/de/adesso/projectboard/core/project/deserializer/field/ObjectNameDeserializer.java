package de.adesso.projectboard.core.project.deserializer.field;

/**
 * A {@link ObjectFieldDeserializer} that returns the string value of a field named
 * {@value #FIELD_NAME} inside a json object.
 *
 * @see de.adesso.projectboard.core.project.persistence.JiraProject
 */
public class ObjectNameDeserializer extends ObjectFieldDeserializer {

    private static final String FIELD_NAME = "name";

    public ObjectNameDeserializer() {
        super(FIELD_NAME);
    }

}
