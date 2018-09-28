package de.adesso.projectboard.core.base.rest.user.persistence;

import de.adesso.projectboard.core.base.rest.project.persistence.Project;
import org.springframework.data.repository.CrudRepository;

/**
 * {@link CrudRepository} to persist {@link User} entities.
 *
 * @see UserService
 */
public interface UserRepository extends CrudRepository<User, String> {

    boolean existsById(String userId);

    boolean existsByIdAndBookmarksContaining(String userId, Project project);

}
