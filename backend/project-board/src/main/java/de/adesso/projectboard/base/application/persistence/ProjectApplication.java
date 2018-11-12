package de.adesso.projectboard.base.application.persistence;

import de.adesso.projectboard.base.project.persistence.Project;
import de.adesso.projectboard.base.user.persistence.User;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity to persist project application data.
 */
@Entity
@Table(name = "PROJECT_APPLICATION")
@Data
@NoArgsConstructor
public class ProjectApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    /**
     * The project the user applied for.
     */
    @ManyToOne
    @JoinColumn(
            name = "PROJECT_ID",
            nullable = false
    )
    Project project;

    /**
     * The user this application belongs to.
     */
    @ManyToOne
    @JoinColumn(
            name = "USER_ID",
            nullable = false
    )
    User user;

    /**
     * The comment of the application.
     */
    @Lob
    @Column(
            name = "APPLICATION_COMMENT",
            length = 4096
    )
    String comment;

    /**
     * The date of the application.
     */
    @Column(
            name = "APPLICATION_DATE",
            nullable = false
    )
    LocalDateTime applicationDate;

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
