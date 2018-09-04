package de.adesso.projectboard.core.base.rest.application;

import de.adesso.projectboard.core.base.rest.application.persistence.ProjectApplication;

/**
 * Interface used by {@link ProjectApplicationController} to handle incoming project applications in
 * a customizable manner.
 *
 * @see de.adesso.projectboard.core.rest.application.JiraProjectApplicationHandler
 */
@FunctionalInterface
public interface ProjectApplicationHandler {

    /**
     *
     * @param applicationDTO
     *          The {@link ProjectApplicationDTO} sent by the user.
     *
     * @return
     *          A {@link ProjectApplication} object.
     */
    ProjectApplication onApplicationReceived(ProjectApplicationDTO applicationDTO);

}
