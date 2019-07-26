package de.adesso.projectboard.base.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@ConfigurationProperties(prefix = "projectboard")
@Data
@Validated
public class ProjectBoardConfigurationProperties {

    /**
     * The delay between every project refresh.
     */
    @Min(1L)
    private long refreshInterval = 30L;

    /**
     * The name of the request parameter to get the
     * desired projection's name of.
     */
    @NotEmpty
    private String projectionNameRequestParameter = "projection";

    /**
     * The URL of the project board. Used to set the allowed origins
     * when the prod profile is activated.
     */
    @NotEmpty
    private String url = "http://localhost:4200/";

    /**
     * The list of all status of a project that require the user to be
     * in the same lob or be a manager to access it in <b>lower case</b>. All
     * status not contained in this list are implicitly accessible by every
     * user.
     *
     * Defaults to {@code offen} and {@code open}.
     */
    private List<String> lobDependentStatus = List.of("offen", "open");

    /**
     * The list off all status of a project that are excluded from the list of
     * projects seen by the users in <b>lower case</b>.
     *
     * Defaults to {@code abgeschlossen}, {@code closed}, {@code angeboten} and {@code offered}.
     */
    private List<String> statusExcludedFromList = List.of("abgeschlossen", "closed", "angeboten", "offered");

    /**
     * The list of all status of a project that prevent users from applying to
     * the project in <b>lower case</b>.
     *
     * Defaults to {@code abgeschlossen} and {@code closed}.
     */
    private List<String> applicationsForbiddenStatus = List.of("abgeschlossen", "closed");

}
