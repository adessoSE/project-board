package de.adesso.projectboard.base.user.service;

import de.adesso.projectboard.base.project.persistence.Project;
import de.adesso.projectboard.base.user.persistence.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

/**
 * {@link UserProjectService} extension to support pagination for {@link Project}s. Uses
 * the Spring Data classes {@link Sort} and {@link PageRequest} to handle pagination and
 * sorting.
 *
 * @see UserProjectService
 * @see PageRequest
 * @see Sort
 */
public interface PageableUserProjectService extends UserProjectService {

    /**
     *
     * @param user
     *          The {@link User} to get the {@link Project}s for.
     *
     * @param sort
     *          The {@link Sort} to apply.
     *
     * @param pageRequest
     *          The {@link PageRequest} to pass pagination information.
     *
     * @return
     *          A {@link Page} of {@link Project}s.
     */
    Page<Project> getProjectsForUserPaginated(User user, Sort sort, PageRequest pageRequest);

    /**
     *
     * @param keyword
     *          The keyword to search for.
     *
     * @param user
     *          The {@link User} to search the {@link Project}s for.
     *
     * @param sort
     *          The {@link Sort} to apply.
     *
     * @param pageRequest
     *          The {@link PageRequest} to pass pagination information.
     *
     * @return
     *          A {@link Page} of {@link Project}s.
     */
    Page<Project> searchProjectsForUser(String keyword, User user, Sort sort, PageRequest pageRequest);

}
