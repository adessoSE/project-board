package de.adesso.projectboard.core.base.rest.user.application;

import de.adesso.projectboard.core.base.rest.user.application.persistence.ProjectApplication;

/**
 * Interface used by {@link de.adesso.projectboard.core.base.rest.user.UserService} to handle incoming project applications in
 * a customizable manner.
 *
 * @see de.adesso.projectboard.core.rest.application.JiraProjectApplicationHandler
 */
@FunctionalInterface
public interface ProjectApplicationHandler {

    /**
     *
     * @param application
     *          The {@link ProjectApplication} instance with the info sent
     *          by the user <b>before</b> it is persisted in the database.
     *
     * @throws RuntimeException
     *          When a error occurs.
     *
     *          <p>
     *              <b>Note:</b> The {@code application} is not persisted
     *              when a exception is thrown.
     *          </p>
     */
    void onApplicationReceived(ProjectApplication application) throws RuntimeException;

}
