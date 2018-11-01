package de.adesso.projectboard.base.user.persistence.structure;

import de.adesso.projectboard.base.user.persistence.User;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Table
@Getter
@Setter
public class OrganizationStructure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private User user;

    @Column(nullable = false)
    private User manager;

    @OneToMany
    private List<User> staffMembers;

}
