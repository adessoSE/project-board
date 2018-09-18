package de.adesso.projectboard.core.base.rest.user.application;

import de.adesso.projectboard.core.base.rest.user.application.persistence.ProjectApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link ProjectApplicationHandler} implementation that performs no action.
 *
 * @see ProjectApplicationHandler
 * @see de.adesso.projectboard.core.rest.handler.application.ProjectBoardApplicationHandler
 */
public class NoActionApplicationHandler implements ProjectApplicationHandler {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void onApplicationReceived(ProjectApplication application) throws RuntimeException {
        logger.debug(String.format("Application received! (User: %s)", application.getUser().getFullName()));
    }

}
