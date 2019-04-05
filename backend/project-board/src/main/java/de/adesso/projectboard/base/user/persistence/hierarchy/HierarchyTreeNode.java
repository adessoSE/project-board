package de.adesso.projectboard.base.user.persistence.hierarchy;

import de.adesso.projectboard.base.user.persistence.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.util.Streamable;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;


/**
 * Instances of this class can be used to represent manager - staff relationships in which
 * a manager has a reference to its direct staff members as well as his indirect staff members
 * ( the staff members of his direct staff members ). Every node has a reference to it's manager.
 * <br>
 *
 * Visual representation of a node of user <i>A</i>:
 * <pre>
 *
 *                    A
 *                  /  \
 *                 /    \
 *                B      C
 *              /  \
 *             D    E
 * </pre>
 *
 * A's <b>direct</b> staff members are the nodes of user <i>B</i> and <i>C</i> and the staff
 * members are the nodes of user <i>B, C, D</i> and <i>E</i>.
 */
@Entity
@Table(name = "HIERARCHY_TREE_NODE")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HierarchyTreeNode implements Iterable<HierarchyTreeNode>, Streamable<HierarchyTreeNode>, Serializable {

    /**
     * The ID of the tree node.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    /**
     * The manager's node.
     */
    @ManyToOne
    @JoinColumn(
            name = "MANAGER_NODE_ID"
    )
    HierarchyTreeNode manager;

    /**
     * The <b>direct</b> staff members' nodes.
     */
    @OneToMany(
            mappedBy = "manager",
            cascade = CascadeType.PERSIST
    )
    List<HierarchyTreeNode> directStaff;

    /**
     * <b>All</b> staff members' nodes. Also includes
     * the direct staff members' nodes.
     */
    @ManyToMany
    @JoinTable(
            name = "HIERARCHY_TREE_NODE_ALL_STAFF",
            joinColumns = @JoinColumn(name = "MANAGER_NODE_ID"),
            inverseJoinColumns = @JoinColumn(name = "NODE_ID")
    )
    List<HierarchyTreeNode> staff;

    /**
     * The user this node belongs to.
     */
    @OneToOne(optional = false)
    @JoinColumn(name = "USER_ID")
    User user;

    /**
     * Boolean that indicates whether the
     * user is a manager.
     */
    @Column(name = "IS_MANAGING_USER")
    boolean managingUser;

    /**
     * Constructs a new instance. The manager of the
     * node is set to {@code this} node.
     *
     * @param user
     *          The user this node belongs to, not null.
     */
    public HierarchyTreeNode(User user) {
        this.directStaff = new ArrayList<>();
        this.staff = new ArrayList<>();

        this.user = Objects.requireNonNull(user);
    }

    /**
     *
     * @return
     *          {@code true}, iff the node's staff are
     *          empty.
     */
    public boolean isLeaf() {
        return staff.isEmpty() && directStaff.isEmpty();
    }

    /**
     *
     * @return
     *          {@code true}, iff the node is equal to its
     *          manager node.
     */
    public boolean isRoot() {
        return Objects.isNull(manager);
    }

    /**
     * Walks up the parent nodes until the root is reached.
     *
     * @return
     *          {@code this}, when the node {@link #isRoot() is a root}
     *          or the result of this method of the parent node otherwise.
     */
    public HierarchyTreeNode getRoot() {
        if(isRoot()) {
            return this;
        }

        return manager.getRoot();
    }

    /**
     * Adds a given {@code staffNode} to the direct staff as well as the
     * staff of itself and all parent nodes until the root is reached
     * after removing it from the old manager's direct staff.
     *
     * @param staffNode
     *          The node to add to the direct staff, not null.
     */
    public void addDirectStaffMember(HierarchyTreeNode staffNode) {
        Objects.requireNonNull(staffNode);

        if(!staffNode.isRoot()) {
            staffNode.manager.removeDirectStaffMember(staffNode);
        }

        directStaff.add(staffNode);
        staffNode.manager = this;

        updateIsManagingUser();
        addStaffMember(staffNode);
    }

    /**
     * Adds the given {@code staffNode} to the staff of this
     * node as well as all parent nodes until the root is reached.
     *
     * @param staffNode
     *          The node to add to the staff, not null.
     */
    void addStaffMember(HierarchyTreeNode staffNode) {
        staff.add(staffNode);
        updateIsManagingUser();

        if(!isRoot()) {
            manager.addStaffMember(staffNode);
        }
    }

    /**
     * Removes a given {@code staffNode} from the direct staff
     * and staff of itself and all parent nodes until the root
     * is reached.
     *
     * @param staffNode
     *          The node to remove from the direct staff, not null.
     */
    public void removeDirectStaffMember(HierarchyTreeNode staffNode) {
        Objects.requireNonNull(staffNode);

        directStaff.remove(staffNode);

        updateIsManagingUser();
        removeStaffMember(staffNode);
    }

    /**
     * Removes the given {@code staffNode} from the staff
     * of this node and all parent nodes until the root is reached.
     *
     * @param staffNode
     *          The node to remove from the staff, not null.
     */
    void removeStaffMember(HierarchyTreeNode staffNode) {
        staff.remove(staffNode);
        updateIsManagingUser();

        if(!isRoot()) {
            manager.removeStaffMember(staffNode);
        }
    }

    void updateIsManagingUser() {
        this.managingUser = !this.directStaff.isEmpty() || this.staff.isEmpty();
    }

    /**
     *
     * @param other
     *          The other node to compare the manager of, not null.
     *
     * @return
     *          {@code true}, iff both managers are {@code null} or
     *          both manager node IDs are equal.
     */
    boolean managersEqual(HierarchyTreeNode other) {
        var otherManager = Objects.requireNonNull(other).manager;

        if(manager == otherManager) {
            return true;
        } else if(Objects.isNull(manager) || Objects.isNull(otherManager)) {
            return false;
        }

        return Objects.equals(manager.id, otherManager.id);
    }

    /**
     *
     * @return
     *          A {@link LevelOrderTreeIterator} instance.
     */
    @Override
    public Iterator<HierarchyTreeNode> iterator() {
        return staff.iterator();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        HierarchyTreeNode other = (HierarchyTreeNode) obj;

        return managingUser == other.managingUser &&
                Objects.equals(id, other.id) &&
                Objects.equals(directStaff, other.directStaff) &&
                Objects.equals(staff, other.staff) &&
                Objects.equals(user, other.user) &&
                managersEqual(other);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, staff, user, managingUser, directStaff) +
                (!Objects.isNull(manager) ? Objects.hash(manager.id) : 0);
    }

}
