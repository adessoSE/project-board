package de.adesso.projectboard.core.base.updater;

import de.adesso.projectboard.core.base.configuration.ProjectBoardConfigurationProperties;
import de.adesso.projectboard.core.base.rest.project.persistence.AbstractProject;
import de.adesso.projectboard.core.base.rest.project.persistence.ProjectRepository;
import de.adesso.projectboard.core.base.reader.ProjectReader;
import de.adesso.projectboard.core.base.updater.persistence.ProjectDatabaseUpdaterInfo;
import de.adesso.projectboard.core.base.updater.persistence.ProjectDatabaseUpdaterInfoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * The central {@link Service} to update the projects in the database.
 */
@Service
public class ProjectDatabaseUpdater {

    private final ProjectDatabaseUpdaterInfoRepository infoRepository;

    private final ProjectRepository projectRepository;

    private final ProjectReader projectReader;

    private final Duration refreshIntervalDuration;

    private final Logger logger;

    @Autowired
    public ProjectDatabaseUpdater(ProjectDatabaseUpdaterInfoRepository infoRepository,
                                  ProjectRepository projectRepository,
                                  ProjectReader projectReader,
                                  ProjectBoardConfigurationProperties properties) {
        this.infoRepository = infoRepository;
        this.projectRepository = projectRepository;
        this.projectReader = projectReader;

        this.refreshIntervalDuration = Duration.ofMinutes(properties.getRefreshInterval());

        this.logger = LoggerFactory.getLogger(getClass());
    }

    /**
     * Refreshes the project database by using a {@link ProjectReader}.
     *
     * @see #shouldUpdate(ProjectDatabaseUpdaterInfo)
     */
    @Scheduled(fixedDelay = 30000L)
    public void refreshProjectDatabase() {
        Optional<ProjectDatabaseUpdaterInfo> lastSuccessfulUpdate
                = infoRepository.findFirstByStatusOrderByTimeDesc(ProjectDatabaseUpdaterInfo.Status.SUCCESS);


        try {
            List<? extends AbstractProject> projects;

            if(lastSuccessfulUpdate.isPresent()) {
                if(!shouldUpdate(lastSuccessfulUpdate.get())) {
                    return;
                }

                projects = projectReader.getAllProjectsSince(lastSuccessfulUpdate.get().getTime());
            } else {
                projects = projectReader.getInitialProjects();
            }

            projectRepository.saveAll(projects);

            infoRepository.save(new ProjectDatabaseUpdaterInfo(LocalDateTime.now(), ProjectDatabaseUpdaterInfo.Status.SUCCESS));
        } catch (Exception e) {
            logger.error("Error updating project database!", e);

            ProjectDatabaseUpdaterInfo info = new ProjectDatabaseUpdaterInfo(LocalDateTime.now(),
                    ProjectDatabaseUpdaterInfo.Status.FAILURE,
                    e);

            infoRepository.save(info);
        }

    }

    /**
     *
     * @param lastUpdate
     *          The {@link ProjectDatabaseUpdaterInfo} object of the last successful update.
     *
     * @return
     *          {@code true} if the difference between {@link LocalDateTime#now() now} and
     *          {@link ProjectDatabaseUpdaterInfo#getTime()} is longer than
     *          {@link ProjectBoardConfigurationProperties#getRefreshInterval()} minutes.
     */
    private boolean shouldUpdate(ProjectDatabaseUpdaterInfo lastUpdate) {
        Duration lastUpdateDeltaDuration = Duration.between(lastUpdate.getTime(), LocalDateTime.now()).abs();

        return refreshIntervalDuration.compareTo(lastUpdateDeltaDuration) <= 0;
    }

}
