package de.adesso.projectboard.base.application.persistence;

import de.adesso.projectboard.base.project.persistence.Project;
import de.adesso.projectboard.base.user.persistence.User;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

/**
 * {@link CrudRepository} to persist {@link ProjectApplication} objects.
 */
public interface ProjectApplicationRepository extends CrudRepository<ProjectApplication, Long> {

    List<ProjectApplication> findAllByProjectEquals(Project project);

    List<ProjectApplication> findAllByUserIn(Iterable<User> users, Sort sort);

    List<ProjectApplication> findAllByUser(User user, Sort sort);

    Optional<ProjectApplication> findByUserAndId(User user, long id);

    boolean existsByUserAndProject(User user, Project project);

}
