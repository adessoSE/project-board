package de.adesso.projectboard.core.base.rest;

@FunctionalInterface
public interface ProjectApplicationHandler {

    void onApplicationReceived(ProjectApplication application);

}
