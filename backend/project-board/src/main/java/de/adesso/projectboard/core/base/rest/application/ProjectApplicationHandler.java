package de.adesso.projectboard.core.base.rest.application;

import de.adesso.projectboard.core.base.rest.application.persistence.ProjectApplicationLog;

@FunctionalInterface
public interface ProjectApplicationHandler {

    ProjectApplicationLog onApplicationReceived(ProjectApplication application);

}
