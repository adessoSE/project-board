package de.adesso.projectboard.core.base.rest.project.persistence;

import org.springframework.data.repository.CrudRepository;

/**
 * {@link CrudRepository} for persisting {@link AbstractProject}s.
 */
public interface ProjectRepository extends CrudRepository<AbstractProject, Long> {
    
}
