package de.adesso.projectboard.base.application.persistence;

import de.adesso.projectboard.base.project.persistence.Project;
import de.adesso.projectboard.base.user.persistence.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entity to persist project application data.
 */
@Entity
@Table(name = "PROJECT_APPLICATION")
@Getter
@Setter
@NoArgsConstructor
public class ProjectApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    /**
     * The project the user applied for.
     */
    @ManyToOne
    @JoinColumn(
            name = "PROJECT_ID",
            nullable = false
    )
    Project project;

    /**
     * The user this application belongs to.
     */
    @ManyToOne
    @JoinColumn(
            name = "USER_ID",
            nullable = false
    )
    User user;

    /**
     * The comment of the application.
     */
    @Lob
    @Column(
            name = "APPLICATION_COMMENT",
            length = 4096
    )
    String comment;

    /**
     * The date of the application.
     */
    @Column(
            name = "APPLICATION_DATE",
            nullable = false
    )
    LocalDateTime applicationDate;

    /**
     * The state of the application.
     */
    @Column(
            name = "STATE",
            nullable = false
    )
    State state;

    /**
     * Constructs a new instance. Adds the application to the user's
     * {@link User#getApplications() applications}.
     *
     * <p>
     *      <b>Note:</b> The {@link #applicationDate} is set to the
     *      current {@link LocalDateTime} when persisting the entity.
     * </p>
     *
     * @param project
     *          The {@link Project} the user applied for.
     *
     * @param comment
     *          The comment of the application.
     *
     * @param user
     *          The {@link User} this project belongs to.
     *
     * @param applicationDate
     *          The {@link LocalDateTime date} of the application.
     */
    public ProjectApplication(Project project, String comment, User user, LocalDateTime applicationDate) {
        Objects.requireNonNull(user);

        this.project = project;
        this.comment = comment;
        this.user = user;
        this.applicationDate = applicationDate;
        this.state = State.NEW;

        user.addApplication(this);
    }

    /**
     * Compares the {@link #id}, {@link #comment}, {@link #project}
     * {@link #applicationDate application date} with {@link Objects#equals(Object, Object)}
     * and the {@link User#getId()}  ID}s of the {@link User}s of both instances.
     *
     * @param obj
     *          The instance to compare {@code this} instance
     *          with.
     *
     * @return
     *         {@code true}, iff all of the comparisons mentioned above
     *         evaluate to {@code true}.
     */
    @Override
    public boolean equals(Object obj) {
        if(this == obj) {
            return true;
        }

        if(obj instanceof ProjectApplication) {
            ProjectApplication other = (ProjectApplication) obj;

            // only compare the user IDs because of the cyclic reference: User <-> Application
            boolean userEquals = Objects.nonNull(this.user) && Objects.nonNull(other.user)
                    && Objects.equals(this.user.getId(), other.user.getId());

            return userEquals &&
                    Objects.equals(this.id, other.id) &&
                    Objects.equals(this.comment, other.comment) &&
                    Objects.equals(this.project, other.project) &&
                    Objects.equals(this.applicationDate, other.applicationDate);
        }

        return false;
    }

    @Override
    public int hashCode() {
        int baseHash = Objects.hash(this.id, this.comment, this.project, this.applicationDate);
        int userIdHash = this.user != null ? Objects.hashCode(this.user.getId()) : 0;

        return baseHash + 31 * userIdHash;
    }

    public enum State {
       NONE, NEW, DELETED, OFFERED;
    }

}
