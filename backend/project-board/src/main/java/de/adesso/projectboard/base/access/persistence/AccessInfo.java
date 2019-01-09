package de.adesso.projectboard.base.access.persistence;

import de.adesso.projectboard.base.user.persistence.User;
import de.adesso.projectboard.rest.security.UserAccessExpressionEvaluator;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDateTime;
import java.util.Objects;

/**
 * Entity to persist info about user access to projects/applications. Used by the
 * {@link UserAccessExpressionEvaluator} to authorize REST interface method invocations.
 *
 * @see User
 */
@Entity
@Table(name = "ACCESS_INFO")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AccessInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(
            name = "USER_ID",
            nullable = false
    )
    User user;

    @Column(
            name = "START_TIME",
            nullable = false
    )
    LocalDateTime accessStart;

    @Column(
            name = "END_TIME",
            nullable = false
    )
    LocalDateTime accessEnd;

    /**
     * Constructs a new instance.
     *
     * <p>
     *     <b>Note</b>: The instance <b>is</b> added to the
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

        user.addAccessInfo(this);
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) {
            return true;
        }

        if(obj instanceof AccessInfo) {
            AccessInfo other = (AccessInfo) obj;

            // only compare the user IDs because of the cyclic reference: User <-> AccessInfo
            boolean userEquals;
            if(Objects.nonNull(this.user) && Objects.nonNull(other.user)) {
                userEquals = Objects.equals(this.user.getId(), other.user.getId());
            } else {
                userEquals = Objects.isNull(this.user) && Objects.isNull(other.user);
            }

            return userEquals &&
                    Objects.equals(this.id, other.id) &&
                    Objects.equals(this.accessStart, other.accessStart) &&
                    Objects.equals(this.accessEnd, other.accessEnd);
        }

        return false;
    }

    @Override
    public int hashCode() {
        int baseHash = Objects.hash(id, accessStart, accessEnd);
        int userIdHash = this.user != null ? Objects.hashCode(this.user.getId()) : 0;

        return baseHash + 31 * userIdHash;
    }
}
