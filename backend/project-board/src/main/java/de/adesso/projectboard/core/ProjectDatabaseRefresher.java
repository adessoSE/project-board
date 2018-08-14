package de.adesso.projectboard.core;

import de.adesso.projectboard.core.configuration.ProjectBoardConfigurationProperties;
import de.adesso.projectboard.core.base.reader.AbstractProjectReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Component
public class ProjectDatabaseRefresher {

    private final AbstractProjectReader projectRestReader;

    private final Duration refreshInterval;

    private LocalDateTime lastRefresh;

    @Autowired
    public ProjectDatabaseRefresher(AbstractProjectReader projectRestReader, ProjectBoardConfigurationProperties properties) {
        this.projectRestReader = projectRestReader;

        this.refreshInterval = Duration.of(properties.getRefreshInterval(), ChronoUnit.MINUTES);
    }

    @Scheduled(fixedDelay = 10000L)
    public void updateProjects() {
        // only call when the time delta between
        // now and lastRefreshed > ProjectBoardConfigurationProperties#refreshInterval
        projectRestReader.getUpdatedProjectsSince(lastRefresh);

    }


}
