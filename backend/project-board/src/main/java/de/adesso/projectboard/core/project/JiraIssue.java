package de.adesso.projectboard.core.project;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import de.adesso.projectboard.core.project.persistence.JiraProject;
import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class JiraIssue {

    @JsonAlias("id")
    private String id;

    private String key;

    @JsonAlias("fields")
    private JiraProject project;

    public JiraProject getProjectWithIdAndKey() {
        project.setId(Long.parseLong(id));
        project.setKey(key);

        return project;
    }

}
