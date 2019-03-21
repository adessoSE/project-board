package helper.base.scheduled;

import de.adesso.projectboard.base.scheduled.AutoRegistered;
import de.adesso.projectboard.base.scheduled.ScheduledJob;

import java.time.LocalDateTime;

@AutoRegistered(false)
public class NonAutoRegisteredJob implements ScheduledJob {

    @Override
    public void execute(LocalDateTime lastExecuteTime) throws Exception {

    }

    @Override
    public void execute() throws Exception {

    }

    @Override
    public String getJobIdentifier() {
        return "NON-AUTO-REGISTERED";
    }

    @Override
    public boolean shouldExecute(LocalDateTime lastExecuteTime) {
        return false;
    }

}
