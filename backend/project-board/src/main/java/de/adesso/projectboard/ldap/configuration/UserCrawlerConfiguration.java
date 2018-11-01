package de.adesso.projectboard.ldap.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.ldap.core.ContextSource;
import org.springframework.ldap.core.LdapTemplate;

@Profile("adesso-keycloak")
@Configuration
public class UserCrawlerConfiguration {

    @Bean
    @Autowired
    public LdapTemplate ldapTemplate(ContextSource source) {
        return new LdapTemplate(source);
    }

}
