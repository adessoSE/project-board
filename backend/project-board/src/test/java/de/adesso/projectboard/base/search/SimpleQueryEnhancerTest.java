package de.adesso.projectboard.base.search;

import org.junit.Before;
import org.junit.Test;
import org.springframework.data.util.Pair;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class SimpleQueryEnhancerTest {

    private SimpleQueryEnhancer simpleQueryEnhancer;

    @Before
    public void setUp() {
        this.simpleQueryEnhancer = new SimpleQueryEnhancer();
    }

    @Test
    public void getReplaceableTermsOfQueryReturnsExpectedPairsForComplexQuery() {
        // given
        var simpleQuery = "(test | java) & -jenkins junnit~2 | \"continuous delivery\"~10 (apache wicket) \"kotlin\" (((typescript | spring) & testing & junit*))";
        var expectedPairs = List.of(
                Pair.of(1, 4),
                Pair.of(8, 11),
                Pair.of(17, 23),
                Pair.of(62, 67),
                Pair.of(69, 74),
                Pair.of(89, 98),
                Pair.of(102, 107),
                Pair.of(112, 118)
        );

        // when
        var actualPairs = simpleQueryEnhancer.getReplaceableTermsOfQuery(simpleQuery);

        // then
        assertThat(actualPairs).containsExactlyInAnyOrderElementsOf(expectedPairs);
    }

    @Test
    public void getReplaceableTermsOfQueryReturnsExpectedPairOfSingleWordQuery() {
        // given
        var simpleQuery = "test";
        var expectedPair = Pair.of(0, simpleQuery.length() - 1);

        // when
        var actualPairs = simpleQueryEnhancer.getReplaceableTermsOfQuery(simpleQuery);

        // then
        assertThat(actualPairs).containsExactly(expectedPair);
    }

    @Test
    public void getReplaceableTermsOfQueryReturnsExpectedPairOfNotQueryWithSingleTerm() {
        // given
        var simpleQuery = "-test";
        var expectedPair = Pair.of(1, simpleQuery.length() - 1);

        // when
        var actualPairs = simpleQueryEnhancer.getReplaceableTermsOfQuery(simpleQuery);

        // then
        assertThat(actualPairs).containsExactly(expectedPair);
    }

    @Test
    public void getReplaceableTermsOfQueryReturnsExpectedPairOfNotQueryWithMultipleTerms() {
        // given
        var simpleQuery = "-(test spring)";
        var expectedPairs = List.of(
                Pair.of(2, 5),
                Pair.of(7, 12)
        );

        // when
        var actualPairs = simpleQueryEnhancer.getReplaceableTermsOfQuery(simpleQuery);

        // then
        assertThat(actualPairs).containsExactlyInAnyOrderElementsOf(expectedPairs);
    }

    @Test
    public void getReplaceableTermsOfQueryReturnsExpectedPairsOfDisjunctionQueryWithWhitespace() {
        // given
        var simpleQuery = "test | java";
        var expectedPairs = List.of(
                Pair.of(0, 3),
                Pair.of(7, simpleQuery.length() - 1)
        );

        // when
        var actualPairs = simpleQueryEnhancer.getReplaceableTermsOfQuery(simpleQuery);

        // then
        assertThat(actualPairs).containsExactlyInAnyOrderElementsOf(expectedPairs);
    }

    @Test
    public void getReplaceableTermsOfQueryReturnsExpectedPairsOfDisjunctionQueryWithoutWhitespace() {
        // given
        var simpleQuery = "test|java";
        var expectedPairs = List.of(
                Pair.of(0, 3),
                Pair.of(5, simpleQuery.length() - 1)
        );

        // when
        var actualPairs = simpleQueryEnhancer.getReplaceableTermsOfQuery(simpleQuery);

        // then
        assertThat(actualPairs).containsExactlyInAnyOrderElementsOf(expectedPairs);
    }

    @Test
    public void getReplaceableTermsOfQueryReturnsExpectedPairsOfConjunctionQueryWithWhitespace() {
        // given
        var simpleQuery = "test & java";
        var expectedPairs = List.of(
                Pair.of(0, 3),
                Pair.of(7, simpleQuery.length() - 1)
        );

        // when
        var actualPairs = simpleQueryEnhancer.getReplaceableTermsOfQuery(simpleQuery);

        // then
        assertThat(actualPairs).containsExactlyInAnyOrderElementsOf(expectedPairs);
    }

    @Test
    public void getReplaceableTermsOfQueryReturnsExpectedPairsOfConjunctionQueryWithoutWhitespace() {
        // given
        var simpleQuery = "test&java";
        var expectedPairs = List.of(
                Pair.of(0, 3),
                Pair.of(5, simpleQuery.length() - 1)
        );

        // when
        var actualPairs = simpleQueryEnhancer.getReplaceableTermsOfQuery(simpleQuery);

        // then
        assertThat(actualPairs).containsExactlyInAnyOrderElementsOf(expectedPairs);
    }

    @Test
    public void getReplaceableTermsOfQueryReturnsExpectedPairsOfTermsSeparatedByWhitespace() {
        // given
        var simpleQuery = "test java";
        var expectedPairs = List.of(
                Pair.of(0, 3),
                Pair.of(5, simpleQuery.length() - 1)
        );

        // when
        var actualPairs = simpleQueryEnhancer.getReplaceableTermsOfQuery(simpleQuery);

        // then
        assertThat(actualPairs).containsExactlyInAnyOrderElementsOf(expectedPairs);
    }

    @Test
    public void getReplaceableTermsOfQueryDoesNotIncludeParentheses() {
        // given
        var simpleQuery = "((test))";
        var expectedPair = Pair.of(2, 5);

        // when
        var actualPairs = simpleQueryEnhancer.getReplaceableTermsOfQuery(simpleQuery);

        // then
        assertThat(actualPairs).containsExactly(expectedPair);
    }

    @Test
    public void getReplaceableTermsOfQueryIgnoresFuzzyTerms() {
        // given
        var simpleQuery = "test~2";

        // when
        var actualPairs = simpleQueryEnhancer.getReplaceableTermsOfQuery(simpleQuery);

        // then
        assertThat(actualPairs).isEmpty();
    }

    @Test
    public void getReplaceableTermsOfQueryIgnoresPrefixTerms() {
        // given
        var simpleQuery = "test*";

        // when
        var actualPairs = simpleQueryEnhancer.getReplaceableTermsOfQuery(simpleQuery);

        // then
        assertThat(actualPairs).isEmpty();
    }

    @Test
    public void getReplaceableTermsOfQueryIgnoresPhraseTerms() {
        // given
        var simpleQuery = "\"test\"";

        // when
        var actualPairs = simpleQueryEnhancer.getReplaceableTermsOfQuery(simpleQuery);

        // then
        assertThat(actualPairs).isEmpty();
    }

    @Test
    public void getReplaceableTermsOfQueryIgnoresNearOperatorOfPhraseTerms() {
        // given
        var simpleQuery = "\"test\"~10";

        // when
        var actualPairs = simpleQueryEnhancer.getReplaceableTermsOfQuery(simpleQuery);

        // then
        assertThat(actualPairs).isEmpty();
    }

    @Test
    public void getReplaceableTermsOfQueryIgnoresParentheses() {
        // given
        var simpleQuery = "(test)";
        var expectedPair = Pair.of(1, 4);

        // when
        var actualPairs = simpleQueryEnhancer.getReplaceableTermsOfQuery(simpleQuery);

        // then
        assertThat(actualPairs).containsExactly(expectedPair);
    }

    @Test
    public void getReplaceableTermsOfQueryIgnoresSinglePrefixOperator() {
        // given
        var simpleQuery = "*";

        // when
        var actualPairs = simpleQueryEnhancer.getReplaceableTermsOfQuery(simpleQuery);

        // then
        assertThat(actualPairs).isEmpty();
    }

    @Test
    public void getReplaceableTermsOfQueryIgnoresSingleFuzzyOperator() {
        // given
        var simpleQuery = "~2";

        // when
        var actualPairs = simpleQueryEnhancer.getReplaceableTermsOfQuery(simpleQuery);

        // then
        assertThat(actualPairs).isEmpty();
    }

    @Test
    public void getIndexAfterFirstAppearanceOfReturnsMinusOneWhenCharacterNotPresentAfterStartIndex() {
        // given
        var input = "test";
        var character = 'd';
        var expectedIndex = -1;
        var startIndex = 0;

        // when
        var actualIndex = simpleQueryEnhancer.getIndexAfterFirstAppearanceOf(startIndex, character, input);

        // then
        assertThat(actualIndex).isEqualTo(expectedIndex);
    }

    @Test
    public void getIndexAfterFirstAppearanceOfReturnsMinusOneWhenCharacterNotPresent() {
        // given
        var input = "abc";
        var character = 'e';
        var expectedIndex = -1;
        var startIndex = 0;

        // when
        var actualIndex = simpleQueryEnhancer.getIndexAfterFirstAppearanceOf(startIndex, character, input);

        // then
        assertThat(actualIndex).isEqualTo(expectedIndex);
    }

    @Test
    public void getIndexAfterFirstAppearanceOfReturnsMinusOneWhenStartIndexGreaterThanInputLength() {
        // given
        var input = "test";
        var character = '"';
        var expectedIndex = -1;
        var startIndex = input.length();

        // when
        var actualIndex = simpleQueryEnhancer.getIndexAfterFirstAppearanceOf(startIndex, character, input);

        // then
        assertThat(actualIndex).isEqualTo(expectedIndex);
    }

    @Test
    public void getIndexAfterFirstAppearanceOfReturnsMinusOneIfCharacterIsLastCharacter() {
        // given
        var input = "abcd";
        var character = 'd';
        var expectedIndex = -1;
        var startIndex = 0;

        // when
        var actualIndex = simpleQueryEnhancer.getIndexAfterFirstAppearanceOf(startIndex, character, input);

        // then
        assertThat(actualIndex).isEqualTo(expectedIndex);
    }

    @Test
    public void getIndexAfterFirstAppearanceOfReturnsIndexAfterFirstAppearanceAfterStartIndexWhenCharacterPresent() {
        // given
        var input = "abc test";
        var character = 't';
        var expectedIndex = 5;
        var startIndex = 3;

        // when
        var actualIndex = simpleQueryEnhancer.getIndexAfterFirstAppearanceOf(startIndex, character, input);

        // then
        assertThat(actualIndex).isEqualTo(expectedIndex);
    }

    @Test
    public void getIndexAfterFirstAppearanceOfAnyReturnsMinusOneWhenStartIndexGreaterThanInputLength() {
        // given
        var input = "test";
        var characters = Set.of('t', 'e');
        var expectedIndex = -1;
        var startIndex = 4;

        // when
        var actualIndex = simpleQueryEnhancer.getIndexAfterFirstAppearanceOfAny(startIndex, characters, input);

        // then
        assertThat(actualIndex).isEqualTo(expectedIndex);
    }

    @Test
    public void getIndexAfterFirstAppearanceOfAnyReturnsMinusOneWhenNoCharacterPresent() {
        // given
        var input = "abcd";
        var characters = Set.of('e', 'f');
        var expectedIndex = -1;
        var startIndex = 0;

        // when
        var actualIndex = simpleQueryEnhancer.getIndexAfterFirstAppearanceOfAny(startIndex, characters, input);

        // then
        assertThat(actualIndex).isEqualTo(expectedIndex);
    }

    @Test
    public void getIndexAfterFirstAppearanceOfAnyReturnsIndexAfterFirstAppearanceAfterStartIndexOfAnyCharacter() {
        // given
        var input = "xyz abcd";
        var characters = Set.of('a', 'b');
        var expectedIndex = 5;
        var startIndex = 3;

        // when
        var actualIndex = simpleQueryEnhancer.getIndexAfterFirstAppearanceOfAny(startIndex, characters, input);

        // then
        assertThat(actualIndex).isEqualTo(expectedIndex);
    }

    @Test
    public void getIndexAfterFirstAppearanceOfAnyReturnsMinusOneWhenNoCharacterPresentAfterStartIndex() {
        // given
        var input = "abcd";
        var characters = Set.of('a', 'b');
        var expectedIndex = -1;
        var startIndex = 2;

        // when
        var actualIndex = simpleQueryEnhancer.getIndexAfterFirstAppearanceOfAny(startIndex, characters, input);

        // then
        assertThat(actualIndex).isEqualTo(expectedIndex);
    }

    @Test
    public void getIndexAfterFirstAppearanceOfAnyReturnsMinusOneWhenCharacterIsLastCharacter() {
        // given
        var input = "abcd";
        var characters = Set.of('d');
        var expectedIndex = -1;
        var startIndex = 0;

        // when
        var actualIndex = simpleQueryEnhancer.getIndexAfterFirstAppearanceOfAny(startIndex, characters, input);

        // then
        assertThat(actualIndex).isEqualTo(expectedIndex);
    }

    @Test
    public void getIndexAfterPhraseTermReturnsIndexAfterNearOperator() {
        // given
        var simpleQuery = "\"java\"~10 test";
        var startIndex = 0;
        var expectedIndex = 10;

        // when
        var actualIndex = simpleQueryEnhancer.getIndexAfterPhraseTerm(startIndex, simpleQuery);

        // then
        assertThat(actualIndex).isEqualTo(expectedIndex);
    }

    @Test
    public void getIndexAfterPhraseTermReturnsIndexAfterSecondQuotationMarks() {
        // given
        var simpleQuery = "\"java\" test";
        var startIndex = 0;
        var expectedIndex = 6;

        // when
        var actualIndex = simpleQueryEnhancer.getIndexAfterPhraseTerm(startIndex, simpleQuery);

        // then
        assertThat(actualIndex).isEqualTo(expectedIndex);
    }

    @Test
    public void getIndexAfterPhraseTermReturnsMinusOneWhenStartIndexGreaterThanInputLength() {
        // given
        var simpleQuery = "\"java\"";
        var startIndex = 6;
        var expectedIndex = -1;

        // when
        var actualIndex = simpleQueryEnhancer.getIndexAfterPhraseTerm(startIndex, simpleQuery);

        // then
        assertThat(actualIndex).isEqualTo(expectedIndex);
    }

    @Test
    public void getIndexAfterPhraseTermReturnsMinusOneWhenQuotationMarkIsLastCharacter() {
        // given
        var simpleQuery = "\"java\"";
        var startIndex = 0;
        var expectedIndex = -1;

        // when
        var actualIndex = simpleQueryEnhancer.getIndexAfterPhraseTerm(startIndex, simpleQuery);

        // then
        assertThat(actualIndex).isEqualTo(expectedIndex);
    }

    @Test
    public void getIndexAfterPhraseTermReturnsMinusOneWhenQuotationNearOperatorIs() {
        // given
        var simpleQuery = "\"java\"";
        var startIndex = 0;
        var expectedIndex = -1;

        // when
        var actualIndex = simpleQueryEnhancer.getIndexAfterPhraseTerm(startIndex, simpleQuery);

        // then
        assertThat(actualIndex).isEqualTo(expectedIndex);
    }

    @Test
    public void getIndexOfFirstAppearanceOfReturnsMinusOneWhenCharacterNotPresentAfterStartIndex() {
        // given
        var input = "abcd";
        var characters = Set.of('b', 'f');
        var startIndex = 2;
        var expectedIndex = -1;

        // when
        var actualIndex = simpleQueryEnhancer.getIndexOfFirstAppearanceOfAny(startIndex, characters, input);

        // then
        assertThat(actualIndex).isEqualTo(expectedIndex);
    }

    @Test
    public void getIndexOfFirstAppearanceOfAnyReturnsMinusOneWhenStartIndexGreaterThanInputLength() {
        // given
        var input = "abcd";
        var characters = Set.of('b');
        var startIndex = 3;
        var expectedIndex = -1;

        // when
        var actualIndex = simpleQueryEnhancer.getIndexOfFirstAppearanceOfAny(startIndex, characters, input);

        // then
        assertThat(actualIndex).isEqualTo(expectedIndex);
    }

    @Test
    public void getIndexOfFirstAppearanceOfAnyReturnsIndexOfFirstAppearanceAfterStartIndex() {
        // given
        var input = "baba";
        var characters = Set.of('a', 'b');
        var startIndex = 1;
        var expectedIndex = 1;

        // when
        var actualIndex = simpleQueryEnhancer.getIndexOfFirstAppearanceOfAny(startIndex, characters, input);

        // then
        assertThat(actualIndex).isEqualTo(expectedIndex);
    }

}
