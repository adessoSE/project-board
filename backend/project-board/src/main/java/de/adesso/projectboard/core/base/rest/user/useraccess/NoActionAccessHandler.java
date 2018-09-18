package de.adesso.projectboard.core.base.rest.user.useraccess;

import de.adesso.projectboard.core.base.rest.user.persistence.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link UserAccessHandler} implementation that performs no action.
 *
 * @see UserAccessHandler
 */
public class NoActionAccessHandler implements UserAccessHandler {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void onAccessGranted(User user) {
        logger.debug(String.format("Access granted for %s!", user.getFullName()));
    }

}
