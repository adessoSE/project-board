package de.adesso.projectboard.core.base.rest.project.persistence;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import de.adesso.projectboard.core.base.rest.project.deserializer.date.CreatedUpdatedDateDeserializer;
import de.adesso.projectboard.core.base.rest.project.deserializer.field.ObjectNameDeserializer;
import de.adesso.projectboard.core.base.rest.project.deserializer.field.ObjectValueDeserializer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Entity to persist project data.
 *
 * @see ProjectRepository
 */
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Project {

    @Id
    @GeneratedValue(
            generator = "project_id_generator"
    )
    @GenericGenerator(
            name = "project_id_generator",
            strategy = "de.adesso.projectboard.core.base.rest.project.persistence.ProjectIdGenerator",
            parameters = @org.hibernate.annotations.Parameter(name = "prefix", value = "AD-")
    )
    private String id;

    @JsonDeserialize(using = ObjectNameDeserializer.class)
    private String status;

    @JsonDeserialize(using = ObjectNameDeserializer.class)
    private String issuetype;

    @JsonAlias("summary")
    private String title;

    @ElementCollection
    private List<String> labels;

    @Lob
    @Column(length = 8192)
    @JsonAlias("customfield_10288")
    private String job;

    @Lob
    @Column(length = 8192)
    @JsonAlias("customfield_10296")
    private String skills;

    @Lob
    @Column(length = 8192)
    private String description;

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
    private String effort;

    @JsonDeserialize(using = CreatedUpdatedDateDeserializer.class)
    private LocalDateTime created;

    @JsonDeserialize(using = CreatedUpdatedDateDeserializer.class)
    private LocalDateTime updated;

    @JsonDeserialize(using = ObjectValueDeserializer.class)
    @JsonAlias("customfield_10290")
    private String freelancer;

    @JsonDeserialize(using = ObjectValueDeserializer.class)
    @JsonAlias("customfield_10306")
    private String elongation;

    @Lob
    @Column(length = 8192)
    @JsonAlias("customfield_10304")
    private String other;

    @Column(nullable = false)
    private ProjectOrigin origin = ProjectOrigin.JIRA;

}
