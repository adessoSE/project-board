package de.adesso.projectboard.core.base.rest.user.persistence;

import de.adesso.projectboard.core.base.rest.application.persistence.ProjectApplication;
import de.adesso.projectboard.core.base.rest.bookmark.persistence.ProjectBookmark;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Entity to persist information about users.
 *
 * @see ProjectBookmark
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
     * The {@link ProjectBookmark bookmarks} of the
     * user.
     */
    @OneToMany(cascade = CascadeType.ALL)
    Set<ProjectBookmark> bookmarks;

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
     * @param bookmark
     *          The {@link ProjectBookmark} to add to this user.
     *
     * @see #addApplication(ProjectApplication)
     */
    public void addBookmark(ProjectBookmark bookmark) {
        this.bookmarks.add(bookmark);
    }

    /**
     *
     * @param application
     *          The {@link ProjectApplication} to add to this user.
     *
     * @see #addBookmark(ProjectBookmark)
     */
    public void addApplication(ProjectApplication application) {
        this.applications.add(application);
    }

    /**
     *
     * @param bookmark
     *          The {@link ProjectBookmark} to remove.
     *
     * @return
     *          The result of {@link Set#remove(Object)}.
     */
    public boolean removeBookmark(ProjectBookmark bookmark) {
        return this.bookmarks.remove(bookmark);
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
