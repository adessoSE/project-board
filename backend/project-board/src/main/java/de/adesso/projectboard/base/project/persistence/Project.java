package de.adesso.projectboard.base.project.persistence;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import de.adesso.projectboard.base.project.deserializer.date.CreatedUpdatedDateDeserializer;
import de.adesso.projectboard.base.project.deserializer.field.ObjectNameDeserializer;
import de.adesso.projectboard.base.project.deserializer.field.ObjectValueDeserializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Entity to persist project data.
 *
 * @see ProjectRepository
 */
@Indexed
@Entity
@Table(name = "PROJECT")
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
public class Project {

    @Id
    @GeneratedValue(
            generator = "project_id_generator"
    )
    @GenericGenerator(
            name = "project_id_generator",
            strategy = "de.adesso.projectboard.base.project.persistence.ProjectIdGenerator",
            parameters = @org.hibernate.annotations.Parameter(name = "prefix", value = "AD-")
    )
    String id;

    @Field
    @JsonDeserialize(using = ObjectNameDeserializer.class)
    String status;

    @Column(name = "ISSUE_TYPE")
    @JsonDeserialize(using = ObjectNameDeserializer.class)
    String issuetype;

    @Field
    @JsonAlias("summary")
    String title;

    @ElementCollection
    @CollectionTable(
            name = "PROJECT_LABELS",
            joinColumns = @JoinColumn(name = "PROJECT_ID")
    )
    @Column(name = "LABEL")
    List<String> labels;

    @Field
    @Lob
    @Column(length = 8192)
    @JsonAlias("customfield_10288")
    String job;

    @Field
    @Lob
    @Column(length = 8192)
    @JsonAlias("customfield_10296")
    String skills;

    @Field
    @Lob
    @Column(length = 8192)
    String description;

    @Field
    @Column(name = "LOB")
    @JsonDeserialize(using = ObjectValueDeserializer.class)
    @JsonAlias("customfield_10292")
    String lob;

    @Field
    @JsonAlias("customfield_10279")
    String customer;

    @Field
    @JsonAlias("customfield_10297")
    String location;

    @Field
    @Column(name = "OPERATION_START")
    @JsonAlias("customfield_10293")
    String operationStart;

    @Field
    @Column(name = "OPERATION_END")
    @JsonAlias("customfield_10294")
    String operationEnd;

    @Field
    @JsonAlias("customfield_10284")
    String effort;

    @JsonDeserialize(using = CreatedUpdatedDateDeserializer.class)
    LocalDateTime created;

    @JsonDeserialize(using = CreatedUpdatedDateDeserializer.class)
    LocalDateTime updated;

    @JsonDeserialize(using = ObjectValueDeserializer.class)
    @JsonAlias("customfield_10290")
    String freelancer;

    @JsonDeserialize(using = ObjectValueDeserializer.class)
    @JsonAlias("customfield_10306")
    String elongation;

    @Field
    @Lob
    @Column(length = 8192)
    @JsonAlias("customfield_10304")
    String other;

    @JsonAlias("customfield_10298")
    String dailyRate;

    @JsonDeserialize(using = ObjectValueDeserializer.class)
    @JsonAlias("customfield_10291")
    String travelCostsCompensated;

}
