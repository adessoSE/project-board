package de.adesso.projectboard.base.search;

import lombok.NonNull;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class SimpleQueryEnhancer {

    private final Set<Character> TERM_DELIMITER = Set.of('(', ')', '|', ' ', '&', '-');

    private final Set<Character> SPECIAL_CHARACTERS = Set.of('~', '*');

    public String enhanceSimpleQuery(@NonNull String simpleQuery) {
        var trimmedSimpleQuery = simpleQuery.trim();
        var replaceableTermIndexPairs = getReplaceableTermsOfQuery(trimmedSimpleQuery);

        if(replaceableTermIndexPairs.isEmpty()) {
            return trimmedSimpleQuery;
        }

        var addedOffset = 0;
        for(var pair : replaceableTermIndexPairs) {

        }

        return trimmedSimpleQuery;
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
    int getIndexAfterFirstAppearanceOfAny(int startIndex, Set<Character> characters, String searchedString) {
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
    int getIndexAfterFirstAppearanceOf(int startIndex, char character, String searchedString) {
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
    int getIndexOfFirstAppearanceOfAny(int startIndex, Set<Character> characters, String searchedString) {
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
    int getIndexAfterPhraseTerm(int startIndex, String simpleQuery) {
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
     *          A set of {@code [startindex, endindex]} pairs of
     *          each replaceable term inside the query.
     */
    List<Pair<Integer, Integer>> getReplaceableTermsOfQuery(String simpleQuery) {
        var termIndexPairs = new ArrayList<Pair<Integer, Integer>>();
        var queryCharCount = simpleQuery.length();

        var index = 0;
        while(index >= 0 && index < queryCharCount) {
            var currentChar = simpleQuery.charAt(index);

            // ignore phrase terms and near operators
            if(currentChar == '"') {
                index = getIndexAfterPhraseTerm(index, simpleQuery);
                continue;
            }

            // skip term delimiters
            if(TERM_DELIMITER.contains(currentChar)) {
                index++;
                continue;
            }

            var nextSpecialCharIndex = getIndexOfFirstAppearanceOfAny(index, SPECIAL_CHARACTERS, simpleQuery);
            var nextTermDelimiterIndex = getIndexOfFirstAppearanceOfAny(index, TERM_DELIMITER, simpleQuery);

            // no term delimiter found
            if(nextTermDelimiterIndex < 0) {
                nextTermDelimiterIndex = queryCharCount;
            }
            var termEndIndex = nextTermDelimiterIndex - 1;

            // when a term delimiter is present and the term is not a fuzzy or prefix term add
            // the pair -> ignore fuzzy and prefix terms
            if(nextSpecialCharIndex < 0 || nextTermDelimiterIndex < nextSpecialCharIndex) {
                termIndexPairs.add(Pair.of(index, termEndIndex));

                index = nextTermDelimiterIndex + 1;
            } else {
                // skip this term
                index = getIndexAfterFirstAppearanceOfAny(nextSpecialCharIndex, TERM_DELIMITER, simpleQuery);
            }
        }

        return termIndexPairs;
    }

}
