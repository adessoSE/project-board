package de.adesso.projectboard.core.rest.useraccess.persistence;

import de.adesso.projectboard.core.base.rest.user.persistence.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

/**
 * {@link CrudRepository} to persist {@link UserAccessInfo} entities that contain
 * information about users.
 *
 * @see UserAccessInfo
 */
public interface UserAccessInfoRepository extends CrudRepository<UserAccessInfo, Long> {

    /**
     *
     * @param user
     *          The {@link User} to get the latest optional
     *          {@link UserAccessInfo} object for.
     *
     * @return
     *          The latest optional {@link UserAccessInfo}
     *          of the given {@link User}.
     *
     * @see #getLatestAccessInfo(User)
     */
    Optional<UserAccessInfo> findFirstByUserOrderByAccessEndDesc(User user);

    /**
     *
     * @param user
     *          The {@link User} to get the latest optional
     *          {@link UserAccessInfo} object for.
     *
     * @return
     *          The latest optional {@link UserAccessInfo}
     *          of the given {@link User}.
     *
     * @see #findFirstByUserOrderByAccessEndDesc(User)
     */
    default Optional<UserAccessInfo> getLatestAccessInfo(User user) {
        return findFirstByUserOrderByAccessEndDesc(user);
    }

}
