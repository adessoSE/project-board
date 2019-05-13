package de.adesso.projectboard.adapter.velocity;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.util.Pair;

import javax.validation.constraints.NotNull;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Service providing convenience methods for apache velocity templates.
 */
public class VelocityTemplateService {

    static final String SUBJECT_DELIMITER = "--!--SUBJECT--!--";

    static final String TEXT_DELIMITER = "--!--TEXT--!--";

    private final VelocityEngine velocityEngine;

    public VelocityTemplateService(VelocityEngine velocityEngine) {
        this.velocityEngine = velocityEngine;
    }

    /**
     *
     * @param templatePath
     *          The template path and name of the velocity template, not null.
     *
     * @param contextMap
     *          The key/value pairs to add to the velocity context storage, may be null.
     *
     * @return
     *          The result of merging to context and the template.
     */
    public String mergeTemplate(@NotNull String templatePath, Map<String, Object> contextMap) {
        var velocityTemplate = velocityEngine.getTemplate(templatePath);
        var velocityContext = new VelocityContext(new HashMap<>(contextMap));
        var writer = new StringWriter();
        velocityTemplate.merge(velocityContext, writer);

        return writer.toString();
    }

    /**
     *
     * @param templatePath
     *          The template path and name of the velocity template, not null.
     *
     * @param contextMap
     *          The key/value pairs to add to the velocity context storage, may be null.
     *
     * @return
     *          A pair of subject/text retrieved by merging the template with a context.
     *
     * @see #mergeTemplate(String, Map)
     */
    public Pair<String, String> getSubjectAndText(@NotNull String templatePath, Map<String, Object> contextMap) {
        var unparsed = mergeTemplate(templatePath, contextMap);
        var subject = getSubStringBetweenDelimiters(unparsed, SUBJECT_DELIMITER, TEXT_DELIMITER);
        var text = getSubStringBetweenDelimiters(unparsed, TEXT_DELIMITER, null);

        return new Pair<>(subject, text);
    }

    /**
     *
     * @param origin
     *          The string to get the sub string from, not null.
     *
     * @param firstDelimiter
     *          The first delimiter, not null.
     *
     * @param secondDelimiter
     *          The second delimiter, may be null.
     *
     * @return
     *          The substring between the last occurrence of the {@code firstDelimiter} and the
     *          first occurrence of the {@code secondDelimiter} with all leading/trailing whitespace characters removed
     *          in case the {@code firstDelimiter} <b>is not</b> {@code null},
     *          or the substring between the last occurrence of the {@code firstDelimiter} and the end
     *          of the {@code origin} string with all leading/trailing whitespace characters removed, or {@code null}
     *          when the {@code firstDelimiter} or {@code secondDelimiter} is not found.
     *
     */
    String getSubStringBetweenDelimiters(String origin, String firstDelimiter, String secondDelimiter) {
        var allOccurrencesOfFirst = origin.split(firstDelimiter);
        if(allOccurrencesOfFirst.length < 2) {
            return null;
        }

        var followingLastOccurrenceOfFirst = allOccurrencesOfFirst[allOccurrencesOfFirst.length - 1];
        if(Objects.isNull(secondDelimiter)) {
            return followingLastOccurrenceOfFirst.trim();
        }

        var allOccurrencesOfSecondInFollowing = followingLastOccurrenceOfFirst.split(secondDelimiter);
        if(allOccurrencesOfSecondInFollowing.length < 2) {
            return null;
        }

        return allOccurrencesOfSecondInFollowing[0].trim();
    }

}
