package de.adesso.projectboard.ad.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;

@ConfigurationProperties(prefix = "projectboard.ldap")
@Validated
@Data
public class LdapConfigurationProperties {

    /**
     * Whether or not to enable the LDAP/AD
     * related functionality. Enabled by default.
     */
    private boolean enabled = true;

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

    /**
     * The hour of the day the users should be updated in
     * a 24 hour format.
     *
     * default: <i>4</i> ['o clock]
     */
    @Min(0L)
    @Max(23L)
    private long updateHour = 4;

}
