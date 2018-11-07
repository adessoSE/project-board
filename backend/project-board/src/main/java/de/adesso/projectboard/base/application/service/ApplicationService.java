package de.adesso.projectboard.base.application.service;

import de.adesso.projectboard.base.application.dto.ProjectApplicationRequestDTO;
import de.adesso.projectboard.base.application.persistence.ProjectApplication;
import de.adesso.projectboard.base.exceptions.ProjectNotFoundException;
import de.adesso.projectboard.base.exceptions.UserNotFoundException;
import de.adesso.projectboard.base.project.persistence.Project;
import de.adesso.projectboard.base.user.persistence.User;

import java.util.List;

/**
 * Service interface to provide functionality to manage {@link ProjectApplication}s.
 */
public interface ApplicationService {

    /**
     *
     * @param userId
     *          The {@link User#id ID} of the {@link User}.
     *
     * @param projectId
     *          The {@link Project#id ID} of the {@link Project}.
     *
     * @return
     *          {@code true}, iff the user's {@link User#applications applications}
     *          contain an application that refers to the project.
     *
     * @throws UserNotFoundException
     *          When no {@link User} with the given {@code userId} was found.
     *
     * @throws ProjectNotFoundException
     *          When no {@link Project} with the given {@code projectId} was found.
     */
    boolean userHasAppliedForProject(String userId, String projectId) throws UserNotFoundException, ProjectNotFoundException;

    /**
     * Creates a new {@link ProjectApplication} and add it
     * to the user's {@link User#applications applications}.
     *
     * @param applicationDTO
     *          The {@link ProjectApplicationRequestDTO} instance.
     *
     * @param userId
     *          The {@link User#id ID} of the {@link User} to create an
     *          application for.
     *
     * @return
     *          The created {@link ProjectApplication}.
     *
     * @throws UserNotFoundException
     *          When no {@link User} with the given {@code userId} was found.
     *
     * @throws ProjectNotFoundException
     *          When no {@link Project} with the given {@code projectId} was found.
     */
    ProjectApplication createApplicationForUser(ProjectApplicationRequestDTO applicationDTO, String userId) throws UserNotFoundException, ProjectNotFoundException;

    /**
     *
     * @param userId
     *          The {@link User#id ID} of the {@link User} to get the
     *          applications of.
     *
     * @return
     *          The user's applications.
     *
     * @throws UserNotFoundException
     *          When no {@link User} with the given {@code userId} was found.
     */
    List<ProjectApplication> getApplicationsOfUser(String userId) throws UserNotFoundException;

}
