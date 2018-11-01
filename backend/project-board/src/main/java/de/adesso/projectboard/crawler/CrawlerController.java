package de.adesso.projectboard.crawler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Profile("adesso-ad")
@RestController
@RequestMapping("/crawler")
public class CrawlerController {

    private final UserCrawler crawler;

    @Autowired
    public CrawlerController(UserCrawler crawler) {
        this.crawler = crawler;
    }

    @PreAuthorize("hasRole('admin')")
    @GetMapping(path = "/crawl")
    public void startCrawling() {
        crawler.crawlUsers();
    }

}
