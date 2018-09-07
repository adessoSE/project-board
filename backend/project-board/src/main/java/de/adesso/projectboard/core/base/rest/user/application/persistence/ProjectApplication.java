package de.adesso.projectboard.core.base.rest.user.application.persistence;

import de.adesso.projectboard.core.base.rest.project.persistence.AbstractProject;
import de.adesso.projectboard.core.base.rest.user.persistence.User;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity to persist project application logs.
 */
@Table
@Entity
@Data
@NoArgsConstructor
public class ProjectApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The project the user applied for.
     */
    @ManyToOne(optional = false)
    private AbstractProject project;

    /**
     * The user this application belongs to.
     */
    @ManyToOne(optional = false)
    private User user;

    /**
     * The comment of the application.
     */
    private String comment;

    /**
     * The date of the application.
     */
    @Column(nullable = false)
    private LocalDateTime applicationDate;

    /**
     * Constructs a new instance. The {@link #applicationDate} is set to the
     * current {@link LocalDateTime} when persisting the entity.
     *
     * @param project
     *          The {@link AbstractProject} the user applied for.
     *
     * @param comment
     *          The comment of the application.
     *
     * @param user
     *          The {@link User} this project belongs to.
     */
    public ProjectApplication(AbstractProject project, String comment, User user) {
        this.project = project;
        this.comment = comment;
        this.user = user;
    }

    @PrePersist
    private void setApplicationDate() {
        this.applicationDate = LocalDateTime.now();
    }

}
