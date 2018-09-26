package de.adesso.projectboard.core.base.rest.user.useraccess.persistence;

import de.adesso.projectboard.core.base.rest.user.persistence.User;
import de.adesso.projectboard.core.rest.security.UserAccessExpressionEvaluator;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity to persist info about user access to projects/applications. Used by the
 * {@link UserAccessExpressionEvaluator} to authorize REST interface method invocations.
 *
 * @see User
 */
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
     * set to the current time.
     *
     * @param user
     *          The {@link User} the access is given to.
     *
     * @param accessEnd
     *          The {@link LocalDateTime} of when the access should end.
     */
    public UserAccessInfo(User user, LocalDateTime accessEnd) {
        this.user = user;
        this.accessStart = LocalDateTime.now();
        this.accessEnd = accessEnd;
    }

    protected UserAccessInfo() {
        // protected no-arg constructor for JPA
    }

    /**
     *
     * @return
     *          {@code true} if the {@link #accessStart} is equal to or before the
     *          {@link LocalDateTime#now() current date} and the {@link #accessEnd} is after the
     *          {@link LocalDateTime#now() current date}.
     */
    public boolean isCurrentlyActive() {
        LocalDateTime now = LocalDateTime.now();

        return (now.equals(accessStart) || now.isAfter(accessStart)) && now.isBefore(accessEnd);
    }

}
