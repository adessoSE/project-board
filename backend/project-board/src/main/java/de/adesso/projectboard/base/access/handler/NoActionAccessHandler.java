package de.adesso.projectboard.base.access.handler;

import de.adesso.projectboard.base.user.persistence.User;
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
        logger.debug(String.format("Access granted for %s!", user.getId()));
    }

}
