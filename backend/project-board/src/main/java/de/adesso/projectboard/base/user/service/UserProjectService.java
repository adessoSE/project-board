package de.adesso.projectboard.base.user.service;

import de.adesso.projectboard.base.project.dto.ProjectRequestDTO;
import de.adesso.projectboard.base.project.persistence.Project;
import de.adesso.projectboard.base.user.persistence.User;
import org.springframework.data.domain.Sort;

import java.util.List;

/**
 * Service interface to manage {@link Project}s for {@link User}s.
 *
 * @see UserService
 */
public interface UserProjectService {

    /**
     *
     * @param user
     *          The {@link User} to get the {@link Project}s for.
     *
     * @param sort
     *          The {@link Sort} to apply.
     *
     * @return
     *          A {@link List} of {@link Project}s sorted accordingly.
     *
     */
    List<Project> getProjectsForUser(User user, Sort sort);

    /**
     *
     * @param user
     *          The {@link User} to get the {@link Project}s for.
     *
     * @param keyword
     *          The keyword to search for.
     *
     * @param sort
     *          The {@link Sort} to apply.
     *
     * @return
     *          A {@link List} of {@link Project}s sorted accordingly.
     */
    List<Project> searchProjectsForUser(User user, String keyword, Sort sort);

    /**
     *
     * @param user
     *          The {@link User}.
     *
     * @param project
     *          The {@link Project}.
     *
     * @return
     *          {@code true}, iff the given {@code user} owns the
     *          given {@code project}.
     */
    boolean userOwnsProject(User user, Project project);

    /**
     * Creates a {@link Project} and adds it to the {@link User}'s
     * {@link User#ownedProjects owned projects}.
     *
     * @param projectDTO
     *          The {@link ProjectRequestDTO} to create the {@link Project}
     *          from.
     *
     * @param user
     *          The {@link User} to create the {@link Project}
     *          for.
     *
     * @return
     *          The created {@link Project}.
     */
    Project createProjectForUser(ProjectRequestDTO projectDTO, User user);

    /**
     * Adds a existing {@link Project} to the the {@link User}'s
     * {@link User#ownedProjects owned projects}.
     *
     * @param user
     *          The {@link User#id ID} of the {@link User} to add the {@link Project}
     *          to.
     *
     * @param project
     *          The existing {@link Project}.
     *
     * @return
     *          The added {@link Project}.
     *
     * @see #createProjectForUser(ProjectRequestDTO, User)
     */
    Project addProjectToUser(User user, Project project);

}
