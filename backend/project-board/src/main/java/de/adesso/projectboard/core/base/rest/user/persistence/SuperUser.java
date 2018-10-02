package de.adesso.projectboard.core.base.rest.user.persistence;

import de.adesso.projectboard.core.base.rest.project.persistence.Project;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * User representing a manager.
 *
 * @see User
 */
@Entity
@Getter
@Setter
public class SuperUser extends User {

    /**
     * The user's supervised {@link User}s.
     */
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<User> staffMembers;

    /**
     * Constructs a new instance. The boss is set to {@code this}
     * by using the {@link #addStaffMember(User)} method.
     *
     * <p>
     *     <b>Note:</b> {@link #setFirstName(String) first name}, {@link #setLastName(String) last name},
     *     {@link #setEmail(String) email address} and {@link #setLob(String) LOB} have to be set
     *     afterwards!
     * </p>
     *
     * @param userId
     *          The ID of the user.
     */
    public SuperUser(String userId) {
        this();

        this.setId(userId);
        this.addStaffMember(this);
    }

    /**
     * Constructs a new instance. Sets the ID and adds the user
     * to the {@link #staffMembers staff members} of the user.
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
    public SuperUser(String userId, SuperUser boss) {
        this();

        this.setId(userId);
        boss.addStaffMember(this);
    }

    protected SuperUser() {
        // protected no-arg constructor for JPA

        this.staffMembers = new LinkedHashSet<>();
    }

    /**
     * Removes the user from the previous boss' {@link #staffMembers staff members},
     * adds to to the {@link #staffMembers staff members} of this user
     * and sets the {@code user}'s boss to {@code this}.
     *
     * @param user
     *          The {@link User} to add to the staff members.
     *
     * @return
     *          {@code true}, if the user was newly added to
     *          the staff members, {@code false} otherwise.
     *
     */
    public boolean addStaffMember(User user) {
        if(staffMembers.add(user)) {
            SuperUser oldBoss = user.getBoss();
            if(oldBoss != null) {
                oldBoss.staffMembers.remove(user);
            }

            user.setBoss(this);

            return true;
        }

        return false;
    }

    /**
     * Removes a {@link User} from the {@link #staffMembers} and sets its
     * {@link #boss} to {@code null}.
     *
     * <p>
     *     <b>Note:</b> The user can not be persisted afterwards, because a {@link SuperUser boss}
     *     is required!
     * </p>
     *
     * @param user
     *          The {@link User} to remove from the {@link #staffMembers}.
     *
     * @return
     *          {@code true}, when the given {@code user} was present in the {@link #staffMembers}
     *          set, {@code false} otherwise.
     */
    public boolean removeStaffMember(User user) {
        if(staffMembers.remove(user)) {
            user.setBoss(null);

            return true;
        }

        return false;
    }

    /**
     *
     * @return
     *          A <b>unmodifiable</b> {@link Set} of the {@link User staff members}.
     */
    @Override
    public Set<User> getStaffMembers() {
        return Collections.unmodifiableSet(staffMembers);
    }

    /**
     *
     * @return
     *          A {@link Set} of the created {@link Project}s of this user.
     */
    @Override
    public Set<Project> getCreatedProjects() {
        return Collections.unmodifiableSet(createdProjects);
    }

    /**
     *
     * @param project
     *          The {@link Project} to add to the created projects.
     *
     * @return
     *          The result of {@link Set#add(Object)}
     */
    @Override
    public boolean addCreatedProject(Project project) {
        return createdProjects.add(project);
    }

    /**
     *
     * @param project
     *          The {@link Project} to remove from the created projects.
     *
     * @return
     *          The result of {@link Set#remove(Object)}.
     */
    @Override
    public boolean removeCreatedProject(Project project) {
        return createdProjects.remove(project);
    }

}
