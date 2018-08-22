package de.adesso.projectboard.core.base.updater.persistence;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

/**
 * The corresponding {@link CrudRepository} to persist {@link ProjectDatabaseUpdaterInfo} in a database.
 *
 * @see ProjectDatabaseUpdaterInfo
 */
public interface ProjectDatabaseUpdaterInfoRepository extends CrudRepository<ProjectDatabaseUpdaterInfo, Long> {

    Optional<ProjectDatabaseUpdaterInfo> findFirstByStatusOrderByTimeDesc(ProjectDatabaseUpdaterInfo.Status status);

    @Query("SELECT p FROM ProjectDatabaseUpdaterInfo AS p WHERE p.time = (SELECT MAX(p.time) FROM ProjectDatabaseUpdaterInfo p)")
    Optional<ProjectDatabaseUpdaterInfo> findLatest();

    long countByStatus(ProjectDatabaseUpdaterInfo.Status status);

}
