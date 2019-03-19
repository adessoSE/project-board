package helper.base.scheduled;

import de.adesso.projectboard.base.scheduled.ScheduledJob;

import java.time.LocalDateTime;

public class NonAnnotatedJob implements ScheduledJob {

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

    @Override
    public boolean shouldExecute(LocalDateTime lastExecuteTime) {
        return false;
    }

}
