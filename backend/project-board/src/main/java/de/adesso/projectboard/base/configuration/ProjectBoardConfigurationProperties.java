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
     * in the same lob or be a manager.
     *
     * Defaults to {@code offen} and {@code open}.
     */
    private List<String> lobDependentStatus = List.of("offen", "open");

    /**
     * The list of of all status of a project that don't require the
     * user to be in the same LoB or be a manager. Cannot be empty!
     *
     * Defaults to {@code eskaliert} and {@code escalated}.
     */
    @NotEmpty
    private List<String> lobIndependentStatus = List.of("eskaliert", "escalated");

}
