package de.adesso.projectboard.base.normalizer;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class LobNormalizer<T> implements Normalizer<T> {

    private final Set<RootTermDistanceCalculator> lobDistanceCalculators;

    protected LobNormalizer(Set<RootTermDistanceCalculator> lobDistanceCalculators) {
        this.lobDistanceCalculators = lobDistanceCalculators;
    }

    @Override
    public List<T> normalize(List<T> objectsToNormalize) {
        var distinctLobs = getDistinctLobs(objectsToNormalize);

        var normalizedLobMap = distinctLobs.stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        distinctLob -> RootTermDistanceCalculator.nearestRootTerm(lobDistanceCalculators, distinctLob, 100)
                ));

        return objectsToNormalize.parallelStream()
                .map(objToNormalize -> {
                    var currentLob = getLobOf(objToNormalize);

                    if(currentLob != null) {
                        var normalizedLob = normalizedLobMap.get(currentLob);

                        return setNormalizedLob(objToNormalize, normalizedLob);
                    }

                    return objToNormalize;
                })
        .collect(Collectors.toList());
    }

    private Set<String> getDistinctLobs(List<T> objectsToNormalize) {
        return objectsToNormalize.stream()
                .map(this::getLobOf)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    /**
     *
     * @param lobContainingObject
     *          The object to get the LoB of, not {@code null}.
     *
     * @return
     *          The lob of the given {@code lobContainingObject}.
     */
    abstract String getLobOf(T lobContainingObject);

    /**
     *
     * @param lobContainingObject
     *          The object to set the normalized LoB of, not {@code null}.
     *
     * @param normalizedLob
     *          The normalized LoB, not {@code null}.
     *
     * @return
     *          The updated {@code lobContainingObject}.
     */
    abstract T setNormalizedLob(T lobContainingObject, String normalizedLob);

}
