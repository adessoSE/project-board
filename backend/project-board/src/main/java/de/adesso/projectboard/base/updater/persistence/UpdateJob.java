package de.adesso.projectboard.base.updater.persistence;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * A info object for the {@link de.adesso.projectboard.base.updater.ProjectDatabaseUpdater}
 * to persist information about the project database updates.
 *
 * @see UpdateJobRepository
 */
@Entity
@Table(name = "UPDATE_JOB")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UpdateJob {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(
            name = "UPDATE_TIME",
            nullable = false
    )
    LocalDateTime time;

    @Column(nullable = false)
    Status status;

    @Lob
    @Column(
            name = "FAILURE_REASON",
            length = 512
    )
    String failureReason;

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
