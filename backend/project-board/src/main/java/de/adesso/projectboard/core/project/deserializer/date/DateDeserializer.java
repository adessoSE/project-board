package de.adesso.projectboard.core.project.deserializer.date;

import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.time.format.DateTimeFormatter;

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
