package de.adesso.projectboard.base.user.service;

import de.adesso.projectboard.base.project.persistence.Project;
import de.adesso.projectboard.base.user.persistence.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

/**
 * {@link UserProjectService} extension to support pagination for {@link Project}s. Uses
 * the Spring Data class {@link PageRequest} to handle pagination and
 * sorting.
 *
 * @see UserProjectService
 * @see PageRequest
 */
public interface PageableUserProjectService extends UserProjectService {

    /**
     *
     * @param user
     *          The {@link User} to get the {@link Project}s for.
     *
     * @param pageable
     *          The {@link Pageable} to pass pagination information.
     *
     * @return
     *          A {@link Page} of {@link Project}s.
     */
    Page<Project> getProjectsForUserPaginated(User user, Pageable pageable);

    /**
     *
     * @param query
     *          The keyword to search for.
     *
     * @param user
     *          The {@link User} to search the {@link Project}s for.
     *
     * @param pageable
     *          The {@link Pageable} to pass pagination information.
     *
     * @return
     *          A {@link Page} of {@link Project}s.
     */
    Page<Project> searchProjectsForUserPaginated(String query, User user, Pageable pageable);

}
