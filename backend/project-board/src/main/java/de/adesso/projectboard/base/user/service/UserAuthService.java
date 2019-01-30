package de.adesso.projectboard.base.user.service;

import de.adesso.projectboard.base.user.persistence.User;

public interface UserAuthService {

    /**
     *
     * @return
     *          The user instance of the currently authenticated
     *          user.
     */
    User getAuthenticatedUser();

    /**
     *
     * @return
     *          The ID of the currently authenticated user.
     */
    String getAuthenticatedUserId();

    /**
     *
     * @return
     *          {@code true}, iff the currently authenticated user
     *          is a <i>admin</i>.
     */
    boolean authenticatedUserIsAdmin();

    /**
     *
     * @param user
     *          The user to check.
     *
     * @return
     *          {@code true}, iff the given {@code user} is <i>effectively</i>
     *          a manager.
     *
     * @see UserService#userIsManager(User)
     */
    boolean userIsEffectivelyAManager(User user);

}
