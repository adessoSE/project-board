package de.adesso.projectboard.reader.jql;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Builder to build JQL queries.
 *
 * @author Daniel Meier
 */
public class JqlQueryStringBuilder {

    private static final String JQL_DATE_PATTERN = "yyyy-MM-dd HH:mm";

    private final DateTimeFormatter dateTimeFormatter;

    private StringBuilder queryStringBuilder;

    /**
     * Constructs a new, empty {@link JqlQueryStringBuilder}.
     */
    public JqlQueryStringBuilder() {
        this.queryStringBuilder = new StringBuilder();

        this.dateTimeFormatter = DateTimeFormatter.ofPattern(JQL_DATE_PATTERN);
    }

    /**
     * Appends a new <i>key - value</i> pair to the query
     * and links it with a logical <i>AND</i>.
     *
     * <br>
     *      <br> before appending: <i>preceeding query</i>
     *      <br> after appending: <i>preceeding query</i> <b>AND key <i>comporator</i> "value"</b>
     *
     * <p>
     *     <b>Note</b>: to produce a valid JQL query you need
     *     to call {@link #newQuery(String, JqlComparator, String)} /
     *     {@link #newQuery(String, JqlComparator, LocalDateTime)} first when
     *     starting a new query!
     * </p>
     *
     *
     * @param key
     *          The key of the pair.
     *
     * @param comparator
     *          The {@link JqlComparator}.
     *
     * @param value
     *          The value of the pair.
     *
     * @return
     *          {@literal this}
     */
    public JqlQueryStringBuilder and(String key, JqlComparator comparator, String value) {
        if(key == null || value == null || comparator == null) {
            return this;
        }

        return append(JqlAppender.AND, key, comparator, value);
    }

    /**
     * Appends a new <i>key - value</i> pair to the query
     * and links it with a logical <i>AND</i>.
     *
     * <br>
     *      <br> before appending: <i>preceeding query</i>
     *      <br> after appending: <i>preceeding query</i> <b>AND key <i>comporator</i> "{@value #JQL_DATE_PATTERN}"</b>
     *
     * <p>
     *     <b>Note</b>: to produce a valid JQL query you need
     *     to call {@link #newQuery(String, JqlComparator, String)} /
     *     {@link #newQuery(String, JqlComparator, LocalDateTime)} first when
     *     starting a new query!
     * </p>
     *
     *
     * @param key
     *          The key of the pair.
     *
     * @param comparator
     *          The {@link JqlComparator}.
     *
     * @param dateTime
     *          The value of the pair. Will be formatted in a JQL compliant
     *          format.
     *
     * @return
     *          {@literal this}
     */
    public JqlQueryStringBuilder and(String key, JqlComparator comparator, LocalDateTime dateTime) {
        if(key == null || dateTime == null || comparator == null) {
            return this;
        }

        return append(JqlAppender.AND, key, comparator, formatDate(dateTime));
    }

    /**
     * Appends a sub query to the current query and links it with
     * a logical <i>AND</i>. <b>The appended sub query is bracketed</b>.
     *
     * <br>
     *      <br> before appending: <i>preceeding query</i>
     *      <br> after appending: <i>preceeding query</i> <b>AND ( subquery )</b>
     *
     * <p>
     *     <b>Note</b>: to produce a valid JQL query you need
     *     to call {@link #newQuery(String, JqlComparator, String)} /
     *     {@link #newQuery(String, JqlComparator, LocalDateTime)} first when
     *     starting a new query!
     * </p>
     *
     * @param subQuery
     *          The sub query to append
     *
     * @return
     *          {@literal this}
     */
    public JqlQueryStringBuilder and(String subQuery) {
        if(subQuery != null) {
            return appendSubQuery(JqlAppender.AND, subQuery);
        }

        return this;
    }

    /**
     * Appends a new <i>key - value</i> pair to the query
     * and links it with a logical <i>OR</i>.
     *
     * <br>
     *      <br> before appending: <i>preceeding query</i>
     *      <br> after appending: <i>preceeding query</i> <b>OR key <i>comporator</i> "value"</b>
     *
     * <p>
     *     <b>Note</b>: to produce a valid JQL query you need
     *     to call {@link #newQuery(String, JqlComparator, String)} /
     *     {@link #newQuery(String, JqlComparator, LocalDateTime)} first when
     *     starting a new query!
     * </p>
     *
     *
     * @param key
     *          The key of the pair.
     *
     * @param comparator
     *          The {@link JqlComparator}.
     *
     * @param value
     *          The value of the pair.
     *
     * @return
     *          {@literal this}
     */
    public JqlQueryStringBuilder or(String key, JqlComparator comparator, String value) {
        if(key == null || value == null || comparator == null) {
            return this;
        }

        return append(JqlAppender.OR, key, comparator, value);
    }

    /**
     * Appends a new <i>key - value</i> pair to the query
     * and links it with a logical <i>OR</i>.
     *
     * <br>
     *      <br> before appending: <i>preceeding query</i>
     *      <br> after appending: <i>preceeding query</i> <b>OR key <i>comporator</i> "{@value #JQL_DATE_PATTERN}"</b>
     *
     * <p>
     *     <b>Note</b>: to produce a valid JQL query you need
     *     to call {@link #newQuery(String, JqlComparator, String)} /
     *     {@link #newQuery(String, JqlComparator, LocalDateTime)} first when
     *     starting a new query!
     * </p>
     *
     *
     * @param key
     *          The key of the pair.
     *
     * @param comparator
     *          The {@link JqlComparator}.
     *
     * @param dateTime
     *          The value of the pair. Will be formatted in a JQL compliant
     *          format.
     *
     * @return
     *          {@literal this}
     */
    public JqlQueryStringBuilder or(String key, JqlComparator comparator, LocalDateTime dateTime) {
        if(key == null || dateTime == null || comparator == null) {
            return this;
        }

        return append(JqlAppender.OR, key, comparator, formatDate(dateTime));
    }

    /**
     * Appends a sub query to the current query and links it with
     * a logical <i>OR</i>. <b>The appended sub query is bracketed</b>.
     *
     * <br>
     *      <br> before appending: <i>preceeding query</i>
     *      <br> after appending: <i>preceeding query</i> <b>OR ( subquery )</b>
     *
     * <p>
     *     <b>Note</b>: to produce a valid JQL query you need
     *     to call {@link #newQuery(String, JqlComparator, String)} /
     *     {@link #newQuery(String, JqlComparator, LocalDateTime)} first when
     *     starting a new query!
     * </p>
     *
     * @param subQuery
     *          The sub query to append
     *
     * @return
     *          {@literal this}
     */
    public JqlQueryStringBuilder or(String subQuery) {
        if(subQuery != null) {
            return appendSubQuery(JqlAppender.OR, subQuery);
        }

        return this;
    }

    /**
     * Starts a new query by clearing the old query and appends
     * a new <i>key - value</i> pair.
     *
     * @param key
     *          The key of the pair.
     *
     * @param comparator
     *          The {@link JqlComparator}.
     *
     * @param value
     *          The value of the pair.
     *
     * @return
     *          {@literal this}.
     */
    public JqlQueryStringBuilder newQuery(String key, JqlComparator comparator, String value) {
        if(key == null || comparator == null || value == null) {
            return this;
        }

        return appendNew(key, comparator, value);
    }

    /**
     * Starts a new query by clearing the old query and appends
     * a new <i>key - value</i> pair.
     *
     * @param key
     *          The key of the pair.
     *
     * @param comparator
     *          The {@link JqlComparator}.
     *
     * @param dateTime
     *          The value of the pair. Will be formatted in a JQL compliant
     *          format.
     *
     * @return
     *          {@literal this}.
     */
    public JqlQueryStringBuilder newQuery(String key, JqlComparator comparator, LocalDateTime dateTime) {
        if(key == null || dateTime == null || comparator == null) {
            return this;
        }

        return appendNew(key, comparator, formatDate(dateTime));
    }

    /**
     *
     * @return
     *          The complete JQL query string.
     */
    public String build() {
        return queryStringBuilder.toString();
    }

    private JqlQueryStringBuilder appendNew(String key, JqlComparator comparator, String value) {
        this.queryStringBuilder = new StringBuilder();

        queryStringBuilder
                .append(' ')
                .append(key)
                .append(' ')
                .append(comparator.toString())
                .append(' ')
                .append("\"")
                .append(value)
                .append("\"");

        return this;
    }

    private JqlQueryStringBuilder append(JqlAppender appender, String key, JqlComparator comparator, String value) {
        queryStringBuilder
                .append(' ')
                .append(appender.toString())
                .append(' ')
                .append(key)
                .append(' ')
                .append(comparator.toString())
                .append(" \"")
                .append(value)
                .append("\"");

        return this;
    }

    private JqlQueryStringBuilder appendSubQuery(JqlAppender appender, String subQuery) {
        queryStringBuilder
                .append(' ')
                .append(appender.toString())
                .append(" (")
                .append(subQuery)
                .append(" )");

        return this;
    }

    private String formatDate(LocalDateTime dateTime) {
        return dateTime.format(dateTimeFormatter);
    }

}
