package de.adesso.projectboard.core.base.updater.persistence;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * A info object for the {@link de.adesso.projectboard.core.base.updater.ProjectDatabaseUpdater}
 * to persist information about the project database updates.
 *
 * @see ProjectDatabaseUpdaterInfoRepository
 */
@Entity
@Table(name = "PROJECT_DATABASE_UPDATER_INFO")
@Getter
@Setter
@AllArgsConstructor
public class ProjectDatabaseUpdaterInfo {

    @Id
    @GeneratedValue
    private long id;

    @Column(nullable = false)
    private LocalDateTime time;

    @Column(nullable = false)
    private Status status;

    private String failureReason;

    protected ProjectDatabaseUpdaterInfo() {
        // protected no-arg constructor for JPA
    }

    public ProjectDatabaseUpdaterInfo(LocalDateTime time, Status status) {
        this.time = time;
        this.status = status;
    }

    public ProjectDatabaseUpdaterInfo(LocalDateTime time, Status status, Exception exception) {
        this.time = time;
        this.status = status;
        this.failureReason = exception != null ? exception.getMessage() : null;
    }

    public enum Status {
        SUCCESS,
        FAILURE
    }

}
