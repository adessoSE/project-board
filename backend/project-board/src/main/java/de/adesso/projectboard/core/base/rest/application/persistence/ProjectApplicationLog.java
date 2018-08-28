package de.adesso.projectboard.core.base.rest.application.persistence;

import de.adesso.projectboard.core.base.rest.application.ProjectApplication;
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
     * The ID of the project the user applied for.
     */
    @Column(nullable = false)
    private Long projectId;

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
     *
     * @param userId
     *          The ID of the user that applied for the project.
     *
     * @param projectApplication
     *          The {@link ProjectApplication} of the project the user applied for.
     */
    public ProjectApplicationLog(String userId, ProjectApplication projectApplication) {
        this.userId = userId;
        this.applicationComment = projectApplication != null ? projectApplication.getComment() : null;
        this.projectId = projectApplication != null ? projectApplication.getProjectId() : null;
    }

    @PrePersist
    private void setApplicationDate() {
        applicationDate = LocalDateTime.now();
    }

}
