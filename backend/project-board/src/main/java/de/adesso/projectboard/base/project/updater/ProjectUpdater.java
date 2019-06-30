package de.adesso.projectboard.base.project.updater;

import de.adesso.projectboard.base.configuration.ProjectBoardConfigurationProperties;
import de.adesso.projectboard.base.normalizer.Normalizer;
import de.adesso.projectboard.base.project.persistence.Project;
import de.adesso.projectboard.base.project.service.ProjectService;
import de.adesso.projectboard.base.reader.ProjectReader;
import de.adesso.projectboard.base.scheduled.AutoRegistered;
import de.adesso.projectboard.base.scheduled.ScheduledJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

/**
 * {@link ScheduledJob} implementation to update persisted jobs.
 */
@Component
@AutoRegistered
public class ProjectUpdater implements ScheduledJob {

    private final ProjectService projectService;

    private final ProjectReader projectReader;

    private final Duration refreshIntervalDuration;

    private final List<Normalizer<Project>> normalizers;

    private final Clock clock;

    @Autowired(required = false)
    public ProjectUpdater(ProjectService projectService,
                          ProjectReader projectReader,
                          ProjectBoardConfigurationProperties properties,
                          @Lazy List<Normalizer<Project>> normalizers,
                          Clock clock) {
        this.projectService = projectService;
        this.projectReader = projectReader;
        this.normalizers = normalizers;
        this.clock = clock;

        this.refreshIntervalDuration = Duration.ofMinutes(properties.getRefreshInterval());
    }

    @Autowired(required = false)
    public ProjectUpdater(ProjectService projectService,
                          ProjectReader projectReader,
                          ProjectBoardConfigurationProperties properties,
                          Clock clock) {
        this(projectService, projectReader, properties, List.of(), clock);
    }

    @Override
    public void execute(LocalDateTime lastExecuteTime) throws Exception {
        var projects = projectReader.getAllProjectsSince(lastExecuteTime);
        normalizeAndSave(projects);
    }

    @Override
    public void execute() throws Exception {
        var projects = projectReader.getInitialProjects();
        normalizeAndSave(projects);
    }

    @Override
    public String getJobIdentifier() {
        return "PROJECT-UPDATER";
    }

    @Override
    public boolean shouldExecute(LocalDateTime lastExecuteTime) {
        Duration lastUpdateDeltaDuration = Duration.between(lastExecuteTime, LocalDateTime.now(clock)).abs();

        return refreshIntervalDuration.compareTo(lastUpdateDeltaDuration) <= 0;
    }

    private void normalizeAndSave(List<Project> projects) {
        var normalizedProjects = projects;
        for(var normalizer : normalizers) {
            normalizedProjects = normalizer.normalize(normalizedProjects);
        }

        projectService.saveAll(normalizedProjects);
    }

}
