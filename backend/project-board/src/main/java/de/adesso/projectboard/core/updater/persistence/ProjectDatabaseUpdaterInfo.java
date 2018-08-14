package de.adesso.projectboard.core.updater.persistence;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
public class ProjectDatabaseUpdaterInfo {

    @Id
    @GeneratedValue
    private long id;

    private LocalDateTime time;

    private Success success;

    public static enum Success {
        SUCCESS,
        FAILURE;
    }

}
