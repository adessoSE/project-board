package de.adesso.projectboard.ldap.configuration;

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
public class LdapConfigurationProperties {

    /**
     * The base path to begin searching at.
     */
    @NotEmpty
    private String ldapBase;

    /**
     * The AD attribute used as the user ID.
     *
     * default: <i>sAMAccountName</i>
     */
    @NotEmpty
    private String userIdAttribute = "sAMAccountName";

}
