package de.adesso.projectboard.base.project.service;

import de.adesso.projectboard.base.exceptions.ProjectNotFoundException;
import de.adesso.projectboard.base.project.persistence.Project;
import de.adesso.projectboard.base.util.Sorting;

import java.util.List;

/**
 *
 */
public interface ProjectService {

    Project getProjectById(String projectId) throws ProjectNotFoundException;

    boolean projectExists(String projectId);

    List<Project> getProjectsForUser(String userId, List<Sorting> sortings);


}
