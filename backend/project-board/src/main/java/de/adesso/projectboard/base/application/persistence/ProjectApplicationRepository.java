package de.adesso.projectboard.base.application.persistence;

import de.adesso.projectboard.base.project.persistence.Project;
import de.adesso.projectboard.base.user.persistence.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * {@link CrudRepository} to persist {@link ProjectApplication} objects.
 */
public interface ProjectApplicationRepository extends CrudRepository<ProjectApplication, Long> {

    List<ProjectApplication> findAllByProjectEquals(Project project);

    boolean existsByUserAndProject(User user, Project project);

}
