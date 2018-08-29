package de.adesso.projectboard.core.rest.security.persistence;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * Entity to persist info about user access to projects.
 *
 * @see UserProjectsAccessInfoRepository
 */
@Table
@Entity
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserProjectsAccessInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty
    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private LocalDateTime accessStart;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime accessEnd;

    /**
     * Constructs a new entity. The {@link #accessStart} is automatically
     * set to the current time when persisting the entity.
     *
     * @param userId
     *          The id of the user.
     *
     * @param accessEnd
     *          The {@link LocalDateTime} of when the access should end.
     */
    public UserProjectsAccessInfo(String userId, LocalDateTime accessEnd) {
        this.userId = userId;
        this.accessEnd = accessEnd;
    }

    protected UserProjectsAccessInfo() {
        // protected no-arg constructor for JPA
    }

    @PrePersist
    public void setAccessStart() {
        this.accessStart = LocalDateTime.now();
    }

}
