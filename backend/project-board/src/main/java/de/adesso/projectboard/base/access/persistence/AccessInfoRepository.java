package de.adesso.projectboard.base.access.persistence;

import org.springframework.data.repository.CrudRepository;

/**
 * {@link CrudRepository} to persist {@link AccessInfo} objects.
 *
 * @see AccessInfo
 */
public interface AccessInfoRepository extends CrudRepository<AccessInfo, Long> {


}
