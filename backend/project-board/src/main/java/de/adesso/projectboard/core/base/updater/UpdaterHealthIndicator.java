package de.adesso.projectboard.core.base.updater;

import de.adesso.projectboard.core.base.updater.persistence.ProjectDatabaseUpdaterInfo;
import de.adesso.projectboard.core.base.updater.persistence.ProjectDatabaseUpdaterInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UpdaterHealthIndicator implements HealthIndicator {

    private final ProjectDatabaseUpdaterInfoRepository repository;

    @Autowired
    public UpdaterHealthIndicator(ProjectDatabaseUpdaterInfoRepository repository) {
        this.repository = repository;
    }

    @Override
    public Health health() {
        Optional<ProjectDatabaseUpdaterInfo> lastInfoOptional = repository.findLatest();

        if(lastInfoOptional.isPresent()) {
            ProjectDatabaseUpdaterInfo lastInfo = lastInfoOptional.get();

            if(ProjectDatabaseUpdaterInfo.Status.FAILURE.equals(lastInfo.getStatus())) {
                return Health.down()
                        .withDetail("reason", lastInfoOptional.get().getFailureReason())
                        .build();
            } else {
                return Health.up()
                        .build();
            }

        }

        return Health.up().build();
    }

}
