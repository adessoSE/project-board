package de.adesso.projectboard.base.user.persistence;

import de.adesso.projectboard.base.access.persistence.AccessInfo;
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
     * The user's {@link AccessInfo} objects to evaluate
     * access.
     */
    @OneToMany(
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            mappedBy = "user"
    )
    List<AccessInfo> accessInfoList;

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

        this.accessInfoList = new LinkedList<>();
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
     *          When the user the application {@link ProjectApplication#user belongs to}
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
     *          A {@link Optional} containing the latest {@link AccessInfo}
     *          instance or an empty one if none is present.
     */
    public Optional<AccessInfo> getLatestAccessInfo() {
        if(accessInfoList.size() > 0) {
            return Optional.of(accessInfoList.get(accessInfoList.size() - 1));
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
     * @param info
     *          The {@link AccessInfo} to add.
     *
     * @throws IllegalArgumentException
     *          When the given {@code info}'s {@link AccessInfo#user user}
     *          is not equal to {@code this} user.
     */
    public void addAccessInfo(AccessInfo info) throws IllegalArgumentException {
        Objects.requireNonNull(info);

        if(!this.equals(info.getUser())) {
            throw new IllegalArgumentException("Info instance belongs to a different or no user!");
        }

        accessInfoList.add(info);
    }

    /**
     *
     * @param info
     *          The {@link AccessInfo} to remove.
     *
     */
    public void removeAccessInfo(AccessInfo info) {
        accessInfoList.remove(info);
    }

}
