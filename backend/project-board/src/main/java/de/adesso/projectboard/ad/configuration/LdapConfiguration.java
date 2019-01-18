package de.adesso.projectboard.ad.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.ldap.core.ContextSource;
import org.springframework.ldap.core.LdapTemplate;

import java.time.Clock;

@Profile("adesso-ad")
@Configuration
public class LdapConfiguration {

    @Bean
    @Autowired
    public LdapTemplate ldapTemplate(ContextSource source) {
        return new LdapTemplate(source);
    }

    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }

}
