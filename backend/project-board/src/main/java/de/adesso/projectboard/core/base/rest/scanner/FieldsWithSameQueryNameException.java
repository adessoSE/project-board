package de.adesso.projectboard.core.base.rest.scanner;

import lombok.Getter;

@Getter
public class FieldsWithSameQueryNameException extends RuntimeException {

    private final String firstFieldName;

    private final String secondFieldName;

    public FieldsWithSameQueryNameException(String firstFieldName, String secondFieldName) {
        this.firstFieldName = firstFieldName;
        this.secondFieldName = secondFieldName;
    }

}
