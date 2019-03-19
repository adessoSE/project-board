package de.adesso.projectboard.base.cleaner;

import de.adesso.projectboard.base.access.persistence.AccessInterval;
import de.adesso.projectboard.base.access.persistence.AccessIntervalRepository;
import de.adesso.projectboard.base.scheduled.FixedHourScheduledJob;
import de.adesso.projectboard.base.user.service.BookmarkService;
import de.adesso.projectboard.base.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDateTime;

/**
 * {@link FixedHourScheduledJob} to remove all project applications and project bookmarks
 * of users whose last access ran out at least 28 days ago.
 */
@Component
@Slf4j
public class ApplicationsAndBookmarkCleaner extends FixedHourScheduledJob {

    private static final int EXECUTION_HOUR = 3;

    private final UserService userService;

    private final BookmarkService bookmarkService;

    private final AccessIntervalRepository accessIntervalRepo;

    private final Clock clock;

    @Autowired
    public ApplicationsAndBookmarkCleaner(UserService userService,
                                          BookmarkService bookmarkService,
                                          AccessIntervalRepository accessIntervalRepo,
                                          Clock clock) {
        super(clock, EXECUTION_HOUR);

        this.bookmarkService = bookmarkService;
        this.userService = userService;
        this.accessIntervalRepo = accessIntervalRepo;
        this.clock = clock;
    }

    /**
     * Removes all project applications and project bookmarks
     * of users whose last access ran out at least 28 days ago.
     */
    void removeOldBookmarksAndApplications() {
        var latestAccessIntervals = accessIntervalRepo.findAllLatestIntervals();

        for (AccessInterval interval : latestAccessIntervals) {
            if (interval.getEndTime().plusDays(28L).isBefore(LocalDateTime.now(clock))) {
                bookmarkService.removeAllBookmarksOfUser(interval.getUser());
                userService.removeAllApplicationsOfUser(interval.getUser());

                log.debug("deleted bookmarks and applications of user " + interval.getUser().getId());
            }
        }
    }

    @Override
    public void execute(LocalDateTime lastExecuteTime) {
        removeOldBookmarksAndApplications();
    }

    @Override
    public void execute() {
        removeOldBookmarksAndApplications();
    }

    @Override
    public String getJobIdentifier() {
        return "APPLICATION-AND-BOOKMARK-CLEANER";
    }

}
