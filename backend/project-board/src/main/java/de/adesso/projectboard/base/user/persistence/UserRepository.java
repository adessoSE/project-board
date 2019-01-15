package de.adesso.projectboard.base.user.persistence;

import de.adesso.projectboard.ad.user.RepositoryUserService;
import de.adesso.projectboard.base.project.persistence.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * {@link JpaRepository} to persist {@link User} entities.
 *
 * @see RepositoryUserService
 */
public interface UserRepository extends JpaRepository<User, String> {

    List<User> findAllByOwnedProjectsContaining(Project project);

    List<User> findAllByBookmarksContaining(Project project);

    boolean existsByIdAndBookmarksContaining(String userId, Project project);

    boolean existsByIdAndOwnedProjectsContaining(String userId, Project project);

}
