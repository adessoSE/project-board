package de.adesso.projectboard.base.scheduled;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Abstract class implementing the {@link ScheduledJob} interface to realize jobs running once
 * a day at a specific hour of the day.
 */
public abstract class FixedHourScheduledJob implements ScheduledJob {

    /**
     * The clock to read the time from.
     */
    protected final Clock clock;

    /**
     * The hour of the day the the job should be executed at.
     */
    protected final int jobExecutionHour;

    /**
     *
     * @param clock
     *          The clock to read the time from, not null.
     *
     * @param jobExecutionHour
     *          The hour of the day the the job should be executed at. Must
     *          be between 0 and 23.
     *
     * @throws IllegalArgumentException
     *          When the given {@code jobExecutionHour} is smaller than 0 or
     *          greater than 23.
     */
    protected FixedHourScheduledJob(Clock clock, int jobExecutionHour) {
        if(jobExecutionHour < 0 || jobExecutionHour > 23) {
            throw new IllegalArgumentException("Job execution hour has to be between 0 and 23!");
        }

        this.clock = clock;
        this.jobExecutionHour = jobExecutionHour;
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
     *                  greater or equal to the execution hour.
     *              </li>
     *
     *              <li>
     *                  The last update was today but before the update hour
     *                  was reached. (The update should have been done yesterday)
     *              </li>
     *          </ul>
     */
    @Override
    public final boolean shouldExecute(LocalDateTime lastExecuteTime) {
        var now = LocalDateTime.now(clock);
        var today = LocalDate.now(clock);
        var yesterday = today.minus(1, ChronoUnit.DAYS);
        var lastExecuteDate = lastExecuteTime.toLocalDate();

        // last execute two days or more ago
        if(yesterday.isAfter(lastExecuteDate)) {
            return true;

            // last execute yesterday
        } else if(today.isAfter(lastExecuteDate)) {
            return now.getHour() >= this.jobExecutionHour;

            // last execute today
        } else {
            return lastExecuteTime.getHour() < this.jobExecutionHour;
        }
    }

}
