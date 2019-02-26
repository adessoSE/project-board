package de.adesso.projectboard.base.access.handler;

import de.adesso.projectboard.base.access.persistence.AccessInterval;
import de.adesso.projectboard.base.user.persistence.User;
import lombok.extern.slf4j.Slf4j;

import java.time.format.DateTimeFormatter;

/**
 * {@link UserAccessEventHandler} implementation that performs simple
 * logging.
 *
 * <p>
 *      Auto-configured implementation in case no other {@link UserAccessEventHandler} bean
 *      is present.
 * </p>
 */
@Slf4j
public class LogUserAccessEventHandler implements UserAccessEventHandler {

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyy HH:mm");

    @Override
    public void onAccessCreated(User user, AccessInterval accessInterval) {
        log.info(String.format("Activated access for user %s until %s!",
                user.getId(),
                accessInterval.getEndTime().format(dateTimeFormatter)));
    }

    @Override
    public void onAccessChanged(User user, AccessInterval accessInterval) {
        log.info(String.format("Access changed for user %s to end on %s!",
                user.getId(),
                accessInterval.getEndTime().format(dateTimeFormatter)));
    }

    @Override
    public void onAccessRevoked(User user) {
        log.info(String.format("Access revoked for user %s!",
                user.getId()));
    }

}
