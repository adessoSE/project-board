package de.adesso.projectboard.base.updater;

import de.adesso.projectboard.base.configuration.ProjectBoardConfigurationProperties;
import de.adesso.projectboard.base.project.persistence.Project;
import de.adesso.projectboard.base.project.service.ProjectService;
import de.adesso.projectboard.base.reader.ProjectReader;
import de.adesso.projectboard.base.updater.persistence.ProjectUpdateJob;
import de.adesso.projectboard.base.updater.persistence.ProjectUpdateJobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * The {@link Service} to update the projects in the database.
 */
@Service
public class ProjectUpdater {

    private final ProjectService projectService;

    private final ProjectUpdateJobRepository updateJobRepo;

    private final ProjectReader projectReader;

    private final Duration refreshIntervalDuration;

    private final Clock clock;

    @Autowired
    public ProjectUpdater(ProjectService projectService,
                          ProjectUpdateJobRepository updateJobRepo,
                          ProjectReader projectReader,
                          ProjectBoardConfigurationProperties properties,
                          Clock clock) {
        this.updateJobRepo = updateJobRepo;
        this.projectService = projectService;
        this.projectReader = projectReader;
        this.clock = clock;

        this.refreshIntervalDuration = Duration.ofMinutes(properties.getRefreshInterval());
    }

    /**
     * Refreshes the project database by using a {@link ProjectReader}.
     *
     * @see #shouldUpdate(ProjectUpdateJob)
     */
    @Scheduled(fixedDelay = 30000L)
    public void refreshProjectDatabase() {
        Optional<ProjectUpdateJob> lastSuccessfulUpdate
                = updateJobRepo.findFirstByStatusOrderByTimeDesc(ProjectUpdateJob.Status.SUCCESS);

        try {
            List<Project> projects;

            if(lastSuccessfulUpdate.isPresent()) {
                if(!shouldUpdate(lastSuccessfulUpdate.get())) {
                    return;
                }

                projects = projectReader.getAllProjectsSince(lastSuccessfulUpdate.get().getTime());
            } else {
                projects = projectReader.getInitialProjects();
            }

            this.projectService.saveAll(projects);

            updateJobRepo.save(new ProjectUpdateJob(LocalDateTime.now(clock), ProjectUpdateJob.Status.SUCCESS));
        } catch (Exception e) {
            ProjectUpdateJob info = new ProjectUpdateJob(LocalDateTime.now(clock),
                    ProjectUpdateJob.Status.FAILURE,
                    e);

            updateJobRepo.save(info);
        }

    }

    /**
     *
     * @param lastUpdate
     *          The {@link ProjectUpdateJob} object of the last successful update.
     *
     * @return
     *          {@code true} if the difference between {@link LocalDateTime#now(Clock) now} and
     *          {@link ProjectUpdateJob#getTime()} is longer than
     *          {@link ProjectBoardConfigurationProperties#getRefreshInterval()} minutes.
     */
    private boolean shouldUpdate(ProjectUpdateJob lastUpdate) {
        Duration lastUpdateDeltaDuration = Duration.between(lastUpdate.getTime(), LocalDateTime.now(clock)).abs();

        return refreshIntervalDuration.compareTo(lastUpdateDeltaDuration) <= 0;
    }

}
