package de.adesso.projectboard.adapter.mail.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import java.util.HashMap;
import java.util.Map;

@ConfigurationProperties(prefix = "projectboard.mail")
@Data
@Validated
public class MailConfigurationProperties {

    /**
     * Whether or not to enable the mail feature.
     * Enabled by default.
     */
    private boolean enabled = true;

    /**
     * The SMTP host to connect to.
     */
    @NotEmpty
    private String host;

    /**
     * The SMTP port to connect to.
     */
    @Min(0)
    @Max(65535)
    private int port = 587;

    /**
     * The username to authenticate with.
     */
    private String username = "";

    /**
     * The password to authenticate with.
     */
    private String password = "";

    /**
     * Additional Java Mail Session properties.
     */
    private Map<String, String> properties = new HashMap<>();

    /**
     * The URL the project ID gets appended to to refer
     * to a specific project.
     */
    @NotEmpty
    private String referralBaseUrl = "http://localhost:4200/browse/";

}
