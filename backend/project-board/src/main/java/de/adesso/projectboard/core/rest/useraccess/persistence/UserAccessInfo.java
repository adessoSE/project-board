package de.adesso.projectboard.core.rest.useraccess.persistence;

import de.adesso.projectboard.core.base.rest.user.persistence.User;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity to persist info about user access to projects/applications.
 *
 * @see UserAccessInfoRepository
 * @see de.adesso.projectboard.core.rest.security.UserAccessExpressionEvaluator
 */
@Table(name = "USER_ACCESS_INFO")
@Entity
@Getter
@Setter
public class UserAccessInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private User user;

    @Column(nullable = false)
    private LocalDateTime accessStart;

    @Column(nullable = false)
    private LocalDateTime accessEnd;

    /**
     * Constructs a new entity. The {@link #accessStart access start} is automatically
     * set to the current time when persisting the entity.
     *
     * @param user
     *          The {@link User} the access is given to.
     *
     * @param accessEnd
     *          The {@link LocalDateTime} of when the access should end.
     */
    public UserAccessInfo(User user, LocalDateTime accessEnd) {
        this.user = user;
        this.accessEnd = accessEnd;
    }

    protected UserAccessInfo() {
        // protected no-arg constructor for JPA
    }

    /**
     * Set the access start date to the current
     * {@link LocalDateTime} when persisting the entity.
     */
    @PrePersist
    public void setAccessStart() {
        this.accessStart = LocalDateTime.now();
    }

}
