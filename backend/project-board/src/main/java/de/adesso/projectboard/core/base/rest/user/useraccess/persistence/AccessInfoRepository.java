package de.adesso.projectboard.core.base.rest.user.useraccess.persistence;

import org.springframework.data.repository.CrudRepository;

/**
 * {@link CrudRepository} to persist {@link AccessInfo} objects.
 *
 * @see AccessInfo
 */
public interface AccessInfoRepository extends CrudRepository<AccessInfo, Long> {


}
