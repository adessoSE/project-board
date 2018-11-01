package de.adesso.projectboard.base.updater;

import de.adesso.projectboard.base.configuration.ProjectBoardConfigurationProperties;
import de.adesso.projectboard.base.project.persistence.Project;
import de.adesso.projectboard.base.project.persistence.ProjectRepository;
import de.adesso.projectboard.base.reader.ProjectReader;
import de.adesso.projectboard.base.updater.persistence.UpdateJob;
import de.adesso.projectboard.base.updater.persistence.UpdateJobRepository;
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
 * The {@link Service} to update the projects in the database.
 */
@Service
public class ProjectDatabaseUpdater {

    private final UpdateJobRepository infoRepository;

    private final ProjectRepository projectRepository;

    private final ProjectReader projectReader;

    private final Duration refreshIntervalDuration;

    private final Logger logger;

    @Autowired
    public ProjectDatabaseUpdater(UpdateJobRepository infoRepository,
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
     * @see #shouldUpdate(UpdateJob)
     */
    @Scheduled(fixedDelay = 30000L)
    public void refreshProjectDatabase() {
        Optional<UpdateJob> lastSuccessfulUpdate
                = infoRepository.findFirstByStatusOrderByTimeDesc(UpdateJob.Status.SUCCESS);

        try {
            List<? extends Project> projects;

            if(lastSuccessfulUpdate.isPresent()) {
                if(!shouldUpdate(lastSuccessfulUpdate.get())) {
                    return;
                }

                projects = projectReader.getAllProjectsSince(lastSuccessfulUpdate.get().getTime());
            } else {
                projects = projectReader.getInitialProjects();
            }

            projectRepository.saveAll(projects);

            infoRepository.save(new UpdateJob(LocalDateTime.now(), UpdateJob.Status.SUCCESS));
        } catch (Exception e) {
            logger.error("Error updating project database!", e);

            UpdateJob info = new UpdateJob(LocalDateTime.now(),
                    UpdateJob.Status.FAILURE,
                    e);

            infoRepository.save(info);
        }

    }

    /**
     *
     * @param lastUpdate
     *          The {@link UpdateJob} object of the last successful update.
     *
     * @return
     *          {@code true} if the difference between {@link LocalDateTime#now() now} and
     *          {@link UpdateJob#getTime()} is longer than
     *          {@link ProjectBoardConfigurationProperties#getRefreshInterval()} minutes.
     */
    private boolean shouldUpdate(UpdateJob lastUpdate) {
        Duration lastUpdateDeltaDuration = Duration.between(lastUpdate.getTime(), LocalDateTime.now()).abs();

        return refreshIntervalDuration.compareTo(lastUpdateDeltaDuration) <= 0;
    }

}
