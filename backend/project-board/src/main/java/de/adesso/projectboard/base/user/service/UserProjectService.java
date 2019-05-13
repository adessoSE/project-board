package de.adesso.projectboard.base.user.service;

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
     * @param query
     *          The keyword to search for.
     *
     * @param sort
     *          The {@link Sort} to apply.
     *
     * @return
     *          A {@link List} of {@link Project}s sorted accordingly.
     */
    List<Project> searchProjectsForUser(User user, String query, Sort sort);

}
