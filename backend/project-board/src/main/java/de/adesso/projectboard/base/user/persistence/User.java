package de.adesso.projectboard.base.user.persistence;

import de.adesso.projectboard.base.access.persistence.AccessInfo;
import de.adesso.projectboard.base.application.persistence.ProjectApplication;
import de.adesso.projectboard.base.project.persistence.Project;
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
public class User {

    /**
     * The <b>unique</b> ID (<i>e.g.</i> the username)
     * of the user.
     */
    @Id
    private String id;

    /**
     * The bookmarked {@link Project}s of the
     * user.
     */
    @ManyToMany
    @JoinTable(name = "PB_USER_BOOKMARKS")
    private Set<Project> bookmarks;

    /**
     * The {@link ProjectApplication applications} of the
     * user.
     */
    @OneToMany(
            cascade = CascadeType.ALL,
            mappedBy = "user"
    )
    private Set<ProjectApplication> applications;

    /**
     * The user's created {@link Project}s.
     */
    @OneToMany(cascade = CascadeType.ALL)
    private Set<Project> createdProjects;

    /**
     * The user's {@link AccessInfo} objects to evaluate
     * REST interface access.
     */
    @OneToMany(
            cascade = CascadeType.ALL,
            mappedBy = "user"
    )
    private List<AccessInfo> accessInfoList;

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
        this.createdProjects = new LinkedHashSet<>();
    }

    /**
     *
     * @param project
     *          The {@link Project} to add a bookmark for.
     *
     * @return
     *          The result of {@link Set#add(Object)}.
     *
     * @see #addApplication(ProjectApplication)
     */
    public boolean addBookmark(Project project) {
        return bookmarks.add(project);
    }

    /**
     *
     * @param application
     *          The {@link ProjectApplication} to add to this user.
     *
     * @return
     *          The result of {@link Set#add(Object)}.
     *
     * @throws IllegalArgumentException
     *          When the user the application {@link ProjectApplication#user belongs to}
     *          is not {@code this} user.
     *
     * @see #addBookmark(Project)
     */
    public boolean addApplication(ProjectApplication application) throws IllegalArgumentException {
        if(!this.equals(application.getUser())) {
            throw new IllegalArgumentException("The application belongs to another user!");
        }

        return applications.add(application);
    }

    /**
     *
     * @param project
     *          The {@link Project} to remove the bookmark for.
     *
     * @return
     *          The result of {@link Set#remove(Object)}.
     */
    public boolean removeBookmark(Project project) {
        return this.bookmarks.remove(project);
    }

    /**
     *
     * @param application
     *          The {@link ProjectApplication} to remove.
     *
     * @return
     *          The result of {@link Set#remove(Object)}.
     */
    public boolean removeApplication(ProjectApplication application) {
        return this.applications.remove(application);
    }

    /**
     *
     * @return
     *          The latest {@link AccessInfo} instance or {@code null}
     *          when none is present.
     */
    public AccessInfo getLatestAccessInfo() {
        if(accessInfoList.size() > 0) {
            return accessInfoList.get(accessInfoList.size() - 1);
        }

        return null;
    }

    /**
     *
     * @return
     *          A <i>unmodifiable</i> {@link Set} of the created
     *          {@link Project}s of the user.
     */
    public Set<Project> getCreatedProjects() {
        return Collections.unmodifiableSet(createdProjects);
    }

    /**
     *
     * @param project
     *          The {@link Project} to add to the created projects.
     *
     * @return
     *          {@code true}, when the {@code project} was added to the created
     *          projects.
     */
    public boolean addCreatedProject(Project project) {
        return createdProjects.add(project);
    }

    /**
     *
     * @param project
     *          The {@link Project} to remove from the created projects.
     *
     * @return
     *          {@code true}, when the {@code projects} was removed from the created
     *          projects.
     */
    public boolean removeCreatedProject(Project project) {
        return createdProjects.remove(project);
    }

}
