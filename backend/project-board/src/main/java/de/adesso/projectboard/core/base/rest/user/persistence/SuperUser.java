package de.adesso.projectboard.core.base.rest.user.persistence;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 *
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
     *
     * @param id
     *          The id of this user.
     *
     * @param firstName
     *          The first name of this user.
     *
     * @param lastName
     *          The last name of this user.
     *
     * @param email
     *          The <b>unique</b> email address of this user.
     *
     */
    public SuperUser(String id, String firstName, String lastName, String email) {
        super(id, firstName, lastName, email);

        this.staffMembers = new LinkedHashSet<>();
    }

    protected SuperUser() {
        // protected no-arg constructor for JPA

        this.staffMembers = new LinkedHashSet<>();
    }

    /**
     * Adds a {@link User} to the {@link #staffMembers} and sets
     * the {@code user}'s boss to {@code this}.
     *
     * @param user
     *          The {@link User} to add to the staff members.
     *
     * @return
     *          The result of {@link Set#add(Object)}
     *
     * @see #removeStaffMember(User)
     */
    public boolean addStaffMember(User user) {
        if(staffMembers.add(user)) {
            user.setBoss(this);

            return true;
        }

        return false;
    }

    /**
     * Removes a {@link User} from the {@link #staffMembers} and sets
     * the {@code user}'s boss to <i>null</i> when the user was
     * found in the {@link #staffMembers staff members} set.
     *
     * @param user
     *          The {@link User} to remove from the staffMembers.
     *
     * @return
     *          The result of {@link Set#remove(Object)}
     *
     * @see #addStaffMember(User)
     */
    public boolean removeStaffMember(User user) {
        if(staffMembers.remove(user)) {
            user.setBoss(null);

            return true;
        }

        return false;
    }

}
