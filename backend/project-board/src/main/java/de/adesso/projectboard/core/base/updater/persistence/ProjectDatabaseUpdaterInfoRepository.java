package de.adesso.projectboard.core.base.updater.persistence;

import org.springframework.data.repository.CrudRepository;

/**
 * The corresponding {@link CrudRepository} to persist {@link ProjectDatabaseUpdaterInfo} in a database.
 *
 * @see ProjectDatabaseUpdaterInfo
 */
public interface ProjectDatabaseUpdaterInfoRepository extends CrudRepository<ProjectDatabaseUpdaterInfo, Long> {

    ProjectDatabaseUpdaterInfo findFirstByStatusOrderByTimeDesc(ProjectDatabaseUpdaterInfo.Status status);

}
