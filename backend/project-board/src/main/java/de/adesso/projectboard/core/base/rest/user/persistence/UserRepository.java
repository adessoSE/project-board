package de.adesso.projectboard.core.base.rest.user.persistence;

import de.adesso.projectboard.core.base.rest.project.persistence.Project;
import de.adesso.projectboard.core.base.rest.user.service.UserService;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

/**
 * {@link CrudRepository} to persist {@link User} entities.
 *
 * @see UserService
 */
public interface UserRepository extends CrudRepository<User, String> {

    Optional<User> findByCreatedProjectsContaining(Project project);

    boolean existsById(String userId);

    boolean existsByIdAndBookmarksContaining(String userId, Project project);

    boolean existsByIdAndBoss(String id, SuperUser boss);

    boolean existsByIdAndCreatedProjectsContaining(String userId, Project project);

}
