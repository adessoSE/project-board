package de.adesso.projectboard.base.application.handler;

import de.adesso.projectboard.base.application.persistence.ProjectApplication;
import de.adesso.projectboard.base.user.persistence.User;
import lombok.extern.slf4j.Slf4j;

/**
 * {@link ProjectApplicationOfferedEventHandler} implementation that performs simple
 * logging.
 *
 * <p>
 *     Auto-configured implementation in case no other {@link ProjectApplicationOfferedEventHandler} bean
 *     is present.
 * </p>
 */
@Slf4j
public class LogProjectApplicationOfferedEventHandler implements ProjectApplicationOfferedEventHandler {

    @Override
    public void onApplicationOffered(User offeringUser, ProjectApplication projectApplication) {
        log.debug(String.format("Application with id '%d' was offered by '%s'", projectApplication.getId(), offeringUser.getId()));
    }

}
