package de.adesso.projectboard.base.exceptions;

import java.util.Objects;

public class HierarchyNotFoundException extends NoSuchEntityException {

    /**
     *
     * @param userId
     *          The ID of the user the hierarchy instance was
     *          not for, not null.
     */
    public HierarchyNotFoundException(String userId) {
        super(String.format("Hierarchy for User with ID '%s' not found!", Objects.requireNonNull(userId)));
    }

}
