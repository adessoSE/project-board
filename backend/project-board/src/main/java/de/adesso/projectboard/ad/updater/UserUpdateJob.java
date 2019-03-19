package de.adesso.projectboard.ad.updater;

import de.adesso.projectboard.ad.configuration.LdapConfigurationProperties;
import de.adesso.projectboard.base.scheduled.FixedHourScheduledJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Clock;
import java.time.LocalDateTime;

/**
 * {@link FixedHourScheduledJob} used to update user related data using a {@link UserUpdater}. The
 * execution time can be configured by modifying the {@link LdapConfigurationProperties#getUpdateHour() update hour}
 * of the {@link LdapConfigurationProperties}.
 */
@Slf4j
public class UserUpdateJob extends FixedHourScheduledJob {

    private final UserUpdater userUpdater;

    @Autowired
    public UserUpdateJob(UserUpdater userUpdater, LdapConfigurationProperties properties, Clock clock) {
        super(clock, properties.getUpdateHour());

        this.userUpdater = userUpdater;
    }

    @Override
    public void execute(LocalDateTime lastExecuteTime) {
        userUpdater.updateHierarchyAndUserData();
        log.debug("Successfully updated users and hierarchy!");
    }

    @Override
    public void execute() {
        userUpdater.updateHierarchyAndUserData();
        log.debug("Successfully updated users and hierarchy!");
    }

    @Override
    public String getJobIdentifier() {
        return "USER-UPDATER";
    }

}
