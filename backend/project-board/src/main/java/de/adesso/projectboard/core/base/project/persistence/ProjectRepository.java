package de.adesso.projectboard.core.base.project.persistence;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

public interface ProjectRepository extends CrudRepository<AbstractProject, Long>, JpaSpecificationExecutor<AbstractProject> {
    
}
