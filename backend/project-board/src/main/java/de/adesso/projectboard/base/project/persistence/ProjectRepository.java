package de.adesso.projectboard.base.project.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * {@link JpaRepository} for persisting {@link Project}s.
 */
public interface ProjectRepository extends JpaRepository<Project, String>, JpaSpecificationExecutor<Project> {

    List<Project> findAll(Specification<Project> specification, Sort sort);

    Page<Project> findAll(Specification<Project> specification, Pageable pageable);

}
