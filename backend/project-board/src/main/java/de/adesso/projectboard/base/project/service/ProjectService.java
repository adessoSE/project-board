package de.adesso.projectboard.base.project.service;

import de.adesso.projectboard.base.exceptions.ProjectNotFoundException;
import de.adesso.projectboard.base.project.persistence.Project;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service interface to provide functionality to manage {@link Project}s.
 *
 * @see de.adesso.projectboard.base.user.service.UserService
 * @see de.adesso.projectboard.base.user.service.UserProjectService
 */
public interface ProjectService {

    /**
     * @param projectId
     *          The {@link Project#getId()}  ID} of the {@link Project}.
     *
     * @return
     *          The {@link Project} with the given {@code projectId}.
     *
     * @throws ProjectNotFoundException
     *          When no {@link Project} with the given {@code projectId}
     *          was found.
     *
     * @see #projectExists(String)
     */
    Project getProjectById(String projectId) throws ProjectNotFoundException;

    /**
     * @param projectId
     *          The {@link Project#getId()}  ID} of the {@link Project}.
     *
     * @return
     *          {@code true}, iff a {@link Project} with the given {@code projectId}
     *          was found.
     */
    boolean projectExists(String projectId);

    /**
     * @param project
     *          The {@link Project} to create a new {@link Project}
     *          from.
     *
     * @return
     *          The created {@link Project}.
     */
    Project createProject(Project project);

    /**
     *
     * @param project
     *          The {@link Project} to save.
     *
     * @return
     *          The saved {@code project}.
     *
     * @see #saveAll(List)
     */
    Project save(Project project);

    /**
     *
     * @param projects
     *          The list of {@link Project}s to save.
     *
     * @return
     *          A list of the saved {@code project}s.
     *
     * @see #save(Project)
     */
    default List<Project> saveAll(List<Project> projects) {
        return projects.stream()
            .map(this::save)
            .collect(Collectors.toList());
    }

}
