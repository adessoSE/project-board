package de.adesso.projectboard.core.reader;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class JiraServerInfo {

    private String version;

    private String serverTitle;

}
