package de.adesso.projectboard.ad.access;

import de.adesso.projectboard.ad.user.RepositoryUserService;
import de.adesso.projectboard.base.access.persistence.AccessInfo;
import de.adesso.projectboard.base.access.persistence.AccessInfoRepository;
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

    private final AccessInfoRepository infoRepo;

    private final Clock clock;

    @Autowired
    public RepositoryUserAccessService(RepositoryUserService userService, AccessInfoRepository infoRepo) {
        this.userService = userService;
        this.infoRepo = infoRepo;

        this.clock = Clock.systemDefaultZone();
    }

    /**
     * Package private constructor for testing purposes.
     *
     * @param userService
     *          The {@link RepositoryUserService}.
     *
     * @param infoRepo
     *          The {@link AccessInfoRepository}.
     *
     * @param clock
     *          The {@link Clock} to get the current time from
     *          when using {@link LocalDateTime#now(Clock)}.
     */
    RepositoryUserAccessService(RepositoryUserService userService, AccessInfoRepository infoRepo, Clock clock) {
        this.userService = userService;
        this.infoRepo = infoRepo;
        this.clock = clock;
    }

    @Override
    public User giveUserAccessUntil(User user, LocalDateTime until) throws IllegalArgumentException {
        if(until.isBefore(LocalDateTime.now(clock))) {
            throw new IllegalArgumentException("End date must lie in the future!");
        }

        Optional<AccessInfo> latestInfoOptional = user.getLatestAccessInfo();

        if(!latestInfoOptional.isPresent() || !userHasActiveAccessInfo(user)) {
            AccessInfo info = new AccessInfo(user, LocalDateTime.now(clock), until);
            user.addAccessInfo(info);

            return userService.save(user);
        } else {
            AccessInfo latestInfo = latestInfoOptional.get();

            latestInfo.setAccessEnd(until);

            infoRepo.save(latestInfo);
        }

        return user;
    }

    @Override
    public User removeAccessFromUser(User user) {
        if(userHasActiveAccessInfo(user)) {
            AccessInfo latestInfo = user.getLatestAccessInfo()
                    .orElseThrow(() -> new IllegalStateException("No info instance present!"));

            latestInfo.setAccessEnd(LocalDateTime.now(clock));

            infoRepo.save(latestInfo);
        }

        return user;
    }

    @Override
    public boolean userHasActiveAccessInfo(User user) {
        Optional<AccessInfo> latestInfoOptional = user.getLatestAccessInfo();

        if(latestInfoOptional.isPresent()) {
            AccessInfo latestInfo = latestInfoOptional.get();

            LocalDateTime startTime = latestInfo.getAccessStart();
            LocalDateTime endTime = latestInfo.getAccessEnd();
            LocalDateTime now = LocalDateTime.now(clock);

            return ((startTime.isEqual(now) || startTime.isBefore(now)) && endTime.isAfter(now));
        }

        return false;
    }

}
