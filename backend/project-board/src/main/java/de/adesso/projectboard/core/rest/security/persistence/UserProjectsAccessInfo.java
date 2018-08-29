package de.adesso.projectboard.core.rest.security.persistence;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
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
public class UserProjectsAccessInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private LocalDateTime accessStart;

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
