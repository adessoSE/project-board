package de.adesso.projectboard.base.normalizer;

import org.junit.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class RootTermDistanceCalculatorTest {

    @Test
    public void calculateMinimalDistanceReturnsZeroWhenTermEqualsRootTerm() {
        // given
        var rootTerm = "banana";
        var term = rootTerm;

        var calculator = new RootTermDistanceCalculator(rootTerm, Set.of());

        // when
        var actualMinDistance = calculator.calculateMinimalDistance(term);

        // then
        assertThat(actualMinDistance).isEqualTo(0);
    }

    @Test
    public void calculateMinimalDistanceReturnsZeroWhenTermEqualsDerivedTerm() {
        // given
        var rootTerm = "banana";
        var derivedTerm = "yellow-thing";
        var term = derivedTerm;

        var calculator = new RootTermDistanceCalculator(rootTerm, Set.of(derivedTerm));

        // when
        var actualMinDistance = calculator.calculateMinimalDistance(term);

        // then
        assertThat(actualMinDistance).isEqualTo(0);
    }

    @Test
    public void calculateMinimalDistanceReturnsMinimalDistanceToRootTermAndDerivedTerms() {
        // given
        var rootTerm = "banana";
        var derivedTerms = Set.of("yellow-thing", "monkey-snack");
        var term = "yllow-thing";
        var expectedMinDistance = 1;

        var calculator = new RootTermDistanceCalculator(rootTerm, derivedTerms);

        // when
        var actualMinDistance = calculator.calculateMinimalDistance(term);

        // then
        assertThat(actualMinDistance).isEqualTo(expectedMinDistance);
    }

    @Test
    public void nearestRootTermReturnsExactlyMatchingRootTerm() {
        // given
        var firstCalcRootTerm = "tomato";
        var secondCalcRootTerm = "apple";
        var term = firstCalcRootTerm;

        var firstCalc = new RootTermDistanceCalculator(firstCalcRootTerm, Set.of());
        var secondCalc = new RootTermDistanceCalculator(secondCalcRootTerm, Set.of());
        var calculators = Set.of(firstCalc, secondCalc);

        // when
        var actualNearestRootTerm = RootTermDistanceCalculator.nearestRootTerm(calculators, term, 100);

        // then
        assertThat(actualNearestRootTerm).isEqualTo(firstCalcRootTerm);
    }

    @Test
    public void nearestRootTermReturnsNearestMatchingRootTerm() {
        // given
        var firstCalcRootTerm = "tomato";
        var firstCalcDerivedTerms = Set.of("tmato");
        var secondCalcRootTerm = "apple";
        var secondCalcDerivedTerms = Set.of("appel");
        var term = "apppel";

        var firstCalc = new RootTermDistanceCalculator(firstCalcRootTerm, firstCalcDerivedTerms);
        var secondCalc = new RootTermDistanceCalculator(secondCalcRootTerm, secondCalcDerivedTerms);
        var calculators = Set.of(firstCalc, secondCalc);

        // when
        var actualNearestRootTerm = RootTermDistanceCalculator.nearestRootTerm(calculators, term, 100);

        // then
        assertThat(actualNearestRootTerm).isEqualTo(secondCalcRootTerm);
    }

    @Test
    public void nearestRootTermReturnsTermWhenThresholdIsExceeded() {
        // given
        var rootTerm = "banana";
        var derivedTerms = Set.of("yellow-thing", "monkey-snack");
        var term = "ylow-thing";
        var threshold = 1; // smaller than the editing distance of 2

        var calculator = new RootTermDistanceCalculator(rootTerm, derivedTerms);

        // when
        var actualRootTerm = RootTermDistanceCalculator.nearestRootTerm(Set.of(calculator), term, threshold);

        // then
        assertThat(actualRootTerm).isEqualTo(term);
    }

}
