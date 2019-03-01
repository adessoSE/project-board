package de.adesso.projectboard.base.cleaner;

import de.adesso.projectboard.base.access.persistence.AccessInterval;
import de.adesso.projectboard.base.access.persistence.AccessIntervalRepository;
import de.adesso.projectboard.base.user.service.BookmarkService;
import de.adesso.projectboard.base.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

/**
 * The service to clean up old bookmarks and applications.
 */
@Service
@Slf4j
public class Cleaner {
    private final UserService userService;

    private final BookmarkService bookmarkService;

    private final AccessIntervalRepository air;

    private final Clock clock;

    @Autowired
    public Cleaner(UserService userService,
                   BookmarkService bookmarkService,
                   AccessIntervalRepository air,
                   Clock clock) {
        this.bookmarkService = bookmarkService;
        this.userService = userService;
        this.air = air;
        this.clock = clock;
    }

    /**
     * Cleans up old bookmarks and applications of deactivated users.
     */
    @Scheduled(fixedDelay = 86400000L)
    @Transactional
    public void removeOldBookmarksAndApplications() {
        List<AccessInterval> latestAccessIntervals = air.findAllLatestIntervals();
        for (AccessInterval interval : latestAccessIntervals) {
            if (interval.getEndTime().plusDays(28L).isBefore(LocalDateTime.now(clock))) {
                bookmarkService.removeAllBookmarksOfUser(interval.getUser());
                userService.removeAllApplicationsOfUser(interval.getUser());
                log.debug("deleted bookmarks and applications of user " + interval.getUser().getId());
            }
        }
    }
}
