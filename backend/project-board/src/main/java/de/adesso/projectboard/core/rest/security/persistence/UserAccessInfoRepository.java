package de.adesso.projectboard.core.rest.security.persistence;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

/**
 * {@link CrudRepository} to persist {@link UserAccessInfo} entities.
 *
 * @see UserAccessInfo
 */
public interface UserAccessInfoRepository extends CrudRepository<UserAccessInfo, Long> {

    Optional<UserAccessInfo> findFirstByUserIdOrderByAccessEndDesc(String userId);

}
