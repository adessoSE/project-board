package de.adesso.projectboard.core.base.rest.user.persistence;

import de.adesso.projectboard.core.base.rest.project.persistence.AbstractProject;
import de.adesso.projectboard.core.base.rest.user.application.persistence.ProjectApplication;
import de.adesso.projectboard.core.base.rest.user.useraccess.persistence.UserAccessInfo;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDateTime;
import java.util.*;

/**
 * Entity to persist information about users.
 *
 * @see AbstractProject
 * @see ProjectApplication
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
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
    @Column(nullable = false)
    private String firstName;

    /**
     * The last name of the user.
     */
    @Column(nullable = false)
    private String lastName;

    /**
     * The <b>unique</b> email address of the user.
     */
    @Column(unique = true, nullable = false)
    private String email;

    /**
     * The bookmarked {@link AbstractProject projects} of the
     * user.
     */
    @OneToMany(cascade = CascadeType.ALL)
    private Set<AbstractProject> bookmarks;

    /**
     * The {@link ProjectApplication applications} of the
     * user.
     */
    @OneToMany(cascade = CascadeType.ALL)
    private Set<ProjectApplication> applications;

    /**
     * The boss of this user.
     */
    @ManyToOne(cascade = CascadeType.ALL)
    private SuperUser boss;

    /**
     * The user's {@link UserAccessInfo} objects to evaluate
     * REST interface access.
     */
    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "JOIN_TABLE_USER_ACCESS_INFO")
    private List<UserAccessInfo> accessInfo;

    /**
     * Constructs a new instance.
     *
     * @param id
     *          The id of the user.
     *
     * @param firstName
     *          The first name of the user.
     *
     * @param lastName
     *          The last name of the user.
     *
     * @param email
     *          The <b>unique</b> email of the user.
     */
    public User(String id, String firstName, String lastName, String email) {
        this();

        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    protected User() {
        // protected no-arg constructor for JPA

        this.accessInfo = new LinkedList<>();
        this.applications = new LinkedHashSet<>();
        this.bookmarks = new LinkedHashSet<>();
    }

    /**
     *
     * @return
     *          The full name of the user in a <i>firstName lastName</i>
     *          pattern.
     *
     * @see #getFirstName()
     * @see #getLastName()
     */
    public String getFullName() {
        return firstName + ' ' + lastName;
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

    /**
     *
     *
     * @param until
     *          The {@link LocalDateTime} until the user has access.
     *
     * @return
     *          The {@link UserAccessInfo} object.
     *
     * @throws IllegalArgumentException
     *          When the given {@link LocalDateTime} is {@link LocalDateTime#isBefore(ChronoLocalDateTime) before}
     *          the current time.
     *
     * @see #hasAccess()
     * @see UserAccessInfo
     */
    public UserAccessInfo giveAccessUntil(LocalDateTime until) throws IllegalArgumentException {
        if(LocalDateTime.now().isAfter(until)) {
            throw new IllegalArgumentException("The given date has to be after the current date!");
        }

        if(hasAccess()) {
            UserAccessInfo activeInfo = accessInfo.get(accessInfo.size() - 1);
            activeInfo.setAccessEnd(until);

            return activeInfo;
        } else {
            UserAccessInfo info = new UserAccessInfo(this, until);
            accessInfo.add(info);

            return info;
        }
    }

    /**
     * Removes the user's access by setting the date of the active
     * {@link UserAccessInfo} to the current date in case it is present.
     *
     * @see #hasAccess()
     */
    public void removeAccess() {
        if(hasAccess()) {
            accessInfo.get(accessInfo.size() - 1).setAccessEnd(LocalDateTime.now());
        }
    }

    /**
     *
     * @return
     *         {@code true}, when the last {@link UserAccessInfo} in the
     *         {@link #accessInfo access info} list {@link UserAccessInfo#isCurrentlyActive()}  is active},
     *         {@code false} otherwise.
     *
     * @see UserAccessInfo#isCurrentlyActive()
     */
    public boolean hasAccess() {
        if(!accessInfo.isEmpty()) {
            return accessInfo.get(accessInfo.size() - 1).isCurrentlyActive();
        }

        return false;
    }

    /**
     *
     *
     * @return
     *          {@code null}, when {@link #hasAccess()} returns {@code false}
     *          and the active {@link UserAccessInfo} otherwise.
     *
     */
    public UserAccessInfo getAccessObject() {
        if(hasAccess()) {
            return accessInfo.get(accessInfo.size() - 1);
        }

        return null;
    }

}
