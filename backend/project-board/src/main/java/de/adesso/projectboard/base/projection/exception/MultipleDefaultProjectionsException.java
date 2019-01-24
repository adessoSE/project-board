package de.adesso.projectboard.base.projection.exception;

import de.adesso.projectboard.base.projection.ProjectionTarget;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class MultipleDefaultProjectionsException extends Exception {

    private static final String MESSAGE_TEMPLATE = "Multiple interfaces annotated with @NamedInterface " +
            "are marked as the default projections for target '%s'!";

    private final ProjectionTarget target;

    public MultipleDefaultProjectionsException(@NonNull ProjectionTarget target) {
        super(String.format(MESSAGE_TEMPLATE, target.toString()));

        this.target = target;
    }

}
