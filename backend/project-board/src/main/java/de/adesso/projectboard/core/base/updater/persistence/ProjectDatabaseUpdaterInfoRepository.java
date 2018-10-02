package de.adesso.projectboard.core.base.updater.persistence;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

/**
 * The corresponding {@link CrudRepository} to persist {@link UpdateJob} in a database.
 *
 * @see UpdateJob
 */
public interface ProjectDatabaseUpdaterInfoRepository extends CrudRepository<UpdateJob, Long> {

    Optional<UpdateJob> findFirstByStatusOrderByTimeDesc(UpdateJob.Status status);

    @Query("SELECT p FROM UpdateJob AS p WHERE p.time = (SELECT MAX(p.time) FROM UpdateJob AS p)")
    Optional<UpdateJob> findLatest();

    long countByStatus(UpdateJob.Status status);

}
