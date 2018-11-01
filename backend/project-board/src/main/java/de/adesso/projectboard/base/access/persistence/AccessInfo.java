package de.adesso.projectboard.base.access.persistence;

import de.adesso.projectboard.base.user.persistence.User;
import de.adesso.projectboard.rest.security.UserAccessExpressionEvaluator;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDateTime;

/**
 * Entity to persist info about user access to projects/applications. Used by the
 * {@link UserAccessExpressionEvaluator} to authorize REST interface method invocations.
 *
 * @see User
 */
@Entity
@Getter
@Setter
public class AccessInfo {

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
     * set to the current {@link LocalDateTime}.
     *
     * <p>
     *     <b>Note</b>: The instance <b>is not</b> added to the
     *     {@code user}'s {@link User#getAccessInfoList() info list}.
     * </p>
     *
     * @param user
     *          The {@link User} the instance belongs to.
     *
     * @param accessEnd
     *          The {@link LocalDateTime} of when the access should end.
     *
     * @see AccessInfo#AccessInfo(User, LocalDateTime, LocalDateTime)
     */
    public AccessInfo(User user, LocalDateTime accessEnd) {
        this(user, LocalDateTime.now(), accessEnd);
    }

    /**
     * <p>
     *     <b>Note</b>: The instance <b>is not</b> added to the
     *     {@code user}'s {@link User#getAccessInfoList() info list}.
     * </p>
     *
     * @param user
     *          The {@link User} the access is given to.
     *
     * @param accessStart
     *          The {@link LocalDateTime} of when the access should begin.
     *
     * @param accessEnd
     *          The {@link LocalDateTime} of when the access should end.
     *
     * @throws IllegalArgumentException
     *          When the {@code accessEnd} {@link LocalDateTime#isBefore(ChronoLocalDateTime) is before}
     *          the {@code accessStart}.
     *
     */
    public AccessInfo(User user, LocalDateTime accessStart, LocalDateTime accessEnd) throws IllegalArgumentException {
        if(accessEnd.isBefore(accessStart)) {
            throw new IllegalArgumentException("The end time has to be after the start time!");
        }

        this.user = user;
        this.accessStart = accessStart;
        this.accessEnd = accessEnd;
    }

    protected AccessInfo() {
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
