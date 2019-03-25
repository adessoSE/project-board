package de.adesso.projectboard.base.scheduled;

import java.time.LocalDateTime;

/**
 * Classes implementing this interface that are supplied as a spring bean will be automatically
 * registered for execution.
 *
 * @see AutoRegistered
 */
public interface ScheduledJob {

    /**
     * Executed when an earlier execution of this job was successful.
     *
     * @param lastExecuteTime
     *          The last time the job was executed, not null.
     *
     * @throws Exception
     *          When an error occurs while executing the job.
     */
    void execute(LocalDateTime lastExecuteTime) throws Exception;

    /**
     * Executed when no previous execution of this job was successful.
     *
     * @throws Exception
     *          When an error occurs while executing the job.
     *
     * @see #execute(LocalDateTime)
     */
    void execute() throws Exception;

    /**
     * The unique identifier of this scheduled job is used to differentiate
     * it from other scheduled jobs. The method is expected to return the same
     * result all the time.
     *
     * @return
     *          The <b>unique</b> identifier for this job, not null.
     */
    String getJobIdentifier();

    /**
     *
     * @param lastExecuteTime
     *          The last time the job was executed successfully, not null.
     *
     * @return
     *          {@code true}, iff the job should execute.
     */
    boolean shouldExecute(LocalDateTime lastExecuteTime);

}
