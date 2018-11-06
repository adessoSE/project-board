package de.adesso.projectboard.base.util;

import lombok.Getter;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@Getter
public class Sorting {

    private final LinkedHashMap<String, Order> propertyOrderMap;

    public Sorting(LinkedHashMap<String, Order> propertyOrderMap) {
        this.propertyOrderMap = propertyOrderMap;
    }

    public Sorting() {
        this.propertyOrderMap = new LinkedHashMap<>();
    }

    public void addPair(String fieldName, Order order) {
        propertyOrderMap.put(fieldName, order);
    }

    /**
     *
     * @return
     *          A Spring data {@link Sort} instance.
     */
    public Sort toSort() {
        if(propertyOrderMap.isEmpty()) {
            return Sort.unsorted();
        }

        List<Sort.Order> orderList = new ArrayList<>();
        propertyOrderMap.forEach((fieldName, order) -> {
            switch (order) {
                case ASCENDING:
                    orderList.add(new Sort.Order(Sort.Direction.ASC, fieldName));
                    break;

                case DESCENDING:
                    orderList.add(new Sort.Order(Sort.Direction.DESC, fieldName));
                    break;
            }
        });

        return Sort.by(orderList);
    }

    public static Sorting fromSort(Sort sort) {
        LinkedHashMap<String, Order> propertyOrderMap = new LinkedHashMap<>();

        sort.forEach(order -> {
            switch(order.getDirection()) {
                case ASC:

                    break;

                case DESC:
                    break;
            }
        });

        return new Sorting(propertyOrderMap);
    }

    public enum Order {
        ASCENDING,
        DESCENDING
    }

}
