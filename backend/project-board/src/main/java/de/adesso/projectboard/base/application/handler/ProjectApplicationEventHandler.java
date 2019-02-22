package de.adesso.projectboard.base.application.handler;

import de.adesso.projectboard.base.application.persistence.ProjectApplication;
import de.adesso.projectboard.base.application.rest.ApplicationController;

/**
 * Interface used by {@link ApplicationController} to handle
 * incoming project applications in a customizable manner.
 *
 * @see de.adesso.projectboard.rest.handler.application.ProjectBoardApplicationEventHandler
 */
@FunctionalInterface
public interface ProjectApplicationEventHandler {

    /**
     *
     * @param application
     *          The {@link ProjectApplication} instance with the info sent
     *          by the user <b>after</b> it has been persisted in the database.
     *
     */
    void onApplicationReceived(ProjectApplication application);

}
