package de.adesso.projectboard.base.normalizer;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.apache.commons.text.similarity.LevenshteinDistance;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
@EqualsAndHashCode
public class RootTermDistanceCalculator {

    /**
     * The root term as it was passed to in the constructor with all leading
     * and trailing whitespace removed to keep the original capitalization.
     */
    private final String originalRootTerm;

    /**
     * The lower case root term that is the desired normalized form.
     */
    private final String lowerCaseRootTerm;

    /**
     * A list of all derived terms that are equivalent to the
     * root term, but differ in the formatting.
     */
    private final Set<String> derivedTerms;

    /**
     * <b>Note:</b> Both the {@code lowerCaseRootTerm} and {@code derivedTerms} are converted to
     * lower case strings with all leading and trailing whitespace removed.
     *
     * @param rootTerm
     *          The root term that serves as the normalized form, not {@code null}.
     *
     * @param derivedTerms
     *          The derived term that are semantically equal to the root term, not {@code null}.
     */
    public RootTermDistanceCalculator(String rootTerm, Set<String> derivedTerms) {
        this.originalRootTerm = rootTerm.strip();
        this.lowerCaseRootTerm = originalRootTerm.toLowerCase();
        this.derivedTerms = derivedTerms.stream()
                .map(String::toLowerCase)
                .map(String::strip)
                .collect(Collectors.toSet());
    }

    /**
     * Calculates the Levenshtein distance between the {@link #getLowerCaseRootTerm() lower case root term} and all
     * {@link #getDerivedTerms() lower case derived terms} and returns the minimal distance. The given
     * {@code stringToMatch} is converted to a lower case string.
     *
     * @param stringToMatch
     *          The string that should be matched, not {@code null}.
     *
     * @return
     *          The minimal Levenshtein distance between the given {@code stringToMatch}
     *          and the root/derived term(s).
     */
    public int calculateMinimalDistance(String stringToMatch) {
        var lowerCaseStringToMatch = stringToMatch.toLowerCase();
        var mergedSet = mergeRootTermAndDerivedTerms();
        boolean matchesRootTermOrDerivedTerm = mergedSet.contains(lowerCaseStringToMatch);

        if(matchesRootTermOrDerivedTerm) {
            return 0;
        }

        return mergedSet.parallelStream()
                .mapToInt(term -> calculateLevenshteinDistance(term, lowerCaseStringToMatch))
                .min()
                .orElse(0);
    }

    /**
     * Matches a given term against multiple calculators and returns the nearest root term
     * in terms of Levenshtein distance.
     *
     * Example:
     *
     * <pre>
     *     First Calculator:
     *          Root Term: {@code salad}
     *          Derived Terms: {@code salat}
     *
     *     Second Calculator:
     *          Root Term: {@code stone}
     *          Derived Terms: {@code stein}
     *
     *     Term: {@code sthein}
     *
     *     Returned Root Term: {@code stone}
     * </pre>
     *
     * @param distanceCalculators
     *          The {@link RootTermDistanceCalculator}s to match the {@code term}
     *          against, not {@code null} or empty.
     *
     * @param term
     *          The term to match against the given {@code distanceCalculators}, not {@code null}.
     *
     * @param threshold
     *          The max. distance that is allowed as the minimal distance, must be greater than or
     *          equal to {@code 0}.
     *
     * @return
     *          The root term of the {@link RootTermDistanceCalculator} with the lowest
     *          editing distance returned by its {@link #calculateMinimalDistance(String)}
     *          method. May lead to an undefined return value in case multiple calculators return
     *          the same min distance.
     *
     * @throws IllegalStateException
     *          In case the lowest editing distance is greater than the given {@code threshold}.
     */
    public static String nearestRootTerm(Set<RootTermDistanceCalculator> distanceCalculators, String term, int threshold) {
        if(threshold < 0) {
            throw new IllegalArgumentException("Threshold must be greater than or equal to 0");
        }

        var calculatorMindDistanceEntries = distanceCalculators.parallelStream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        distanceCalculator -> distanceCalculator.calculateMinimalDistance(term)
                ))
                .entrySet();

        var minDistanceEntry = Collections.min(calculatorMindDistanceEntries, Comparator.comparingInt(Map.Entry::getValue));

        if(minDistanceEntry.getValue() <= threshold) {
            return minDistanceEntry.getKey().getOriginalRootTerm();
        } else {
            throw new IllegalStateException("The minimal distance of all calculators exceeds the threshold value");
        }
    }

    private int calculateLevenshteinDistance(String firstString, String secondString) {
        var levenshteinDistance = new LevenshteinDistance();
        return levenshteinDistance.apply(firstString, secondString);
    }

    private Set<String> mergeRootTermAndDerivedTerms() {
        var mergeSet = new HashSet<>(derivedTerms);
        mergeSet.add(lowerCaseRootTerm);
        return mergeSet;
    }

}
