package de.adesso.projectboard.base.application.handler;

import de.adesso.projectboard.base.application.persistence.ProjectApplication;
import lombok.extern.slf4j.Slf4j;

/**
 * {@link ProjectApplicationEventHandler} implementation that performs simple
 * logging.
 *
 * <p>
 *     Auto-configured implementation in case no other {@link ProjectApplicationEventHandler} bean
 *     is present.
 * </p>
 */
@Slf4j
public class LogProjectApplicationEventHandler implements ProjectApplicationEventHandler {

    @Override
    public void onApplicationReceived(ProjectApplication application) {
        log.info(String.format("Application of user '%s' received!", application.getUser().getId()));
    }

}
