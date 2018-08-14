package de.adesso.projectboard.core.updater.persistence;

import org.springframework.data.repository.CrudRepository;

public interface ProjectDatabaseUpdaterInfoRepository extends CrudRepository<ProjectDatabaseUpdaterInfo, Long> {

    ProjectDatabaseUpdaterInfo findFirstBySuccessOrderByTimeDesc(ProjectDatabaseUpdaterInfo.Success success);

}
