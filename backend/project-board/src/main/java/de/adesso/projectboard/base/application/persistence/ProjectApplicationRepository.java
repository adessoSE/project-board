package de.adesso.projectboard.base.application.persistence;

import de.adesso.projectboard.base.project.persistence.Project;
import de.adesso.projectboard.base.user.persistence.User;
import de.adesso.projectboard.service.ApplicationServiceImpl;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * {@link CrudRepository} to persist {@link ProjectApplication} objects.
 *
 * @see ApplicationServiceImpl
 */
public interface ProjectApplicationRepository extends CrudRepository<ProjectApplication, Long> {

    List<ProjectApplication> findAllByProjectEquals(Project project);

    boolean existsByUserAndProject(User user, Project project);

}
