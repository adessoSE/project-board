package de.adesso.projectboard.core.base.updater.persistence;

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
@Table(name = "PROJECT_DATABASE_UPDATER_INFO")
@Entity
@Getter
@Setter
public class ProjectDatabaseUpdaterInfo {

    @Id
    @GeneratedValue
    private long id;

    @Column(name = "time", nullable = false)
    private LocalDateTime time;

    @Column(name = "status", nullable = false)
    private Status status;

    protected ProjectDatabaseUpdaterInfo() {
        // protected no-arg constructor for JPA
    }

    public ProjectDatabaseUpdaterInfo(LocalDateTime time, Status status) {
        this.time = time;
        this.status = status;
    }

    public enum Status {
        SUCCESS,
        FAILURE
    }

}
