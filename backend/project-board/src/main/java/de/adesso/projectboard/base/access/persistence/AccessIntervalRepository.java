package de.adesso.projectboard.base.access.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * {@link JpaRepository} to persist {@link AccessInterval} objects.
 *
 * @see AccessInterval
 */
public interface AccessIntervalRepository extends JpaRepository<AccessInterval, Long> {
    @Query(value = "SELECT a FROM AccessInterval a WHERE a.endTime = (SELECT MAX(aa.endTime) FROM AccessInterval aa WHERE aa.user = a.user)")
    List<AccessInterval> findAllLatestIntervals();
}
