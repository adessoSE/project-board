package de.adesso.projectboard.ad.updater.persistence;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "USER_UPDATE_JOB")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateJob {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @NotNull
    @Column(name = "UPDATE_TIME")
    LocalDateTime updateTime;

    @Column(name = "SUCCESS")
    boolean success;

    public UserUpdateJob(@NonNull LocalDateTime updateTime, boolean success) {
        this.updateTime = updateTime;
        this.success = success;
    }

}
