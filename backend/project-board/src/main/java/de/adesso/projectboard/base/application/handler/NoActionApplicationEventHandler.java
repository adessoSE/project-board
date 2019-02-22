package de.adesso.projectboard.base.application.handler;

import de.adesso.projectboard.base.application.persistence.ProjectApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link ProjectApplicationEventHandler} implementation that performs no action.
 *
 * @see ProjectApplicationEventHandler
 * @see de.adesso.projectboard.rest.handler.application.ProjectBoardApplicationEventHandler
 */
public class NoActionApplicationEventHandler implements ProjectApplicationEventHandler {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void onApplicationReceived(ProjectApplication application) {
        logger.debug(String.format("Application received! (User: %s)", application.getUser().getId()));
    }

}
