package de.adesso.projectboard.base.application.handler;

import de.adesso.projectboard.base.application.persistence.ProjectApplication;
import de.adesso.projectboard.base.user.persistence.User;

@FunctionalInterface
public interface ProjectApplicationOfferedEventHandler {

    /**
     * @param offeringUser
     *          The user that offered the application, not {@code null}.
     *
     * @param projectApplication
     *          The application that was offered, not {@code null}.
     */
    void onApplicationOffered(User offeringUser, ProjectApplication projectApplication);

}
