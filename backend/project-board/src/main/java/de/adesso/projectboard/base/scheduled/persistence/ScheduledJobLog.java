package de.adesso.projectboard.base.scheduled.persistence;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "SCHEDULED_JOB_LOG")
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ScheduledJobLog {

    @Id
    @GeneratedValue
    Long id;

    @Column(
            name = "JOB_TIME",
            nullable = false
    )
    LocalDateTime time;

    @Column(
            name = "JOB_IDENTIFIER",
            nullable = false
    )
    String jobIdentifier;

    @Column(
            name = "JOB_STATUS",
            nullable = false
    )
    Status status;

    /**
     *
     * @param time
     *          The date and time the job was executed, not null.
     *
     * @param jobIdentifier
     *          The identifier to differentiate this form other job logs, not null.
     *
     * @param status
     *          The status, not null.
     */
    public ScheduledJobLog(@NonNull LocalDateTime time, @NonNull String jobIdentifier, @NonNull Status status) {
        this.time = time;
        this.jobIdentifier = jobIdentifier;
        this.status = status;
    }

    public enum Status {
        FAILURE,
        SUCCESS
    }

}
