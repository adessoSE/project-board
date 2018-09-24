package de.adesso.projectboard.core.base.rest.project.persistence;

import org.springframework.data.repository.CrudRepository;

/**
 * {@link CrudRepository} for persisting {@link Project}s.
 */
public interface ProjectRepository extends CrudRepository<Project, String> {
    
}
