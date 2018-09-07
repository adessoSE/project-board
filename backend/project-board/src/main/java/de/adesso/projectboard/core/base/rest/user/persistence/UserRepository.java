package de.adesso.projectboard.core.base.rest.user.persistence;

import de.adesso.projectboard.core.base.rest.project.persistence.AbstractProject;
import org.springframework.data.repository.CrudRepository;

/**
 * {@link CrudRepository} to persist {@link User} entities.
 *
 * @see de.adesso.projectboard.core.base.rest.user.UserService
 */
public interface UserRepository extends CrudRepository<User, String> {

    boolean existsById(String userId);

    boolean existsByIdAndBookmarksContaining(String userId, AbstractProject project);

}
