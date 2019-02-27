package de.adesso.projectboard.ad.access;

import de.adesso.projectboard.ad.user.RepositoryUserService;
import de.adesso.projectboard.base.access.handler.UserAccessEventHandler;
import de.adesso.projectboard.base.access.persistence.AccessInterval;
import de.adesso.projectboard.base.access.persistence.AccessIntervalRepository;
import de.adesso.projectboard.base.access.service.UserAccessService;
import de.adesso.projectboard.base.user.persistence.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;

@Profile("adesso-ad")
@Service
public class RepositoryUserAccessService implements UserAccessService {

    private final RepositoryUserService userService;

    private final AccessIntervalRepository intervalRepo;

    private final UserAccessEventHandler userAccessEventHandler;

    private final Clock clock;

    @Autowired
    public RepositoryUserAccessService(RepositoryUserService userService,
                                       AccessIntervalRepository intervalRepo,
                                       UserAccessEventHandler userAccessEventHandler,
                                       Clock clock) {
        this.userService = userService;
        this.intervalRepo = intervalRepo;
        this.userAccessEventHandler = userAccessEventHandler;
        this.clock = clock;
    }

    @Override
    public User giveUserAccessUntil(User user, LocalDateTime until) throws IllegalArgumentException {
        if(until.isBefore(LocalDateTime.now(clock))) {
            throw new IllegalArgumentException("End date must lie in the future!");
        }

        var latestIntervalOptional = user.getLatestAccessInterval();

        if(!latestIntervalOptional.isPresent() || !userHasActiveAccessInterval(user)) {
            var interval = new AccessInterval(user, LocalDateTime.now(clock), until);
            user.addAccessInterval(interval);

            userAccessEventHandler.onAccessCreated(user, interval);

            return userService.save(user);
        } else {
            var latestInterval = latestIntervalOptional.get();
            var oldEndTime = latestInterval.getEndTime();
            latestInterval.setEndTime(until);
            intervalRepo.save(latestInterval);

            userAccessEventHandler.onAccessChanged(user, latestInterval, oldEndTime);
        }

        return user;
    }

    @Override
    public User removeAccessFromUser(User user) {
        if(userHasActiveAccessInterval(user)) {
            var latestInterval = user.getLatestAccessInterval()
                    .orElseThrow(() -> new IllegalStateException("No interval instance present!"));
            var previousEndTime = latestInterval.getEndTime();
            latestInterval.setEndTime(LocalDateTime.now(clock));
            intervalRepo.save(latestInterval);

            userAccessEventHandler.onAccessRevoked(user, previousEndTime);
        }

        return user;
    }

    @Override
    public boolean userHasActiveAccessInterval(User user) {
        var latestIntervalOptional = user.getLatestAccessInterval();

        if(latestIntervalOptional.isPresent()) {
            var latestInterval = latestIntervalOptional.get();

            var startTime = latestInterval.getStartTime();
            var endTime = latestInterval.getEndTime();
            var now = LocalDateTime.now(clock);

            return ((startTime.isEqual(now) || startTime.isBefore(now)) && endTime.isAfter(now));
        }

        return false;
    }

}
