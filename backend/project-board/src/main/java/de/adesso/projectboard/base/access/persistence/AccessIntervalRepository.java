package de.adesso.projectboard.base.access.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * {@link JpaRepository} to persist {@link AccessInterval} objects.
 *
 * @see AccessInterval
 */
public interface AccessIntervalRepository extends JpaRepository<AccessInterval, Long> {

}
