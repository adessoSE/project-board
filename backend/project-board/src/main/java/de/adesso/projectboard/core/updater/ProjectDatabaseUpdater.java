package de.adesso.projectboard.core.updater;

import de.adesso.projectboard.core.base.project.AbstractProject;
import de.adesso.projectboard.core.base.project.AbstractProjectRepository;
import de.adesso.projectboard.core.base.reader.AbstractProjectReader;
import de.adesso.projectboard.core.updater.persistence.ProjectDatabaseUpdaterInfo;
import de.adesso.projectboard.core.updater.persistence.ProjectDatabaseUpdaterInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProjectDatabaseUpdater {

    private final ProjectDatabaseUpdaterInfoRepository infoRepository;

    private final AbstractProjectRepository projectRepository;

    private final AbstractProjectReader projectReader;

    @Autowired
    public ProjectDatabaseUpdater(ProjectDatabaseUpdaterInfoRepository infoRepository,
                                  AbstractProjectRepository projectRepository,
                                  AbstractProjectReader projectReader) {
        this.infoRepository = infoRepository;
        this.projectRepository = projectRepository;
        this.projectReader = projectReader;
    }

    @Scheduled(fixedDelay = 10000L)
    public void refreshProjectDatabase() {

        ProjectDatabaseUpdaterInfo lastUpdate = infoRepository.findFirstBySuccessOrderByTimeDesc(ProjectDatabaseUpdaterInfo.Success.SUCCESS);

        // only call when the time delta between
        // now and lastRefresh >= ProjectBoardConfigurationProperties#refreshInterval
        List<? extends AbstractProject> projects = projectReader.getUpdatedProjectsSince();

        // update DB content ...
    }



}
