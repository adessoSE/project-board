package de.adesso.projectboard.base.scheduled;

import de.adesso.projectboard.base.scheduled.persistence.ScheduledJobLog;
import de.adesso.projectboard.base.scheduled.persistence.ScheduledJobLogRepository;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.Callable;

/**
 * Executor executing all <i>registered</i> {@link ScheduledJob} implementations periodically,
 * persisting job logs with timestamps of the last execution.
 *
 * All <b>top level (non-nested), singleton, eager-initializing</b> beans present in the
 * spring application context are automatically registered.
 */
@Component
@Slf4j
public class ScheduledJobExecutor implements ApplicationContextAware {

    /**
     * Repository to save the created {@code ScheduledJobLog}s.
     */
    private final ScheduledJobLogRepository jobLogRepo;

    /**
     * The clock to read the time from.
     */
    private final Clock clock;

    /**
     * Map mapping a {@code ScheduledJob} to its unique
     * identifier.
     */
    final HashMap<ScheduledJob, String> scheduledJobIdMap;

    @Autowired
    public ScheduledJobExecutor(ScheduledJobLogRepository jobLogRepo, Clock clock) {
        this.jobLogRepo = jobLogRepo;
        this.clock = clock;
        this.scheduledJobIdMap = new HashMap<>();
    }

    /**
     * Executes all jobs that match one of the following conditions:
     *
     * <ul>
     *      <li>
     *          No {@link ScheduledJobLog} with the job's {@link ScheduledJob#getJobIdentifier() job identifier}
     *          and status set to {@code SUCCESS} is found.
     *      </li>
     *
     *      <li>
     *          A {@link ScheduledJobLog} with the properties above is found and the {@link ScheduledJob#shouldExecute(LocalDateTime)}
     *          method with the job log's {@link ScheduledJobLog#getTime() time} returns {@code true}.
     *      </li>
     * </ul>
     */
    @Scheduled(
            initialDelay = 0L,
            fixedDelay = 45_000L
    )
    public void executeJobs() {
        log.debug("Executing registered scheduled jobs...");

        scheduledJobIdMap.forEach((job, jobIdentifier) -> {
            var jobLogOptional =
                    jobLogRepo.findFirstByJobIdentifierAndStatusOrderByTimeDesc(jobIdentifier, ScheduledJobLog.Status.SUCCESS);

            if(jobLogOptional.isPresent()) {
                var jobLog = jobLogOptional.get();
                var lastSuccessfulExecute = jobLog.getTime();

                if(job.shouldExecute(lastSuccessfulExecute)) {
                    execute(() -> {
                        job.execute(lastSuccessfulExecute);
                        return null;
                    }, jobIdentifier);
                }
            } else {
                execute(() -> {
                    job.execute();
                    return null;
                }, jobIdentifier);
            }
        });
    }

    void execute(Callable<Void> callable, String jobIdentifier) {
        ScheduledJobLog jobLog;

        try {
            callable.call();

            // execution went well
            var executionTime = LocalDateTime.now(clock);
            jobLog = new ScheduledJobLog(executionTime, jobIdentifier, ScheduledJobLog.Status.SUCCESS);

            log.debug(String.format("Successfully executed scheduled job with job identifier %s!",
                    jobIdentifier));
        } catch (Exception ex) {
            // execution failed
            var executionTime = LocalDateTime.now(clock);
            jobLog = new ScheduledJobLog(executionTime, jobIdentifier, ScheduledJobLog.Status.FAILURE);

            log.debug(String.format("Failed to executed scheduled job with job identifier %s!",
                    jobIdentifier), ex);
        }

        jobLogRepo.save(jobLog);
    }



    /**
     * Adds a scheduled job to the executed jobs of this executor.
     *
     * @param scheduledJob
     *          The scheduled job to registered, not null.
     *
     * @throws IllegalArgumentException
     *          When one of the following conditions is satisfied:
     *          <ul>
     *              <li>
     *                  The job identifier returned by the {@code scheduledJob}
     *                  is {@code null}.
     *              </li>
     *
     *              <li>
     *                  The job identifier returned by the {@code scheduledJob}
                        contains only whitespace.
     *              </li>
     *          </ul>
     *
     * @throws IllegalStateException
     *          When a job is registered whose job identifier after trimming already
     *          exists.
     */
    public void registerScheduledJob(@NonNull ScheduledJob scheduledJob) {
        if(!scheduledJobIdMap.containsKey(scheduledJob)) {
           var jobIdentifier = scheduledJob.getJobIdentifier();
           if(Objects.isNull(jobIdentifier)) {
               throw new IllegalArgumentException("A job's identifier can't be null!");
           }

           var trimmedJobIdentifier = jobIdentifier.trim();
           if(trimmedJobIdentifier.isEmpty()) {
               throw new IllegalArgumentException("A job's identifier can't contain only whitespace!");
           }

           if(scheduledJobIdMap.containsValue(trimmedJobIdentifier)) {
               throw new IllegalStateException(String.format("Multiple scheduled jobs with the same job identifier '%s' found!",
                       trimmedJobIdentifier));
           }

           scheduledJobIdMap.put(scheduledJob, trimmedJobIdentifier);

           log.debug(String.format("Added scheduled job (%s) with job identifier '%s'!",
                   scheduledJob.getClass().getName(),
                   jobIdentifier));
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        var scheduledJobs = applicationContext.getBeansOfType(ScheduledJob.class).values();
        scheduledJobs.stream()
                .filter(this::shouldAutoRegister)
                .forEach(this::registerScheduledJob);
    }

    /**
     *
     * @param scheduledJob
     *          The scheduled job to check, not null.
     *
     * @return
     *          {@code true}, iff the {@code scheduledJob} should
     *          be registered automatically.
     */
    boolean shouldAutoRegister(ScheduledJob scheduledJob) {
        var autoRegisteredAnnotation = scheduledJob.getClass().getAnnotation(AutoRegistered.class);

        if(Objects.isNull(autoRegisteredAnnotation)) {
            return true;
        } else {
            return autoRegisteredAnnotation.value();
        }
    }

}
