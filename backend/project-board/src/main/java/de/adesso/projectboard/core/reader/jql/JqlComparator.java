package de.adesso.projectboard.core.reader.jql;

/**
 * Comparator to create JQL statements.
 *
 * @see JqlQueryStringBuilder
 */
public enum JqlComparator {

    NOT_EQUAL("!="),
    LESS("<"),
    LESS_OR_EQUAL("<="),
    EQUAL("="),
    GREATER_OR_EQUAL(">="),
    GREATER(">"),
    IN("IN");

    private final String stringValue;

    JqlComparator(String stringValue) {
        this.stringValue = stringValue;
    }

    @Override
    public String toString() {
        return stringValue;
    }

}
