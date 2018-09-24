package de.adesso.projectboard.core.base.rest.user.persistence;

import de.adesso.projectboard.core.base.rest.project.persistence.Project;
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
 * @see SuperUser
 * @see Project
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

    @Column(nullable = false)
    private String lob;

    /**
     * The bookmarked {@link Project projects} of the
     * user.
     */
    @OneToMany(cascade = CascadeType.ALL)
    private Set<Project> bookmarks;

    /**
     * The {@link ProjectApplication applications} of the
     * user.
     */
    @OneToMany(cascade = CascadeType.ALL)
    private Set<ProjectApplication> applications;

    /**
     * The boss of this user.
     */
    @ManyToOne(cascade = CascadeType.ALL, optional = false)
    private SuperUser boss;

    /**
     * The user's {@link UserAccessInfo} objects to evaluate
     * REST interface access.
     */
    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "JOIN_TABLE_USER_ACCESS_INFO")
    private List<UserAccessInfo> accessInfo;


    /**
     * Constructs a new instance. Sets the ID and adds the user
     * to the {@link SuperUser#staffMembers staff members} of the
     * given {@link SuperUser}.
     *
     * <p>
     *     <b>Note:</b> {@link #setFirstName(String) first name}, {@link #setLastName(String) last name},
     *     {@link #setEmail(String) email address} and {@link #setLob(String) LOB} have to be set
     *     afterwards!
     * </p>
     *
     * @param userId
     *          The ID of the user.
     *
     * @param boss
     *          The {@link SuperUser boss} of the user.
     */
    public User(String userId, SuperUser boss) {
        this();

        this.id = userId;
        boss.addStaffMember(this);
    }

    protected User() {
        // protected no-arg constructor for JPA

        this.accessInfo = new LinkedList<>();
        this.applications = new LinkedHashSet<>();
        this.bookmarks = new LinkedHashSet<>();
    }

    /**
     *
     * @param firstName
     *          The first name of the user.
     *
     * @param lastName
     *          The last name of the user.
     *
     * @see #setFirstName(String)
     * @see #setLastName(String)
     */
    public void setFullName(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
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
     * @see #addBookmark(Project)
     */
    public boolean addApplication(ProjectApplication application) {
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

    /**
     *
     * @param boss
     *          The boss of the user.
     */
    protected void setBoss(SuperUser boss) {
        this.boss = boss;
    }

}
