package helper.base.scheduled;

import de.adesso.projectboard.base.scheduled.FixedHourScheduledJob;

import java.time.Clock;
import java.time.LocalDateTime;

public class FixedHourJob extends FixedHourScheduledJob {

    public FixedHourJob(Clock clock, int jobExecutionHour) {
        // just call super constructor
        super(clock, jobExecutionHour);
    }

    @Override
    public void execute(LocalDateTime lastExecuteTime) throws Exception {

    }

    @Override
    public void execute() throws Exception {

    }

    @Override
    public String getJobIdentifier() {
        return null;
    }

}
