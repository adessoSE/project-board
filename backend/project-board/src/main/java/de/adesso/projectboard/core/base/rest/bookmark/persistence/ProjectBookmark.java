package de.adesso.projectboard.core.base.rest.bookmark.persistence;

import de.adesso.projectboard.core.base.project.persistence.AbstractProject;
import lombok.Data;

import javax.persistence.*;

@Entity
@Table
@Data
public class ProjectBookmark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private AbstractProject project;

}
