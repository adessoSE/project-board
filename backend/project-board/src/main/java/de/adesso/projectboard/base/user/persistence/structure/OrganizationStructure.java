package de.adesso.projectboard.base.user.persistence.structure;

import de.adesso.projectboard.base.user.persistence.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrganizationStructure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private User user;

    @Column(nullable = false)
    private User manager;

    @OneToMany
    private Set<User> staffMembers = new HashSet<>();

    /**
     *
     * @param user
     *          The {@link User} this instance belongs to.
     *
     * @param manager
     *          The manager of the user.
     *
     * @param staffMembers
     *          The staff members of the user.
     */
    public OrganizationStructure(User user, User manager, Set<User> staffMembers) {
        this.user = user;
        this.manager = manager;
        this.staffMembers = staffMembers;
    }

}
