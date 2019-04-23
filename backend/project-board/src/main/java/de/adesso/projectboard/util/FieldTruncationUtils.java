package de.adesso.projectboard.util;

import lombok.NonNull;
import org.springframework.util.ReflectionUtils;

import javax.persistence.Column;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Objects;

/**
 * Util class using spring's {@link ReflectionUtils} class to truncate non-final string field values
 * to the column length of the field.
 */
public class FieldTruncationUtils {

    public static final int DEFAULT_COLUMN_LENGTH = 255;

    /**
     * Truncates all non-final string field values of a given {@code entity} to their column
     * length. The column length used either the length specified in the JPA {@link Column}
     * annotation (if present) or the default lenght of {@value DEFAULT_COLUMN_LENGTH}.
     *
     * @param entity
     *          The entity to truncate the string field values of, not null.
     *
     * @param <T>
     *          The entity type.
     *
     * @return
     *          The entity with all string field values truncated to their
     *          columns length.
     */
    public static <T> T truncateStringsToColumnLengths(@NonNull T entity) {
        ReflectionUtils.doWithFields(entity.getClass(), new StringTruncationFieldCallback(entity), new StringFieldFilter());
        return entity;
    }

    static class StringTruncationFieldCallback implements ReflectionUtils.FieldCallback {

        private final Object entity;

        StringTruncationFieldCallback(Object entity) {
            this.entity = entity;
        }

        @Override
        public void doWith(Field field) throws IllegalArgumentException {
            ReflectionUtils.makeAccessible(field);

            var columnLength = getColumnLengthOfField(field);
            var originalString = (String) ReflectionUtils.getField(field, entity);
            var truncatedString = truncateString(originalString, columnLength);
            ReflectionUtils.setField(field, entity, truncatedString);
        }

        int getColumnLengthOfField(Field field) {
            var columnAnnotation = field.getAnnotation(Column.class);
            if(Objects.nonNull(columnAnnotation)) {
                return columnAnnotation.length();
            }

            return DEFAULT_COLUMN_LENGTH;
        }

        String truncateString(String originalString, int columnLength) {
            if(Objects.isNull(originalString) || originalString.length() <= columnLength) {
                return originalString;
            }

            if(columnLength <= 0) {
                return "";
            } else {
                return originalString.substring(0, columnLength);
            }
        }

    }

    static class StringFieldFilter implements ReflectionUtils.FieldFilter {

        @Override
        public boolean matches(Field field) {
            return field.getType().equals(String.class) && !Modifier.isFinal(field.getModifiers());
        }

    }

}
