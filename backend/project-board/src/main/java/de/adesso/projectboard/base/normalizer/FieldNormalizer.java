package de.adesso.projectboard.base.normalizer;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class FieldNormalizer<T> implements Normalizer<T> {

    private final Set<RootTermDistanceCalculator> fieldDistanceCalculators;

    protected FieldNormalizer(Set<RootTermDistanceCalculator> fieldDistanceCalculators) {
        this.fieldDistanceCalculators = fieldDistanceCalculators;
    }

    @Override
    public List<T> normalize(List<T> objectsToNormalize) {
        var distinctValues = getDistinctFieldValues(objectsToNormalize);

        var normalizedLobMap = distinctValues.stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        distinctValue -> RootTermDistanceCalculator.nearestRootTerm(fieldDistanceCalculators, distinctValue, 7)
                ));

        return objectsToNormalize.parallelStream()
                .map(objToNormalize -> {
                    var currentLob = getFieldValue(objToNormalize);

                    if(currentLob != null) {
                        var normalizedLob = normalizedLobMap.get(currentLob);

                        return setNormalizedFieldValue(objToNormalize, normalizedLob);
                    }

                    return objToNormalize;
                })
        .collect(Collectors.toList());
    }

    private Set<String> getDistinctFieldValues(List<T> objectsToNormalize) {
        return objectsToNormalize.stream()
                .map(this::getFieldValue)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    /**
     *
     * @param fieldContainingObject
     *          The object to get un-normalized field value of, not {@code null}.
     *
     * @return
     *          The un-normalized field value of the given {@code fieldContainingObject}.
     */
    abstract String getFieldValue(T fieldContainingObject);

    /**
     *
     * @param fieldContainingObject
     *          The object to set the normalized field value in, not {@code null}.
     *
     * @param normalizedValue
     *          The normalized value, not {@code null}.
     *
     * @return
     *          The updated {@code fieldContainingObject}.
     */
    abstract T setNormalizedFieldValue(T fieldContainingObject, String normalizedValue);

}
