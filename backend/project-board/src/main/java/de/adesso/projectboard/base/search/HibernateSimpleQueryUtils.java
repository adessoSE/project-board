package de.adesso.projectboard.base.search;

import lombok.NonNull;
import org.springframework.data.util.Pair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class HibernateSimpleQueryUtils {

    private static final Set<Character> TERM_DELIMITER = Set.of('(', ')', '|', ' ', '&', '-');

    private static final Set<Character> SPECIAL_CHARACTERS = Set.of('~', '*');

    /**
     * Replaces all simple terms in a given Hibernate search simple query. A simple
     * term is a term that is neither fuzzy (eg. <i>java~2</i>) nor a prefix term
     * (eg. <i>java*</i>) or phrase term (eg. <i>"java"</i>). The term gets replaced
     * by a disjunction of the original term, a fuzzy term with a maximum editing distance
     * of {@code 1} and a prefix term.
     *
     * <p/>
     *
     * <b>Example</b>: The simple query <i>((java | junit*) & spring*)</i> will be converted
     * to <i>((<b>(java | java~2 | java*)</b> | junit*) & spring*)</i>.
     *
     * @param simpleQuery
     *          The simple query to replace all simple terms in, not null.
     *
     * @return
     *          A trimmed simple query with all simple terms replaced.
     */
    public static String makeQueryPrefixAndFuzzy(@NonNull String simpleQuery) {
        var trimmedSimpleQuery = simpleQuery.trim();
        var replaceableTermIndexPairs = getReplaceableTermsOfQuery(trimmedSimpleQuery);

        if(replaceableTermIndexPairs.isEmpty()) {
            return trimmedSimpleQuery;
        }

        var fuzzyAndPrefixQuery = trimmedSimpleQuery;
        var addedOffsetByReplacingTerms = 0;

        for(var pair : replaceableTermIndexPairs) {
            var termStartIndex = pair.getFirst() + addedOffsetByReplacingTerms;
            var termEndIndex = pair.getSecond() + addedOffsetByReplacingTerms;
            var termStartEndIndexPair = Pair.of(termStartIndex, termEndIndex);

            var preReplacementQueryLength = fuzzyAndPrefixQuery.length();
            fuzzyAndPrefixQuery = replaceTermWithFuzzyAndPrefixDisjunction(termStartEndIndexPair, fuzzyAndPrefixQuery);
            var postReplacementQueryLength = fuzzyAndPrefixQuery.length();

            addedOffsetByReplacingTerms += postReplacementQueryLength - preReplacementQueryLength;
        }

        return fuzzyAndPrefixQuery;
    }

    /**
     *
     * @param startEndIndexPair
     *          The start and end index pair of the term to replace, not null.
     *
     * @param simpleQuery
     *          The simple query to replace the term in, not null.
     *
     * @return
     *          A query where the given term is replaced by a disjunction of
     *          the original term, a fuzzy term with a maximum editing distance of {@code 1}
     *          and a prefix term.
     */
    static String replaceTermWithFuzzyAndPrefixDisjunction(Pair<Integer, Integer> startEndIndexPair, String simpleQuery) {
        var termStartIndex = startEndIndexPair.getFirst();
        var termEndIndex = startEndIndexPair.getSecond();

        var term = simpleQuery.substring(termStartIndex, termEndIndex + 1);
        var replaceTerm = String.format("(%s)",
                createHibernateSearchDisjunction(List.of(term, term + "~1", term + "*")));

        return replaceSubstring(startEndIndexPair, simpleQuery, replaceTerm);
    }

    /**
     *
     * @param startEndIndexPair
     *          A pair of the start and end index of the substring to replace, not null.
     *
     * @param stringToReplaceIn
     *          The string to replace the substring in, not null.
     *
     * @param replacement
     *          The string to replace the substring with.
     *
     * @return
     *          The string the substring is replaced in.
     */
    static String replaceSubstring(Pair<Integer, Integer> startEndIndexPair, String stringToReplaceIn, String replacement) {
        var startIndex = startEndIndexPair.getFirst();
        var endIndex = startEndIndexPair.getSecond();

        var predReplaceSubstring = startIndex <= 1 ? "" : stringToReplaceIn.substring(0, startIndex);
        var succReplaceSubstring = endIndex >= (stringToReplaceIn.length() - 1) ? "" : stringToReplaceIn.substring(endIndex + 1);

        return String.format("%s%s%s", predReplaceSubstring, replacement, succReplaceSubstring);
    }

    /**
     *
     * @param startIndex
     *          The index to start searching for the given {@code character} at (<i>inclusive</i>),
     *          must be greater than or equal to zero.
     *
     * @param characters
     *          The characters to search for, not null.
     *
     * @param searchedString
     *          The string to search in, not null.
     *
     * @return
     *          The index following the first appearance of any of the given {@code characters} greater or
     *          equal to the given {@code startIndex} or {@code -1} if no character was not found
     *          or it's the last character of the string.
     */
    static int getIndexAfterFirstAppearanceOfAny(int startIndex, Set<Character> characters, String searchedString) {
        var queryCharCount = searchedString.length();

        if(startIndex >= queryCharCount) {
            return -1;
        }

        for(var currentIndex = startIndex; currentIndex < queryCharCount; currentIndex++) {
            if(characters.contains(searchedString.charAt(currentIndex)) && (currentIndex + 1 ) != queryCharCount) {
                return currentIndex + 1;
            }
        }

        return -1;
    }

    /**
     *
     * @param startIndex
     *          The index to start searching for the given {@code character} at (<i>inclusive</i>),
     *          must be greater than or equal to zero.
     *
     * @param character
     *          The character to get the index following its first appearance of.
     *
     * @param searchedString
     *          The string to search in, not null.
     *
     * @return
     *          The index following the first appearance of the given {@code character} greater or
     *          equal to the given {@code startIndex} or {@code -1} if the character was not found
     *          or it's the last character of the string.
     */
    static int getIndexAfterFirstAppearanceOf(int startIndex, char character, String searchedString) {
        var queryCharCount = searchedString.length();

        if(startIndex >= queryCharCount) {
            return -1;
        }

        var indexAfterFirstAppearance = searchedString.substring(startIndex).indexOf(character) + startIndex + 1;
        if(indexAfterFirstAppearance > 0 && indexAfterFirstAppearance < queryCharCount) {
            return indexAfterFirstAppearance;
        }

        return -1;
    }

    /**
     *
     * @param startIndex
     *          The index to start searching for one of the given {@code characters} at (<i>inclusive</i>),
     *          must be greater than or equal to zero.
     *
     * @param characters
     *          The characters to search for, not null.
     *
     * @param searchedString
     *          The string to search in, not null.
     *
     * @return
     *          The index of the first appearance of any character contained inside the
     *          {@code characters} set that is greater than or equal to the given {@code startIndex}
     *          or {@code -1} if no character was found.
     */
    static int getIndexOfFirstAppearanceOfAny(int startIndex, Set<Character> characters, String searchedString) {
        var queryCharCount = searchedString.length();

        if(startIndex >= queryCharCount) {
            return -1;
        }

        for(var currentIndex = startIndex; currentIndex < queryCharCount; currentIndex++) {
            if(characters.contains(searchedString.charAt(currentIndex))) {
                return currentIndex;
            }
        }

        return -1;
    }

    /**
     *
     * @param startIndex
     *          The start index of the phrase term, so the index of the
     *          first quotation mark, must be greater than or equal to zero.
     *
     * @param simpleQuery
     *          The query to get the index following the phrase term
     *          of, not null.
     *
     * @return
     *          The index following the phrase term or {@code -1} if it's there is
     *          no index after the phrase term.
     */
    static int getIndexAfterPhraseTerm(int startIndex, String simpleQuery) {
        var queryCharCount = simpleQuery.length();

        if(startIndex >= queryCharCount) {
            return -1;
        }

        var endIndex = getIndexAfterFirstAppearanceOf(startIndex + 1, '"', simpleQuery);

        if(endIndex > 0 && endIndex < queryCharCount && simpleQuery.charAt(endIndex) == '~') {
            endIndex = getIndexAfterFirstAppearanceOfAny(endIndex, TERM_DELIMITER, simpleQuery);
        }

        return endIndex;
    }

    /**
     *
     * @param simpleQuery
     *          The simple query to get the replaceable terms of, not null.
     *
     * @return
     *          A list of {@code [startindex, endindex]} pairs of
     *          each replaceable term of the query ordered in ascending order.
     */
    static List<Pair<Integer, Integer>> getReplaceableTermsOfQuery(String simpleQuery) {
        var termIndexPairs = new ArrayList<Pair<Integer, Integer>>();
        var queryCharCount = simpleQuery.length();

        var index = 0;
        while(index >= 0 && index < queryCharCount) {
            var currentChar = simpleQuery.charAt(index);

            // skip term delimiters
            if(TERM_DELIMITER.contains(currentChar)) {
                index++;
                continue;
            }

            // ignore phrase terms and near operators
            if(currentChar == '"') {
                index = getIndexAfterPhraseTerm(index, simpleQuery);
                continue;
            }

            var nextSpecialCharIndex = getIndexOfFirstAppearanceOfAny(index, SPECIAL_CHARACTERS, simpleQuery);
            var nextTermDelimiterIndex = getIndexOfFirstAppearanceOfAny(index, TERM_DELIMITER, simpleQuery);

            // no term delimiter found after the current term
            if(nextTermDelimiterIndex < 0) {
                nextTermDelimiterIndex = queryCharCount;
            }
            var termEndIndex = nextTermDelimiterIndex - 1;

            // when a term delimiter is present and the term is not a fuzzy or prefix term add
            // the pair -> ignore fuzzy and prefix terms
            var isSimpleTerm = nextSpecialCharIndex < 0 || nextTermDelimiterIndex < nextSpecialCharIndex;
            if(isSimpleTerm) {
                termIndexPairs.add(Pair.of(index, termEndIndex));

                index = nextTermDelimiterIndex + 1;
            } else {
                // skip this term
                index = getIndexAfterFirstAppearanceOfAny(nextSpecialCharIndex, TERM_DELIMITER, simpleQuery);
            }
        }

        return termIndexPairs;
    }

    /**
     *
     * @param values
     *          The values to create the disjunction of, not null.
     *
     * @return
     *          A hibernate simple query representing a disjunction of all given
     *          {@code values}.
     *          .
     */
    public static String createHibernateSearchDisjunction(Collection<String> values) {
        var valueArr = values.toArray(String[]::new);
        var fieldMatchStringBuilder = new StringBuilder(valueArr[0]);

        for(var valueIndex = 1; valueIndex < valueArr.length; valueIndex++) {
            fieldMatchStringBuilder
                    .append(" | ")
                    .append(valueArr[valueIndex]);
        }

        return fieldMatchStringBuilder.toString();
    }

}
