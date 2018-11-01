package de.adesso.projectboard.base.application.handler;

import de.adesso.projectboard.base.application.persistence.ProjectApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link ProjectApplicationHandler} implementation that performs no action.
 *
 * @see ProjectApplicationHandler
 * @see de.adesso.projectboard.rest.handler.application.ProjectBoardApplicationHandler
 */
public class NoActionApplicationHandler implements ProjectApplicationHandler {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void onApplicationReceived(ProjectApplication application) throws RuntimeException {
        logger.debug(String.format("Application received! (User: %s)", application.getUser().getFullName()));
    }

}
