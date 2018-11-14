package de.adesso.projectboard.ldap.access;

import de.adesso.projectboard.base.access.persistence.AccessInfo;
import de.adesso.projectboard.base.access.persistence.AccessInfoRepository;
import de.adesso.projectboard.base.access.service.UserAccessService;
import de.adesso.projectboard.base.user.persistence.User;
import de.adesso.projectboard.ldap.user.LdapUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Profile("adesso-ad")
@Service
public class LdapUserAccessService implements UserAccessService {

    private final LdapUserService userService;

    private final AccessInfoRepository infoRepo;

    @Autowired
    public LdapUserAccessService(LdapUserService userService, AccessInfoRepository infoRepo) {
        this.userService = userService;
        this.infoRepo = infoRepo;
    }

    @Override
    public User giveUserAccessUntil(User user, LocalDateTime until) throws IllegalArgumentException {
        if(until.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("End date must lie in the future!");
        }

        // check if a valid user instance was passed
        userService.validateExistence(user);

        AccessInfo latestInfo = user.getLatestAccessInfo();
        List<AccessInfo> infoList = user.getAccessInfoList();

        if(latestInfo == null || !latestInfo.isCurrentlyActive()) {
            AccessInfo info = new AccessInfo(user, LocalDateTime.now(), until);
            infoList.add(info);
            userService.save(user);
        } else {
            latestInfo.setAccessEnd(until);

            infoRepo.save(latestInfo);
        }

        return user;
    }

    @Override
    public User removeAccessFromUser(User user) {
        // check if a valid user instance was passed
        userService.validateExistence(user);

        AccessInfo latestInfo = user.getLatestAccessInfo();

        if(latestInfo != null && latestInfo.isCurrentlyActive()) {
            latestInfo.setAccessEnd(LocalDateTime.now());
            infoRepo.save(latestInfo);
        }

        return user;
    }

    @Override
    public boolean userHasAccess(User user) {
        AccessInfo latestInfo = user.getLatestAccessInfo();

        if(latestInfo != null) {
            return latestInfo.isCurrentlyActive();
        }

        return false;
    }

}
