package de.adesso.projectboard.core.base.rest.user.application.persistence;

import de.adesso.projectboard.core.base.rest.project.persistence.Project;
import de.adesso.projectboard.core.base.rest.user.persistence.User;
import de.adesso.projectboard.core.base.rest.user.service.ApplicationService;
import org.springframework.data.repository.CrudRepository;

/**
 * {@link CrudRepository} to persist {@link ProjectApplication} objects.
 *
 * @see ApplicationService
 */
public interface ProjectApplicationRepository extends CrudRepository<ProjectApplication, Long> {

    boolean existsByUserAndProject(User user, Project project);

}
