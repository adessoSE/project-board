package de.adesso.projectboard.base.user.service;

import de.adesso.projectboard.base.exceptions.ProjectNotFoundException;
import de.adesso.projectboard.base.exceptions.UserNotFoundException;
import de.adesso.projectboard.base.project.dto.ProjectRequestDTO;
import de.adesso.projectboard.base.project.persistence.Project;
import de.adesso.projectboard.base.user.persistence.User;
import de.adesso.projectboard.base.util.Sorting;

import java.util.List;

/**
 * Service interface to manage {@link Project}s for {@link User}s.
 *
 * @see UserService
 */
public interface UserProjectService {

    /**
     *
     * @param userId
     *          The {@link User#id ID} of the {@link User} to get the
     *          {@link Project}s for.
     *
     * @param sorting
     *          The {@link Sorting} to apply.
     *
     * @return
     *          A {@link List} of {@link Project}s sorted accordingly.
     *
     * @throws UserNotFoundException
     *          When no {@link User} with the given {@code userId} was found.
     */
    List<Project> getProjectsForUser(String userId, Sorting sorting) throws UserNotFoundException;

    /**
     *
     * @param userId
     *          The {@link User#id ID} of the {@link User} to get the
     *          {@link Project}s for.
     *
     * @param keyword
     *          The keyword to search for.
     *
     * @param sorting
     *          The {@link Sorting} to apply.
     *
     * @return
     *          A {@link List} of {@link Project}s sorted accordingly.
     *
     * @throws UserNotFoundException
     *          When no {@link User} with the given {@code userId} was found.
     */
    List<Project> searchProjectsForUser(String userId, String keyword, Sorting sorting) throws UserNotFoundException;

    /**
     *
     * @param userId
     *          The {@link User#id ID} of the {@link User}.
     *
     * @param projectId
     *          The {@link Project#id ID} of the {@link Project}.
     *
     * @return
     *          {@code true}, iff the user with the given {@code userId} has
     *          created the project with the given {@code projectId}.
     *
     * @throws UserNotFoundException
     *          When no {@link User} with the given {@code userId} was found.
     *
     * @throws ProjectNotFoundException
     *          When no {@link Project} with the given {@code projectId} was found.
     */
    boolean userHasCreatedProject(String userId, String projectId) throws UserNotFoundException, ProjectNotFoundException;

    /**
     * Creates a {@link Project} and adds it to the {@link User}'s
     * {@link User#ownedProjects owned projects}.
     *
     * @param projectDTO
     *          The {@link ProjectRequestDTO} to create the {@link Project}
     *          from.
     *
     * @param userId
     *          The {@link User#id ID} of the {@link User} to create the {@link Project}
     *          for.
     *
     * @return
     *          The created {@link Project}.
     *
     * @throws UserNotFoundException
     *          When no {@link User} with the given {@code userId} was found.
     */
    Project createProjectForUser(ProjectRequestDTO projectDTO, String userId) throws UserNotFoundException;

    /**
     * Adds a {@link Project} to the the {@link User}'s
     * {@link User#ownedProjects owned projects}.
     *
     * @param userId
     *          The {@link User#id ID} of the {@link User} to add the {@link Project}
     *          to.
     *
     * @param projectId
     *          The {@link Project#id ID} of the {@link Project}.
     *
     * @return
     *          The added {@link Project}.
     *
     * @throws UserNotFoundException
     *          When no {@link User} with the given {@code userId} was found.
     *
     * @throws ProjectNotFoundException
     *          When no {@link Project} with the given {@code projectId} was found.
     *
     * @see #createProjectForUser(ProjectRequestDTO, String)
     */
    Project addProjectToUser(String userId, String projectId) throws UserNotFoundException, ProjectNotFoundException;

}
