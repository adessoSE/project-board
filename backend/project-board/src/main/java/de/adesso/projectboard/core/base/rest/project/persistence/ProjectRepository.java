package de.adesso.projectboard.core.base.rest.project.persistence;

import org.springframework.data.repository.CrudRepository;

public interface ProjectRepository extends CrudRepository<AbstractProject, Long> {
    
}
