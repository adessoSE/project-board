package de.adesso.projectboard.core.project;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import de.adesso.projectboard.core.project.persistence.Project;
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
    private Project project;

    public Project getProjectWithIdAndKey() {
        project.setId(Long.parseLong(id));
        project.setKey(key);

        return project;
    }

}
