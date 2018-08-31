package de.adesso.projectboard.core.base.rest.application.persistence;

import de.adesso.projectboard.core.base.project.persistence.AbstractProject;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity to persist project application logs.
 */
@Table(name = "PROJECT_APPLICATION_LOG")
@Entity
@Getter
@Setter
@NoArgsConstructor
public class ProjectApplicationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The ID of the user that applied for the project.
     */
    @Column(nullable = false)
    private String userId;

    /**
     * The project the user applied for.
     */
    @ManyToOne(optional = false)
    private AbstractProject project;

    /**
     * The comment of the application.
     */
    private String applicationComment;

    /**
     * The date of the application.
     */
    @Column(nullable = false)
    private LocalDateTime applicationDate;

    /**
     * Constructs a new instance. The {@link #applicationDate} is set to the
     * current {@link LocalDateTime} when persisting the entity.
     *
     * @param userId
     *          The ID of the user that applied for the project.
     *
     * @param applicationComment
     *          The comment oof the application.
     *
     * @param project
     *          The {@link AbstractProject} the user applied for.
     */
    public ProjectApplicationLog(String userId, String applicationComment, AbstractProject project) {
        this.userId = userId;
        this.applicationComment = applicationComment;
        this.project = project;
    }

    @PrePersist
    private void setApplicationDate() {
        applicationDate = LocalDateTime.now();
    }

}
