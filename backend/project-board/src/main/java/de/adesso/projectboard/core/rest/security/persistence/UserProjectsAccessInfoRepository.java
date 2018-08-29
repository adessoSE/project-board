package de.adesso.projectboard.core.rest.security.persistence;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

/**
 * {@link CrudRepository} to persist {@link UserProjectsAccessInfo} entities.
 *
 * @see UserProjectsAccessInfo
 */
public interface UserProjectsAccessInfoRepository extends CrudRepository<UserProjectsAccessInfo, Long> {

    Optional<UserProjectsAccessInfo> findFirstByUserIdOrderByAccessEndDesc(String userId);

}
