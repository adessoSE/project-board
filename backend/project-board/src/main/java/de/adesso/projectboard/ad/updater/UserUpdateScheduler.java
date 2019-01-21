package de.adesso.projectboard.ad.updater;

import de.adesso.projectboard.ad.configuration.LdapConfigurationProperties;
import de.adesso.projectboard.ad.updater.persistence.UserUpdateJob;
import de.adesso.projectboard.ad.updater.persistence.UserUpdateJobRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

@Service
@Slf4j
@Transactional
public class UserUpdateScheduler {

    private final UserUpdater userUpdater;

    private final UserUpdateJobRepository userUpdateJobRepo;

    private final Clock clock;

    private final long updateHour;

    @Autowired
    public UserUpdateScheduler(UserUpdater userUpdater, UserUpdateJobRepository userUpdateJobRepo, Clock clock, LdapConfigurationProperties properties) {
        this.userUpdater = userUpdater;
        this.userUpdateJobRepo = userUpdateJobRepo;
        this.clock = clock;

        this.updateHour = properties.getUpdateHour();
    }

    @Scheduled(
            initialDelay = 0L,
            fixedDelay = 300_000L
    )
    public void update() {
        var lastSuccessfulUpdate = userUpdateJobRepo.findFirstBySuccessTrueOrderByUpdateTimeDesc()
                .orElse(null);

        if(shouldUpdate(lastSuccessfulUpdate)) {
            try {
                userUpdater.updateHierarchyAndUserData();

                var job = new UserUpdateJob(LocalDateTime.now(clock), true);
                userUpdateJobRepo.save(job);

            } catch (Exception e) {
                var job = new UserUpdateJob(LocalDateTime.now(clock), false);
                userUpdateJobRepo.save(job);

                log.debug("Error while updating user related data!", e);
            }
        }

    }

    /**
     *
     * @param lastSuccessfulUpdate
     *          The last successful update, nullable.
     *
     * @return
     *          {@code true}, iff a update should be performed
     *          at the time of calling.
     */
    boolean shouldUpdate(UserUpdateJob lastSuccessfulUpdate) {
        if(Objects.isNull(lastSuccessfulUpdate)) {
            return true;
        }

        var now = LocalDateTime.now(clock);
        var today = now.toLocalDate();
        var yesterday = today.minus(1, ChronoUnit.DAYS);
        var lastUpdateDate = lastSuccessfulUpdate.getUpdateTime().toLocalDate();

        if(yesterday.isAfter(lastUpdateDate)) {
            return true;
        } else if(today.isAfter(lastUpdateDate)) {
            return now.getHour() >= updateHour;
        } else {
            return now.getHour() <= updateHour;
        }
    }

}
