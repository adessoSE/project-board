package de.adesso.projectboard.base.user.persistence;

import de.adesso.projectboard.base.project.persistence.Project;
import de.adesso.projectboard.base.user.service.UserServiceImpl;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * {@link JpaRepository} to persist {@link User} entities.
 *
 * @see UserServiceImpl
 */
public interface UserRepository extends JpaRepository<User, String> {

    List<User> findAllByCreatedProjectsContaining(Project project);

    List<User> findAllByBookmarksContaining(Project project);

    boolean existsByIdAndBookmarksContaining(String userId, Project project);

    boolean existsByIdAndCreatedProjectsContaining(String userId, Project project);

}
