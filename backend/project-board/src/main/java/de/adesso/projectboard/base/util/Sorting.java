package de.adesso.projectboard.base.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Sorting {

    private String fieldName;

    private Order order;

    public enum Order {
        ASCENDING,
        DESCENDING
    }

    public static <T> Comparator<T> toComparator(Class<T> comparatorClass, List<Sorting> sortings) {
        sortings.forEach(sorting -> {
            try {
                Field field = comparatorClass.getDeclaredField(sorting.getFieldName());

            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        });
    }

}
