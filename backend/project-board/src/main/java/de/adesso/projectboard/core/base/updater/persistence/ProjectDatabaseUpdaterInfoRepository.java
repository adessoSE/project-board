package de.adesso.projectboard.core.base.updater.persistence;

import org.springframework.data.repository.CrudRepository;

public interface ProjectDatabaseUpdaterInfoRepository extends CrudRepository<ProjectDatabaseUpdaterInfo, Long> {

    ProjectDatabaseUpdaterInfo findFirstByStatusOrderByTimeDesc(ProjectDatabaseUpdaterInfo.Status status);

}
