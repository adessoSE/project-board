package de.adesso.projectboard.core.base.rest.user.persistence;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
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
    @OneToMany(cascade = CascadeType.ALL)
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
     *
     * @return
     *          A <b>unmodifiable</b> {@link Set} of the {@link User staff members}.
     */
    @Override
    public Set<User> getStaffMembers() {
        return Collections.unmodifiableSet(staffMembers);
    }

}
