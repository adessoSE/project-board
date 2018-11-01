package de.adesso.projectboard.core.base.rest.user.persistence;

import de.adesso.projectboard.core.base.rest.project.persistence.Project;
import de.adesso.projectboard.core.base.rest.user.application.persistence.ProjectApplication;
import de.adesso.projectboard.core.base.rest.user.useraccess.persistence.AccessInfo;
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
@Table(name = "PB_USER")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Getter
@Setter
public class User {

    /**
     * The <b>unique</b> id (<i>e.g.</i> the username)
     * of the user.
     */
    @Id
    String id;

    /**
     * The first name of the user.
     */
    @Column(nullable = false)
    String firstName;

    /**
     * The last name of the user.
     */
    @Column(nullable = false)
    String lastName;

    /**
     * The <b>unique</b> email address of the user.
     */
    @Column(unique = true, nullable = false)
    String email;

    /**
     * The LoB of the user.
     */
    @Column(nullable = false)
    String lob;

    /**
     * The bookmarked {@link Project}s of the
     * user.
     */
    @ManyToMany
    @JoinTable(name = "USER_BOOKMARKS")
    Set<Project> bookmarks;

    /**
     * The {@link ProjectApplication applications} of the
     * user.
     */
    @OneToMany(
            cascade = CascadeType.ALL,
            mappedBy = "user"
    )
    Set<ProjectApplication> applications;

    /**
     * The user's created {@link Project}s.
     */
    @OneToMany(cascade = CascadeType.ALL)
    Set<Project> createdProjects;

    /**
     * The boss of this user.
     */
    @ManyToOne(
            cascade = {CascadeType.REFRESH, CascadeType.DETACH, CascadeType.MERGE},
            optional = false
    )
    SuperUser boss;

    /**
     * The user's {@link AccessInfo} objects to evaluate
     * REST interface access.
     */
    @OneToMany(
            cascade = CascadeType.ALL,
            mappedBy = "user"
    )
    List<AccessInfo> accessInfoList;

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

        this.accessInfoList = new LinkedList<>();
        this.applications = new LinkedHashSet<>();
        this.bookmarks = new LinkedHashSet<>();
        this.createdProjects = new LinkedHashSet<>();
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
    public User setFullName(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;

        return this;
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
     *
     * @param until
     *          The {@link LocalDateTime} until the user has access.
     *
     * @return
     *          The {@link AccessInfo} object.
     *
     * @throws IllegalArgumentException
     *          When the given {@link LocalDateTime} is {@link LocalDateTime#isBefore(ChronoLocalDateTime) before}
     *          the current time.
     *
     * @see #hasAccess()
     * @see AccessInfo
     */
    public AccessInfo giveAccessUntil(LocalDateTime until) throws IllegalArgumentException {
        if(LocalDateTime.now().isAfter(until)) {
            throw new IllegalArgumentException("The given date has to be after the current date!");
        }

        if(hasAccess()) {
            AccessInfo activeInfo = accessInfoList.get(accessInfoList.size() - 1);
            activeInfo.setAccessEnd(until);

            return activeInfo;
        } else {
            AccessInfo info = new AccessInfo(this, until);
            accessInfoList.add(info);

            return info;
        }
    }

    /**
     * Removes the user's access by setting the date of the active
     * {@link AccessInfo} to the current date in case it is present.
     *
     * @see #hasAccess()
     */
    public void removeAccess() {
        if(hasAccess()) {
            accessInfoList.get(accessInfoList.size() - 1).setAccessEnd(LocalDateTime.now());
        }
    }

    /**
     *
     * @return
     *         {@code true}, when the last {@link AccessInfo} in the
     *         {@link #accessInfoList access info} list {@link AccessInfo#isCurrentlyActive()}  is active},
     *         {@code false} otherwise.
     *
     * @see AccessInfo#isCurrentlyActive()
     */
    public boolean hasAccess() {
        if(!accessInfoList.isEmpty()) {
            return accessInfoList.get(accessInfoList.size() - 1).isCurrentlyActive();
        }

        return false;
    }

    /**
     *
     *
     * @return
     *          {@code null}, when {@link #hasAccess()} returns {@code false}
     *          and the active {@link AccessInfo} otherwise.
     *
     */
    public AccessInfo getAccessObject() {
        if(hasAccess()) {
            return accessInfoList.get(accessInfoList.size() - 1);
        }

        return null;
    }

    /**
     *
     * @param boss
     *          The boss of the user.
     *
     * @see SuperUser#addStaffMember(User)
     */
    public void setBoss(SuperUser boss) {
        boss.addStaffMember(this);
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
        return false;
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
        return false;
    }
}
