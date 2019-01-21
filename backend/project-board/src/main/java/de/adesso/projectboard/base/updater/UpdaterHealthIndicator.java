package de.adesso.projectboard.base.updater;

import de.adesso.projectboard.base.updater.persistence.ProjectUpdateJob;
import de.adesso.projectboard.base.updater.persistence.ProjectUpdateJobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * A {@link HealthIndicator} implementation for the {@link ProjectUpdater}.
 *
 * @see ProjectUpdater
 */
@Component
public class UpdaterHealthIndicator implements HealthIndicator {

    private final ProjectUpdateJobRepository updaterRepository;

    @Autowired
    public UpdaterHealthIndicator(ProjectUpdateJobRepository repository) {
        this.updaterRepository = repository;
    }

    /**
     *
     * @return
     *          The {@link Health} of the {@link ProjectUpdater}. Gives additional
     *          details about the status.
     */
    @Override
    public Health health() {
        Optional<ProjectUpdateJob> lastInfoOptional = updaterRepository.findLatest();

        if(lastInfoOptional.isPresent()) {
            ProjectUpdateJob lastInfo = lastInfoOptional.get();

            if(ProjectUpdateJob.Status.FAILURE.equals(lastInfo.getStatus())) {
                return Health.down()
                        .withDetail("reason", lastInfoOptional.get().getFailureReason())
                        .build();
            } else {
                return Health.up()
                        .withDetail("lastUpdate", lastInfo.getTime())
                        .withDetail("totalUpdates", updaterRepository.count())
                        .withDetail("successfulUpdates", updaterRepository.countByStatus(ProjectUpdateJob.Status.SUCCESS))
                        .build();
            }

        }

        return Health.up().build();
    }

}
