package de.adesso.projectboard.ad.access;

import de.adesso.projectboard.ad.user.RepositoryUserService;
import de.adesso.projectboard.base.access.persistence.AccessInterval;
import de.adesso.projectboard.base.access.persistence.AccessIntervalRepository;
import de.adesso.projectboard.base.access.service.UserAccessService;
import de.adesso.projectboard.base.user.persistence.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Optional;

@Profile("adesso-ad")
@Service
public class RepositoryUserAccessService implements UserAccessService {

    private final RepositoryUserService userService;

    private final AccessIntervalRepository intervalRepo;

    private final Clock clock;

    @Autowired
    public RepositoryUserAccessService(RepositoryUserService userService, AccessIntervalRepository intervalRepo, Clock clock) {
        this.userService = userService;
        this.intervalRepo = intervalRepo;
        this.clock = clock;
    }

    @Override
    public User giveUserAccessUntil(User user, LocalDateTime until) throws IllegalArgumentException {
        if(until.isBefore(LocalDateTime.now(clock))) {
            throw new IllegalArgumentException("End date must lie in the future!");
        }

        Optional<AccessInterval> latestIntervalOptional = user.getLatestAccessInterval();

        if(!latestIntervalOptional.isPresent() || !userHasActiveAccessInterval(user)) {
            AccessInterval interval = new AccessInterval(user, LocalDateTime.now(clock), until);
            user.addAccessInterval(interval);

            return userService.save(user);
        } else {
            AccessInterval latestInterval = latestIntervalOptional.get();

            latestInterval.setEndTime(until);

            intervalRepo.save(latestInterval);
        }

        return user;
    }

    @Override
    public User removeAccessFromUser(User user) {
        if(userHasActiveAccessInterval(user)) {
            AccessInterval latestInterval = user.getLatestAccessInterval()
                    .orElseThrow(() -> new IllegalStateException("No interval instance present!"));

            latestInterval.setEndTime(LocalDateTime.now(clock));

            intervalRepo.save(latestInterval);
        }

        return user;
    }

    @Override
    public boolean userHasActiveAccessInterval(User user) {
        Optional<AccessInterval> latestIntervalOptional = user.getLatestAccessInterval();

        if(latestIntervalOptional.isPresent()) {
            AccessInterval latestInterval = latestIntervalOptional.get();

            LocalDateTime startTime = latestInterval.getStartTime();
            LocalDateTime endTime = latestInterval.getEndTime();
            LocalDateTime now = LocalDateTime.now(clock);

            return ((startTime.isEqual(now) || startTime.isBefore(now)) && endTime.isAfter(now));
        }

        return false;
    }

}
