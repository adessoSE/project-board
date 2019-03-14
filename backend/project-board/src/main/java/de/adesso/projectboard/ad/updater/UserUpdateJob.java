package de.adesso.projectboard.ad.updater;

import de.adesso.projectboard.ad.configuration.LdapConfigurationProperties;
import de.adesso.projectboard.base.scheduled.ScheduledJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Slf4j
@Transactional
public class UserUpdateJob implements ScheduledJob {

    private final UserUpdater userUpdater;

    private final Clock clock;

    private final int updateHour;

    @Autowired
    public UserUpdateJob(UserUpdater userUpdater, LdapConfigurationProperties properties, Clock clock) {
        this.userUpdater = userUpdater;
        this.clock = clock;

        this.updateHour = properties.getUpdateHour();
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

    /**
     *
     * @param lastExecuteTime
     *          The last time the job was executed successfully, not null.
     *
     * @return
     *          {@code true}, iff one of the following conditions is satisfied:
     *          <ul>
     *              <li>
     *                  The last execute was at least two days ago.
     *              </li>
     *
     *              <li>
     *                  The last execute was yesterday and the current hour is
     *                  greater or equal to the update hour.
     *              </li>
     *
     *              <li>
     *                  The last update was today but before the update hour
     *                  was reached. (The update should have been done yesterday)
     *              </li>
     *          </ul>
     */
    @Override
    public boolean shouldExecute(LocalDateTime lastExecuteTime) {
        var now = LocalDateTime.now(clock);
        var today = LocalDate.now(clock);
        var yesterday = today.minus(1, ChronoUnit.DAYS);
        var lastExecuteDate = lastExecuteTime.toLocalDate();

        // last execute two days or more ago
        if(yesterday.isAfter(lastExecuteDate)) {
            return true;

        // last execute yesterday
        } else if(today.isAfter(lastExecuteDate)) {
            return now.getHour() >= this.updateHour;

        // last execute today
        } else {
            return lastExecuteTime.getHour() < this.updateHour;
        }
    }

}
