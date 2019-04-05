package de.adesso.projectboard.base.search;

import lombok.NonNull;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class SimpleQueryEnhancer {

    final Set<Character> TERM_DELIMITER = Set.of('(', ')', '|', ' ', '&');

    final Set<Character> SPECIAL_CHARACTER = Set.of('~', '*');

    public String enhanceSimpleQuery(@NonNull String simpleQuery) {
        return "";
    }

    int getIndexAfterNextAppearanceOfAny(int startIndex, Set<Character> characters, String simpleQuery) {
        var queryCharCount = simpleQuery.length();

        for(var currIndex = startIndex + 1; currIndex < queryCharCount; currIndex++) {
            if(characters.contains(simpleQuery.charAt(currIndex)) && (currIndex + 1 ) != queryCharCount) {
                return currIndex + 1;
            }
        }

        return -1;
    }

    int getIndexAfterNextAppearanceOf(int startIndex, char character, String simpleQuery) {
        if(startIndex >= simpleQuery.length()) {
            return -1;
        }

        return simpleQuery.substring(startIndex + 1).indexOf(character);
    }

    Set<Pair<Integer, Integer>> getReplacableTermsOfQuery(String simpleQuery) {
        var pairs = new HashSet<Pair<Integer, Integer>>();

        var index = 0;
        while(index >= 0 && index < simpleQuery.length()) {
            var currentChar = simpleQuery.charAt(index);

            if(currentChar == '"') {
                index = getIndexAfterNextAppearanceOf(index, '"', simpleQuery);
                continue;
            }

            if(currentChar == '-' || SPECIAL_CHARACTER.contains(currentChar)) {
                index = getIndexAfterNextAppearanceOfAny(index, TERM_DELIMITER, simpleQuery);
                continue;
            }


        }

        return pairs;
    }

}
