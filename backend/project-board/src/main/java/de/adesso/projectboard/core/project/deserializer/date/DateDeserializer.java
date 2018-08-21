package de.adesso.projectboard.core.project.deserializer.date;

import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.time.format.DateTimeFormatter;

/**
 * A {@link StdDeserializer} that supplies methods to parse dates
 * by a supplied date pattern.
 *
 * @param <T>
 *          The type to deserialize to.
 *
 * @see de.adesso.projectboard.core.project.persistence.JiraProject
 */
public abstract class DateDeserializer<T> extends StdDeserializer<T> {

    private final DateTimeFormatter dateTimeFormatter;

    public DateDeserializer(String datePattern) {
        this(null, datePattern);
    }

    public DateDeserializer(Class<?> vc, String datePattern) {
        super(vc);

        this.dateTimeFormatter = DateTimeFormatter.ofPattern(datePattern);
    }

    public DateTimeFormatter getDateTimeFormatter() {
        return dateTimeFormatter;
    }

}
