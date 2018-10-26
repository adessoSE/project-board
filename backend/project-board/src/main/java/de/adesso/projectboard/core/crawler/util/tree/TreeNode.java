package de.adesso.projectboard.core.crawler.util.tree;

import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * A tree node with one parent node and multiple child nodes.
 *
 * @param <T>
 *          The type of the node's {@link TreeNode#content content}
 */
@Getter
@Setter
public class TreeNode<T> implements Iterable<TreeNode<T>> {

    /**
     * The parent node.
     */
    private TreeNode<T> parent;

    /**
     * The child nodes.
     */
    private final Set<TreeNode<T>> children;

    /**
     * The content of this node.
     */
    private final T content;

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
    }

    /**
     * Constructs a new instance.
     *
     * @param content
     *          The content of this node.
     */
    public TreeNode(T content) {
        this.children = new HashSet<>();

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
     */
    public void addChild(TreeNode<T> child) {
        if(child.parent != null) {
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
     */
    public void setParent(TreeNode<T> newParent) {
        if(parent != null) {
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
     *          When the node has no parent node ({@code parent == null}).
     */
    public boolean isRoot() {
        return parent == null;
    }

    /**
     * Traverses the tree upwards until the parent is reached.
     *
     * @return
     *          {@code this}, when the node {@link #isRoot() is a root}
     *          and the result of {@link #getRoot()} of the parent node
     *          otherwise.
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
     *          A {@link RootFirstTreeIterator} instance.
     */
    @Override
    public Iterator<TreeNode<T>> iterator() {
        return new RootFirstTreeIterator<>(this);
    }

}
