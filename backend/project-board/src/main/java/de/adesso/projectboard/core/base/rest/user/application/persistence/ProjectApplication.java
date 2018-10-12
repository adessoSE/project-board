package de.adesso.projectboard.core.base.rest.user.application.persistence;

import de.adesso.projectboard.core.base.rest.project.persistence.Project;
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
    private Project project;

    /**
     * The user this application belongs to.
     */
    @ManyToOne(
            cascade = {CascadeType.REFRESH, CascadeType.DETACH, CascadeType.MERGE},
            optional = false
    )
    private User user;

    /**
     * The comment of the application.
     */
    @Column(length = 4096)
    @Lob
    private String comment;

    /**
     * The date of the application.
     */
    @Column(nullable = false)
    private LocalDateTime applicationDate;

    /**
     * Constructs a new instance. Adds the application to the user's
     * {@link User#applications}.
     *
     * <p>
     *      <b>Note:</b> The {@link #applicationDate} is set to the
     *      current {@link LocalDateTime} when persisting the entity.
     * </p>
     *
     * @param project
     *          The {@link Project} the user applied for.
     *
     * @param comment
     *          The comment of the application.
     *
     * @param user
     *          The {@link User} this project belongs to.
     */
    public ProjectApplication(Project project, String comment, User user) {
        this.project = project;
        this.comment = comment;
        this.user = user;

        user.addApplication(this);
    }

    @PrePersist
    private void setApplicationDateToNow() {
        this.applicationDate = LocalDateTime.now();
    }

}
