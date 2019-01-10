package de.adesso.projectboard.base.user.persistence.hierarchy;

import de.adesso.projectboard.base.user.persistence.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.util.Streamable;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Entity
@Table(name = "HIERARCHY_TREE_NODE")
@Getter
@Setter
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
    @JoinColumn(name = "MANAGER_NODE_ID")
    HierarchyTreeNode manager;

    /**
     * The employees' nodes.
     */
    @OneToMany(
            mappedBy = "manager",
            orphanRemoval = true,
            cascade = CascadeType.ALL
    )
    List<HierarchyTreeNode> employees;

    /**
     * The user this node belongs to.
     */
    @OneToOne(optional = false)
    @Column(name = "USER_ID")
    User user;

    /**
     * Boolean that indicates whether the
     * user is a manager.
     */
    @Column(name = "IS_MANAGING_USER")
    boolean managingUser;

    @Column(name = "IS_FULLY_INITIALIZED")
    boolean fullyInitialized;

    /**
     * Constructs a new instance. The newly created node
     * is added to the given {@code manager} node's via the
     * {@link HierarchyTreeNode#addEmployee(HierarchyTreeNode)}
     * method.
     *
     * @param manager
     *          The manager's node, not null.
     *
     * @param user
     *          The user this node belongs to, not null.
     */
    public HierarchyTreeNode(HierarchyTreeNode manager, User user) {
        this(user);

        manager.addEmployee(this);
        this.manager = manager;
    }

    /**
     * Constructs a new instance. Sets the content to
     * the given {@code content} and the parent to
     * {@code null}.
     *
     * @param user
     *          The user this node belongs to, not null.
     *
     */
    public HierarchyTreeNode(User user) {
        this.employees = new ArrayList<>();
        this.user = user;
        this.manager = null;
        this.managingUser = false;
    }

    /**
     * Adds a new employee node to the {@link HierarchyTreeNode#employees employees}
     * of {@code this} node. Removes the employee node from previous manager's employees
     * and sets the {@link HierarchyTreeNode#manager manager} to {@code this}.
     *
     * @param employee
     *          The employee node to add, not null.
     *
     * @throws IllegalArgumentException
     *          When the given {@code employee} is equal to {@code this}.
     */
    public void addEmployee(HierarchyTreeNode employee) throws IllegalArgumentException {
        if(equals(employee)) {
            throw new IllegalArgumentException("A node can't have itself as a child!");
        }

        if(!Objects.isNull(employee.manager)) {
            manager.employees.remove(employee);
        }

        this.employees.add(employee);
        employee.manager = this;
    }

    /**
     * Sets the manager of {@code this} to the given {@code newParent}.
     * Removes {@code this} node from the previous parent's
     * {@link HierarchyTreeNode#employees employees} and adds it to the new parent's.
     *
     * @param newManager
     *          The new manager's node.
     *
     * @throws IllegalArgumentException
     *          When the given {@code newManager} is equal to {@code this}.
     */
    public void setManager(HierarchyTreeNode newManager) throws IllegalArgumentException {
        if(equals(newManager)) {
            throw new IllegalArgumentException("A node can't be it's own newManager!");
        }

        if(!Objects.isNull(newManager)) {
            this.manager.employees.remove(this);
        }

        newManager.employees.add(this);
        this.manager = newManager;
    }

    /**
     *
     * @return
     *          {@code true}, iff the node's employees are
     *          empty.
     */
    public boolean isLeaf() {
        return employees.isEmpty();
    }

    /**
     *
     * @return
     *          {@code true}, iff the node has no parent node
     *          ({@code parent == null}).
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
     *
     * @return
     *          A empty optional, iff the parent node
     *          is {@code null} and a optional containing the
     *          parent node otherwise.
     */
    public Optional<HierarchyTreeNode> getManager() {
        return Optional.ofNullable(manager);
    }

    /**
     *
     * @param other
     *          The other node to compare the manager of, nullable.
     *
     * @return
     *          {@code true}, iff both managers are {@code null} or
     *          both manager node IDs are equal.
     */
    boolean managersEqual(HierarchyTreeNode other) {
        if(manager == other.manager) {
            return true;
        } else if(!Objects.isNull(manager) && !Objects.isNull(other.manager)) {
            return Objects.equals(manager.id, other.manager.id);
        }

        return false;
    }

    /**
     *
     * @return
     *          A {@link LevelOrderTreeIterator} instance.
     */
    @Override
    public Iterator<HierarchyTreeNode> iterator() {
        return new LevelOrderTreeIterator(this);
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
                Objects.equals(employees, other.employees) &&
                Objects.equals(user, other.user) &&
                managersEqual(other);
    }

    @Override
    public int hashCode() {
        var managerId = getManager().isPresent() ? manager.id : null;
        return Objects.hash(id, employees, user, managingUser, managerId);
    }

}
