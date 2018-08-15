package de.adesso.projectboard.core.base.updater;

import de.adesso.projectboard.core.base.configuration.ProjectBoardConfigurationProperties;
import de.adesso.projectboard.core.base.project.persistence.AbstractProject;
import de.adesso.projectboard.core.base.project.persistence.ProjectRepository;
import de.adesso.projectboard.core.base.reader.AbstractProjectReader;
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

@Service
public class ProjectDatabaseUpdater {

    private final ProjectDatabaseUpdaterInfoRepository infoRepository;

    private final ProjectRepository projectRepository;

    private final AbstractProjectReader projectReader;

    private final Duration refreshIntervalDuration;

    private final Duration maxUpdateDuration;

    private final Logger logger;

    @Autowired
    public ProjectDatabaseUpdater(ProjectDatabaseUpdaterInfoRepository infoRepository,
                                  ProjectRepository projectRepository,
                                  AbstractProjectReader projectReader,
                                  ProjectBoardConfigurationProperties properties) {
        this.infoRepository = infoRepository;
        this.projectRepository = projectRepository;
        this.projectReader = projectReader;

        this.refreshIntervalDuration = Duration.ofSeconds(properties.getRefreshInterval());
        this.maxUpdateDuration = Duration.ofDays(properties.getMaxUpdateDays());

        this.logger = LoggerFactory.getLogger(getClass());
    }

    @Scheduled(fixedDelay = 10000L)
    public void refreshProjectDatabase() {
        ProjectDatabaseUpdaterInfo lastSuccessfulUpdate = infoRepository.findFirstByStatusOrderByTimeDesc(ProjectDatabaseUpdaterInfo.Status.SUCCESS);

        if(!shouldUpdate(lastSuccessfulUpdate)) {
            return;
        }

        try {
            List<? extends AbstractProject> projects = projectReader.getAllProjectsSince(getLastUpdateTime(lastSuccessfulUpdate));

            projectRepository.saveAll(projects);

            infoRepository.save(new ProjectDatabaseUpdaterInfo(LocalDateTime.now(), ProjectDatabaseUpdaterInfo.Status.SUCCESS));
        } catch (Exception e) {
            logger.debug(e.toString());

            infoRepository.save(new ProjectDatabaseUpdaterInfo(LocalDateTime.now(), ProjectDatabaseUpdaterInfo.Status.FAILURE));
        }

    }

    private boolean shouldUpdate(ProjectDatabaseUpdaterInfo lastUpdate) {
        if(lastUpdate == null) {
            return true;
        }

        Duration lastUpdateDeltaDuration = Duration.between(lastUpdate.getTime(), LocalDateTime.now()).abs();

        return refreshIntervalDuration.compareTo(lastUpdateDeltaDuration) <= 0;
    }

    private LocalDateTime getLastUpdateTime(ProjectDatabaseUpdaterInfo lastUpdate) {
        if(lastUpdate != null) {
            Duration updateDelta = Duration.between(LocalDateTime.now(), lastUpdate.getTime());

            if(updateDelta.compareTo(maxUpdateDuration) <= 0) {
                return lastUpdate.getTime();
            }
        }

        return LocalDateTime.now().minus(maxUpdateDuration);
    }

}
