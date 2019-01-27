package de.adesso.projectboard.base.projection.exception;

import de.adesso.projectboard.base.projection.ProjectionTarget;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

@Getter
@EqualsAndHashCode(callSuper = false)
public class MultipleSimilarlyNamedProjectionsException extends Exception {

    private static final String MESSAGE_TEMPLATE = "Multiple interfaces annotated with @NamedInterface " +
            "have the same name ('%s') for the target '%s'!";

    private final String name;

    private final ProjectionTarget target;

    public MultipleSimilarlyNamedProjectionsException(@NonNull String name, @NonNull ProjectionTarget target) {
        super(String.format(MESSAGE_TEMPLATE, name, target.toString()));

        this.name = name;
        this.target = target;
    }

}
