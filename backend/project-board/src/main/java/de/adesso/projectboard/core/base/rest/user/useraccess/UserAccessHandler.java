package de.adesso.projectboard.core.base.rest.user.useraccess;

import de.adesso.projectboard.core.base.rest.user.UserAccessController;
import de.adesso.projectboard.core.base.rest.user.persistence.User;
import de.adesso.projectboard.core.base.rest.user.useraccess.dto.UserAccessInfoRequestDTO;

/**
 * Interface used by {@link UserAccessController} to handle
 * user access
 *
 * @see UserAccessController#createAccessForUser(UserAccessInfoRequestDTO, String)
 */
@FunctionalInterface
public interface UserAccessHandler {

    /**
     *
     * @param user
     *          The {@link User} who has been granted access.
     *
     */
    void onAccessGranted(User user);

}
