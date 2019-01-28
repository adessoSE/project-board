package de.adesso.projectboard.base.projection.exception;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

@Getter
@EqualsAndHashCode(callSuper = false)
public class MultipleSimilarlyNamedProjectionsException extends Exception {

    private static final String MESSAGE_TEMPLATE = "Multiple interfaces annotated with @NamedInterface " +
            "have the same name ('%s') for the target class '%s'!";

    private final String name;

    private final Class<?> targetClass;

    public MultipleSimilarlyNamedProjectionsException(@NonNull String name, @NonNull Class<?> targetClass) {
        super(String.format(MESSAGE_TEMPLATE, name, targetClass.getName()));

        this.name = name;
        this.targetClass = targetClass;
    }

}
