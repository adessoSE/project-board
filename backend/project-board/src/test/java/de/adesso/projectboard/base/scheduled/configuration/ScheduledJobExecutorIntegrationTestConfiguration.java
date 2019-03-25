package de.adesso.projectboard.base.scheduled.configuration;

import de.adesso.projectboard.base.scheduled.ScheduledJobExecutor;
import de.adesso.projectboard.base.scheduled.persistence.ScheduledJobLogRepository;
import helper.base.scheduled.AnnotatedJob;
import helper.base.scheduled.NonAnnotatedJob;
import helper.base.scheduled.NonAutoRegisteredJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class ScheduledJobExecutorIntegrationTestConfiguration {

    @Bean
    public AnnotatedJob annotatedJob() {
        return new AnnotatedJob();
    }

    @Bean
    public NonAnnotatedJob nonAnnotatedJob() {
        return new NonAnnotatedJob();
    }

    @Bean
    public NonAutoRegisteredJob nonAutoRegisteredJob() {
        return new NonAutoRegisteredJob();
    }

    @Autowired
    @Bean
    public ScheduledJobExecutor scheduledJobExecutor(ScheduledJobLogRepository jobLogRepo) {
        return new ScheduledJobExecutor(jobLogRepo, Clock.systemDefaultZone());
    }

}
