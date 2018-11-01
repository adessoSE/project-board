package de.adesso.projectboard.core.crawler.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;

@Profile("adesso-ad")
@ConfigurationProperties(prefix = "projectboard.ldap")
@Configuration
@Validated
@Getter
@Setter
public class CrawlerConfigurationProperties {

    /**
     * The base path to crawl users from.
     */
    @NotEmpty
    private String crawlBase;

}
