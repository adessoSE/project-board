package de.adesso.projectboard.base.exceptions;

import java.util.Objects;

public class UserDataNotFoundException extends NoSuchEntityException {

    /**
     *
     * @param userId
     *          The ID of the user the data instance was
     *          not found for, not null.
     */
    public UserDataNotFoundException(String userId) {
        super(String.format("Data for User with ID '%s' not found!", Objects.requireNonNull(userId)));
    }

}
