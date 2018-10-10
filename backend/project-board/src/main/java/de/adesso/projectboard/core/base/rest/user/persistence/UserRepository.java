package de.adesso.projectboard.core.base.rest.user.persistence;

import de.adesso.projectboard.core.base.rest.project.persistence.Project;
import de.adesso.projectboard.core.base.rest.user.service.UserService;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * {@link JpaRepository} to persist {@link User} entities.
 *
 * @see UserService
 */
public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByCreatedProjectsContaining(Project project);

    List<User> findAllByBossEquals(SuperUser boss, Sort sort);

    boolean existsById(String userId);

    boolean existsByIdAndBookmarksContaining(String userId, Project project);

    boolean existsByIdAndBoss(String id, SuperUser boss);

    boolean existsByIdAndCreatedProjectsContaining(String userId, Project project);

}
