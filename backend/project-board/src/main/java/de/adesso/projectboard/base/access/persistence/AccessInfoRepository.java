package de.adesso.projectboard.base.access.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * {@link JpaRepository} to persist {@link AccessInfo} objects.
 *
 * @see AccessInfo
 */
public interface AccessInfoRepository extends JpaRepository<AccessInfo, Long> {

}
