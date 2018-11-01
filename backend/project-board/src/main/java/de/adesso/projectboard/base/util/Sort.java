package de.adesso.projectboard.base.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Comparator;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Sort {

    private String fieldName;

    private Order order;

    public enum Order {
        ASCENDING,
        DESCENDING
    }

    public static <T> Comparator<T> toComparator(Class<T> comparatorClass, List<Sort> sorts) {
        sorts.forEach(sort -> {
            try {
                comparatorClass.getDeclaredField(sort.getFieldName());


            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        });
    }

}
