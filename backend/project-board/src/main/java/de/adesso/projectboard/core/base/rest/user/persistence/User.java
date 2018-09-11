package de.adesso.projectboard.core.base.rest.user.persistence;

import de.adesso.projectboard.core.base.rest.user.application.persistence.ProjectApplication;
import de.adesso.projectboard.core.base.rest.project.persistence.AbstractProject;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Entity to persist information about users.
 *
 * @see AbstractProject
 * @see ProjectApplication
 */
@Entity
@Table
@Getter
@Setter
public class User {

    /**
     * The <b>unique</b> id (<i>e.g.</i> the username)
     * of the user.
     */
    @Id
    private String id;

    /**
     * The first name of the user.
     */
    private String firstName;

    /**
     * The last name of the user.
     */
    private String lastName;

    /**
     * The bookmarked {@link AbstractProject projects} of the
     * user.
     */
    @OneToMany(cascade = CascadeType.ALL)
    Set<AbstractProject> bookmarks;

    /**
     * The {@link ProjectApplication applications} of the
     * user.
     */
    @OneToMany(cascade = CascadeType.ALL)
    Set<ProjectApplication> applications;

    /**
     *
     * @param id
     *          The id of the user.
     *
     * @param firstName
     *          The first name of the user.
     *
     * @param lastName
     *          The last name of the user.
     */
    public User(String id, String firstName, String lastName) {
        this(id);

        this.firstName = firstName;
        this.lastName = lastName;
    }

    /**
     * Constructs a new instance. {@link #firstName First name} and
     * {@link #lastName last name} are not set!
     *
     * @param id
     *          The id of the user.
     */
    public User(String id) {
        this.id = id;

        this.applications = new LinkedHashSet<>();
        this.bookmarks = new LinkedHashSet<>();
    }

    protected User() {
        // protected no-arg constructor for JPA

        this.applications = new LinkedHashSet<>();
        this.bookmarks = new LinkedHashSet<>();
    }

    /**
     *
     * @param project
     *          The {@link AbstractProject} to add a bookmark for.
     *
     * @return
     *          The result of {@link Set#add(Object)}.
     *
     * @see #addApplication(ProjectApplication)
     */
    public boolean addBookmark(AbstractProject project) {
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
     * @see #addBookmark(AbstractProject)
     */
    public boolean addApplication(ProjectApplication application) {
        return applications.add(application);
    }

    /**
     *
     * @param project
     *          The {@link AbstractProject} to remove the bookmark for.
     *
     * @return
     *          The result of {@link Set#remove(Object)}.
     */
    public boolean removeBookmark(AbstractProject project) {
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

}
