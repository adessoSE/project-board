package de.adesso.projectboard.base.updater.persistence;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

/**
 * The corresponding {@link CrudRepository} to persist {@link ProjectUpdateJob} in a database.
 *
 * @see ProjectUpdateJob
 */
public interface ProjectUpdateJobRepository extends CrudRepository<ProjectUpdateJob, Long> {

    Optional<ProjectUpdateJob> findFirstByStatusOrderByTimeDesc(ProjectUpdateJob.Status status);

    @Query("SELECT p FROM ProjectUpdateJob AS p WHERE p.time = (SELECT MAX(p.time) FROM ProjectUpdateJob AS p)")
    Optional<ProjectUpdateJob> findLatest();

    long countByStatus(ProjectUpdateJob.Status status);

}
