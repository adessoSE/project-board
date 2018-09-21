package de.adesso.projectboard.core.security;

import de.adesso.projectboard.core.base.rest.user.persistence.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Profile("adesso-keycloak")
@Service
public class UserCrawler {

    private final UserRepository userRepository;

    @Autowired
    public UserCrawler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // executed every day at 4am
    @Scheduled(cron = "0 4 * * * *")
    public void crawlUsers() {
        // TODO: implement to crawl users from AD and persist them in the database
    }

}
