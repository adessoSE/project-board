package de.adesso.projectboard.base.scheduled.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * {@link JpaRepository} to persist {@link ScheduledJobLog}s.
 */
public interface ScheduledJobLogRepository extends JpaRepository<ScheduledJobLog, Long> {

    Optional<ScheduledJobLog> findFirstByJobIdentifierAndStatusOrderByTimeDesc(String jobIdentifier, ScheduledJobLog.Status status);

}
