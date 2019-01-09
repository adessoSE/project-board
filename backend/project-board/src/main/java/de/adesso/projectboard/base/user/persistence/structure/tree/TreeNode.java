package de.adesso.projectboard.base.user.persistence.structure.tree;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.util.Streamable;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;
import java.util.function.Function;

@Getter
@Setter
@MappedSuperclass
public class TreeNode<T> implements Iterable<T>, Streamable<T>, Serializable {

    /**
     * The ID of the tree node.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The parent node.
     */
    @ManyToOne
    @JoinColumn(name = "PARENT_NODE_ID")
    TreeNode<T> parent;

    /**
     * The child nodes.
     */
    @OneToMany(
            mappedBy = "parent",
            orphanRemoval = true,
            cascade = CascadeType.ALL
    )
    List<TreeNode<T>> children;

    /**
     * The content of this node.
     */
    @OneToOne(optional = false)
    @Column(name = "CONTENT_ID")
    T content;

    /**
     * Constructs a new instance. The newly created node is
     * added to the parent's {@link TreeNode#children children}
     * via the {@link TreeNode#addChild(TreeNode)} method.
     *
     * @param parent
     *          The parent of this node.
     *
     * @param content
     *          The content of this node.
     *
     * @see TreeNode#TreeNode(Object)
     */
    public TreeNode(TreeNode<T> parent, T content) {
        this(content);

        parent.addChild(this);
        this.parent = parent;
    }

    /**
     * Constructs a new instance. Sets the content to
     * the given {@code content} and the parent to
     * {@code null}.
     *
     * @param content
     *          The content of this node.
     *
     */
    public TreeNode(T content) {
        this.children = new ArrayList<>();

        this.content = content;
        this.parent = null;
    }

    /**
     * Adds a new child to the {@link TreeNode#children children}
     * of the node. Removes it from previous parent's children
     * and sets the {@link TreeNode#parent parent} to {@code this}.
     *
     * @param child
     *          The child to add.
     *
     * @throws IllegalArgumentException
     *          When the {@code child} is equal to {@code this}.
     */
    public void addChild(TreeNode<T> child) throws IllegalArgumentException {
        if(equals(child)) {
            throw new IllegalArgumentException("A node can't have itself as a child!");
        }

        if(!Objects.isNull(child.parent)) {
            parent.children.remove(child);
        }

        this.children.add(child);

        child.parent = this;
    }

    /**
     * Sets the parent of the node to the given {@code newParent}.
     * Removes it from the previous parent's {@link TreeNode#children children}
     * and adds it to the new parent's.
     *
     * @param newParent
     *          The new parent node.
     *
     * @throws IllegalArgumentException
     *          When the new parent node is equal to {@code this}.
     */
    public void setParent(TreeNode<T> newParent) throws IllegalArgumentException {
        if(equals(newParent)) {
            throw new IllegalArgumentException("A node can't be it's own parent!");
        }

        if(!Objects.isNull(parent)) {
            parent.children.remove(this);
        }

        newParent.children.add(this);
        this.parent = newParent;
    }

    /**
     *
     * @return
     *          When the node has no {@link TreeNode#children children}
     *          nodes ({@code children.size() == 0}).
     */
    public boolean isLeaf() {
        return children.isEmpty();
    }

    /**
     *
     * @return
     *          {@code true}, iff the node has no parent node
     *          ({@code parent == null}).
     */
    public boolean isRoot() {
        return Objects.isNull(parent);
    }

    /**
     * Walks up the parent nodes until the root is reached.
     *
     * @return
     *          {@code this}, when the node {@link #isRoot() is a root}
     *          or the result of this method of the parent node otherwise.
     */
    public TreeNode<T> getRoot() {
        if(isRoot()) {
            return this;
        }

        return parent.getRoot();
    }

    /**
     *
     * @return
     *          A {@link LevelOrderTreeIterator} instance.
     */
    @Override
    public Iterator<T> iterator() {
        return new LevelOrderTreeIterator<>(this);
    }

    /**
     * Maps this node's and it's children's content
     * using the given {@code mappingFunction}. The
     * child - parent relationship is kept.
     *
     * @param mappingFunction
     *          The function to apply to the tree nodes'
     *          content.
     *
     * @param <S>
     *          The type of the returned tree node's
     *          content.
     *
     * @return
     *          A tree node with the content of itself and all
     *          it's children mapped by the given {@code mappingFunction}.
     *          The parent of the returned tree node will not be set.
     */
    public <S> TreeNode<S> mapNodeAndChildren(Function<T, S> mappingFunction) {
        var newRoot = new TreeNode<S>(mappingFunction.apply(content));

        children.stream()
                .map(childNode -> childNode.mapNodeAndChildren(mappingFunction))
                .forEach(newRoot::addChild);

        return newRoot;
    }

    /**
     *
     * @return
     *          A empty optional, iff the parent node
     *          is {@code null} and a optional containing the
     *          parent node otherwise.
     */
    public Optional<TreeNode<T>> getParent() {
        return Optional.ofNullable(parent);
    }

    @Override
    public boolean equals(Object otherObj) {
        if (this == otherObj) {
            return true;
        } else if (otherObj == null || getClass() != otherObj.getClass()) {
            return false;
        }

        TreeNode<?> otherNode = (TreeNode<?>) otherObj;

        return Objects.equals(id, otherNode.id) &&
                Objects.equals(children, otherNode.children) &&
                Objects.equals(content, otherNode.content) &&
                (Objects.isNull(parent) ? Objects.isNull(otherNode.parent) :
                        (!Objects.isNull(otherNode.parent) && Objects.equals(parent.id, otherNode.parent.id))
                );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, children, content) +
                31 * (!Objects.isNull(parent) ? Objects.hash(parent.id) : 0);
    }

}


