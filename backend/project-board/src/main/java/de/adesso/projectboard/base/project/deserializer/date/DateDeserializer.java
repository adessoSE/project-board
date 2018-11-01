package de.adesso.projectboard.base.project.deserializer.date;

import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import de.adesso.projectboard.base.project.persistence.Project;

import java.time.format.DateTimeFormatter;

/**
 * A {@link StdDeserializer} that supplies methods to parse dates
 * by a supplied date pattern.
 *
 * @param <T>
 *          The type to deserialize to.
 *
 * @see Project
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
