package de.adesso.projectboard.core.base.rest.scanner;

import org.springframework.boot.diagnostics.AbstractFailureAnalyzer;
import org.springframework.boot.diagnostics.FailureAnalysis;

// TODO: where to put string resources?
public class ProjectScannerFailureAnalyzer extends AbstractFailureAnalyzer<FieldsWithSameQueryNameException> {

    @Override
    protected FailureAnalysis analyze(Throwable rootFailure, FieldsWithSameQueryNameException cause) {
        return new FailureAnalysis(getDescription(cause), getAction(), cause);
    }

    private String getDescription(FieldsWithSameQueryNameException cause) {
        return String.format("Multiple fields have the same name in the search query! (Conflicting fields: \"%s\" and \"%s\")",
                cause.getFirstFieldName(),
                cause.getSecondFieldName());
    }

    private String getAction() {
        return "Consider changing the value(s) of the @QueryName annotation(s) to have no naming conflict with each other!";
    }

}
