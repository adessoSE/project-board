package de.adesso.projectboard.base.project.service;

import de.adesso.projectboard.base.application.persistence.ProjectApplication;
import de.adesso.projectboard.base.exceptions.ProjectNotFoundException;
import de.adesso.projectboard.base.project.dto.ProjectRequestDTO;
import de.adesso.projectboard.base.project.persistence.Project;
import de.adesso.projectboard.base.user.persistence.User;

import java.util.Objects;

/**
 * Service interface to provide functionality to manage {@link Project}s.
 *
 * @see de.adesso.projectboard.base.user.service.UserService
 * @see de.adesso.projectboard.base.user.service.UserProjectService
 */
public interface ProjectService {

    /**
     * @param projectId
     *          The {@link Project#id ID} of the {@link Project}.
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
     *          The {@link Project#id ID} of the {@link Project}.
     *
     * @return
     *          {@code true}, iff a {@link Project} with the given {@code projectId}
     *          was found.
     */
    boolean projectExists(String projectId);

    /**
     * @param projectDTO
     *          The {@link ProjectRequestDTO} instance to create a {@link Project}
     *          from.
     * @return
     *          The created {@link Project}.
     */
    Project createProject(ProjectRequestDTO projectDTO);

    /**
     * @param projectDTO
     *          The {@link ProjectRequestDTO} instance.
     *
     * @param projectId
     *          The {@link Project#id ID} of the {@link Project}
     *          to update.
     *
     * @return
     *          The updated {@link Project}.
     *
     * @throws ProjectNotFoundException
     *          When no {@link Project} with the given {@code projectId}
     *          was found.
     */
    Project updateProject(ProjectRequestDTO projectDTO, String projectId);

    /**
     * @param project
     *          The {@link Project} to delete.
     *
     * @see #deleteProjectById(String)
     */
    void deleteProject(Project project);

    /**
     * Deletes a {@link Project} by it's {@link Project#id ID}. Also removes
     * it from the {@link User#ownedProjects owned projects} and {@link User#bookmarks bookmarks}
     * of all {@link User}s and also removes all {@link ProjectApplication}s referring to the
     * project.
     *
     * @param projectId
     *          The {@link Project#id ID} of the {@link Project}
     *          to delete.
     *
     * @throws ProjectNotFoundException
     *          When no {@link Project} with the given {@code projectId}
     *          was found.
     */
    void deleteProjectById(String projectId) throws ProjectNotFoundException;

    /**
     * Method to validate the existence of a given {@link Project}
     * instance.
     *
     * @param project
     *          The {@link Project} to validate.
     *
     * @return
     *          The given {@code project}.
     *
     * @throws ProjectNotFoundException
     *          When the no {@link Project} with the given {@code project}'s
     *          {@link Project#id ID} exists.
     *
     */
    default Project validateExistence(Project project) throws ProjectNotFoundException {
        Project givenProject = Objects.requireNonNull(project);

        if(projectExists(givenProject.getId())) {
            return givenProject;
        } else {
            throw new ProjectNotFoundException();
        }
    }

}
