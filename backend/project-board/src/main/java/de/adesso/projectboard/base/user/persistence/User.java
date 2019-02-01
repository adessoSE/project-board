package de.adesso.projectboard.base.user.persistence;

import de.adesso.projectboard.base.access.persistence.AccessInterval;
import de.adesso.projectboard.base.application.persistence.ProjectApplication;
import de.adesso.projectboard.base.project.persistence.Project;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.*;

/**
 * Entity to persist information about users.
 *
 * @see Project
 * @see ProjectApplication
 */
@Entity
@Table(name = "PB_USER")
@Getter
@Setter
@EqualsAndHashCode
public class User {

    /**
     * The <b>unique</b> ID (<i>e.g.</i> the username)
     * of the user.
     */
    @Id
    String id;

    /**
     * The bookmarked {@link Project}s of the
     * user.
     */
    @ManyToMany
    @JoinTable(
            name = "PB_USER_BOOKMARKS",
            joinColumns = @JoinColumn(name = "USER_ID"),
            inverseJoinColumns = @JoinColumn(name = "PROJECT_ID")
    )
    Set<Project> bookmarks;

    /**
     * The {@link ProjectApplication applications} of the
     * user.
     */
    @OneToMany(
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            mappedBy = "user"
    )
    Set<ProjectApplication> applications;

    /**
     * The user's owned {@link Project}s.
     */
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "PB_USER_OWNED_PROJECTS",
            joinColumns = @JoinColumn(name = "USER_ID"),
            inverseJoinColumns = @JoinColumn(name = "PROJECT_ID")
    )
    Set<Project> ownedProjects;

    /**
     * The user's {@link AccessInterval} objects to evaluate
     * access.
     */
    @OneToMany(
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            mappedBy = "user"
    )
    List<AccessInterval> accessIntervals;

    /**
     *
     * @param userId
     *          The ID of the user.
     */
    public User(String userId) {
        this();

        this.id = userId;
    }

    protected User() {
        // protected no-arg constructor for JPA

        this.accessIntervals = new LinkedList<>();
        this.applications = new LinkedHashSet<>();
        this.bookmarks = new LinkedHashSet<>();
        this.ownedProjects = new LinkedHashSet<>();
    }

    /**
     *
     * @param project
     *          The {@link Project} to add a bookmark for.
     *
     * @see #addApplication(ProjectApplication)
     */
    public void addBookmark(Project project) {
        this.bookmarks.add(project);
    }

    /**
     *
     * @param project
     *          The {@link Project} to remove the bookmark for.
     *
     */
    public void removeBookmark(Project project) {
        this.bookmarks.remove(project);
    }

    /**
     *
     * @param application
     *          The {@link ProjectApplication} to add to this user.
     *
     * @throws IllegalArgumentException
     *          When the {@link ProjectApplication#getUser() user} the application belongs to
     *          is not {@code this} user.
     *
     * @see #addBookmark(Project)
     */
    public void addApplication(ProjectApplication application) throws IllegalArgumentException {
        if(!this.equals(application.getUser())) {
            throw new IllegalArgumentException("The application belongs to another user!");
        }

        applications.add(application);
    }

    /**
     *
     * @param application
     *          The {@link ProjectApplication} to remove.
     *
     */
    public void removeApplication(ProjectApplication application) {
        this.applications.remove(application);
    }

    /**
     *
     * @return
     *          A {@link Optional} containing the latest {@link AccessInterval}
     *          instance or an empty one if none is present.
     */
    public Optional<AccessInterval> getLatestAccessInterval() {
        if(accessIntervals.size() > 0) {
            return Optional.of(accessIntervals.get(accessIntervals.size() - 1));
        }

        return Optional.empty();
    }

    /**
     *
     * @param project
     *          The {@link Project} to add to the created projects.
     *
     */
    public void addOwnedProject(Project project) {
        ownedProjects.add(project);
    }

    /**
     *
     * @param project
     *          The {@link Project} to remove from the created projects.
     *
     */
    public void removeOwnedProject(Project project) {
        ownedProjects.remove(project);
    }

    /**
     *
     * @param interval
     *          The {@link AccessInterval} to add.
     *
     * @throws IllegalArgumentException
     *          When the given {@code interval}'s {@link AccessInterval#getUser() user}
     *          is not equal to {@code this} user.
     */
    public void addAccessInterval(AccessInterval interval) throws IllegalArgumentException {
        Objects.requireNonNull(interval);

        if(!this.equals(interval.getUser())) {
            throw new IllegalArgumentException("Interval instance belongs to a different or no user!");
        }

        accessIntervals.add(interval);
    }

    /**
     *
     * @param interval
     *          The {@link AccessInterval} to remove.
     *
     */
    public void removeAccessInterval(AccessInterval interval) {
        accessIntervals.remove(interval);
    }

}
