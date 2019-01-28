package de.adesso.projectboard.base.projection.exception;

import lombok.Getter;
import lombok.NonNull;

@Getter
public class MultipleDefaultProjectionsException extends Exception {

    private static final String MESSAGE_TEMPLATE = "Multiple interfaces annotated with @NamedInterface " +
            "are marked as the default projections for target class '%s'!";

    private final Class<?> target;

    public MultipleDefaultProjectionsException(@NonNull Class<?> targetClass) {
        super(String.format(MESSAGE_TEMPLATE, targetClass.getName()));

        this.target = targetClass;
    }

}
