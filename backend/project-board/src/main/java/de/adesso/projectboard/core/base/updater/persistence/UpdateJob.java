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
public class UpdateJob {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private LocalDateTime time;

    @Column(nullable = false)
    private Status status;

    @Lob
    @Column(length = 512)
    private String failureReason;

    protected UpdateJob() {
        // protected no-arg constructor for JPA
    }

    public UpdateJob(LocalDateTime time, Status status) {
        this.time = time;
        this.status = status;
    }

    public UpdateJob(LocalDateTime time, Status status, Exception exception) {
        this.time = time;
        this.status = status;
        this.failureReason = exception != null ? exception.getMessage() : null;

        if(failureReason != null && failureReason.length() > 512) {
            failureReason = failureReason.substring(0, 511);
        }
    }

    public enum Status {
        SUCCESS,
        FAILURE
    }

}
