package de.adesso.projectboard.core.base.project;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface AbstractProjectRepository extends CrudRepository<AbstractProject, Long> {
    
}
