package de.adesso.projectboard.adapter.jira.reader.jql;

/**
 * The logical connections to connect queries.
 *
 * @see JqlQueryStringBuilder
 */
public enum JqlAppender {

    AND("AND"),
    OR("OR");

    private final String stringValue;

    JqlAppender(String stringValue) {
        this.stringValue = stringValue;
    }

    @Override
    public String toString() {
        return stringValue;
    }

}
