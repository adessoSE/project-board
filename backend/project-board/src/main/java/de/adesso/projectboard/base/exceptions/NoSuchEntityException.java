package de.adesso.projectboard.base.exceptions;

import lombok.NoArgsConstructor;

import java.util.Objects;

@NoArgsConstructor
class NoSuchEntityException extends RuntimeException {

    NoSuchEntityException(String entityName, Object entityId) {
        super(String.format("%s with ID '%s' not found!", entityName, Objects.toString(entityId)));
    }

}
