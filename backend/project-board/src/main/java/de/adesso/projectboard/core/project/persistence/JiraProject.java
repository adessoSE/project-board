package de.adesso.projectboard.core.project.persistence;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import de.adesso.projectboard.core.base.project.persistence.AbstractProject;
import de.adesso.projectboard.core.project.deserializer.date.CreatedUpdatedDateDeserializer;
import de.adesso.projectboard.core.project.deserializer.field.ObjectNameDeserializer;
import de.adesso.projectboard.core.project.deserializer.field.ObjectValueDeserializer;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * A {@link AbstractProject} that is used by the {@link de.adesso.projectboard.core.reader.JiraProjectReader}.
 *
 * @see de.adesso.projectboard.core.reader.JiraProjectReader
 */
@Entity
@Table(name = "JIRA_PROJECT")
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class JiraProject extends AbstractProject {

    @JsonDeserialize(using = ObjectNameDeserializer.class)
    private String status;

    @JsonDeserialize(using = ObjectNameDeserializer.class)
    private String issuetype;

    private String key;

    @JsonAlias("summary")
    private String title;

    @Lob
    @Column(length = 4096)
    @JsonAlias("customfield_10288")
    private String exercise;

    @Lob
    @Column(length = 4096)
    @JsonAlias("customfield_10296")
    private String skills;

    @JsonDeserialize(using = ObjectValueDeserializer.class)
    @JsonAlias("customfield_10292")
    private String lob;

    @JsonAlias("customfield_10279")
    private String customer;

    @JsonAlias("customfield_10297")
    private String location;

    @JsonAlias("customfield_10293")
    private String operationStart;

    @JsonAlias("customfield_10294")
    private String operationEnd;

    @JsonAlias("customfield_10284")
    private String work;

    @JsonDeserialize(using = CreatedUpdatedDateDeserializer.class)
    private LocalDateTime created;

    @JsonDeserialize(using = CreatedUpdatedDateDeserializer.class)
    private LocalDateTime updated;

    @JsonDeserialize(using = ObjectValueDeserializer.class)
    @JsonAlias("customfield_10290")
    private String freelancer;

}
