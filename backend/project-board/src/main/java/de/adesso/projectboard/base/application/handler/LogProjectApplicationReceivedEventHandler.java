package de.adesso.projectboard.base.application.handler;

import de.adesso.projectboard.base.application.persistence.ProjectApplication;
import lombok.extern.slf4j.Slf4j;

/**
 * {@link ProjectApplicationReceivedEventHandler} implementation that performs simple
 * logging.
 *
 * <p>
 *     Auto-configured implementation in case no other {@link ProjectApplicationReceivedEventHandler} bean
 *     is present.
 * </p>
 */
@Slf4j
public class LogProjectApplicationReceivedEventHandler implements ProjectApplicationReceivedEventHandler {

    @Override
    public void onApplicationReceived(ProjectApplication application) {
        log.info(String.format("Application of user '%s' received!", application.getUser().getId()));
    }

}
