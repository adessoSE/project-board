package de.adesso.projectboard.core.base.rest.user.application;

import de.adesso.projectboard.core.base.rest.user.application.persistence.ProjectApplication;

/**
 * Interface used by {@link de.adesso.projectboard.core.base.rest.user.ApplicationController} to handle
 * incoming project applications in a customizable manner.
 *
 * @see de.adesso.projectboard.core.rest.handler.application.ProjectBoardApplicationHandler
 */
@FunctionalInterface
public interface ProjectApplicationHandler {

    /**
     *
     * @param application
     *          The {@link ProjectApplication} instance with the info sent
     *          by the user <b>after</b> it has been persisted in the database.
     *
     */
    void onApplicationReceived(ProjectApplication application) throws RuntimeException;

}
