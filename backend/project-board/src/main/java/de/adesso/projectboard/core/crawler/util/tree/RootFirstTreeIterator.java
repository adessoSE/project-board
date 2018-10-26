package de.adesso.projectboard.core.crawler.util.tree;

import java.util.Iterator;

/**
 * {@link Iterator} implementation to iterate a {@link TreeNode} in a
 * {@code node} - {@code child} order.
 *
 * @param <T>
 *          The type of the {@link TreeNode}'s {@link TreeNode#content content}.
 */
public class RootFirstTreeIterator<T> implements Iterator<TreeNode<T>> {

    /**
     * The root {@link TreeNode} of this iterator.
     */
    private final TreeNode<T> node;

    /**
     * Indicates if the root node of this iterator
     * has already been returned.
     */
    private boolean nodeReturned;

    /**
     * {@link Iterator} to iterate over all child nodes.
     */
    private final Iterator<TreeNode<T>> childrenIterator;

    /**
     * {@link Iterator} to iterate over all child nodes
     * of the currently viewed child node.
     */
    private Iterator<TreeNode<T>> currentChildNodeIterator;

    RootFirstTreeIterator(TreeNode<T> node) {
        this.node = node;

        this.childrenIterator = node.getChildren().iterator();
        this.nodeReturned = false;
    }

    @Override
    public boolean hasNext() {
        if(!nodeReturned) {
            return true;
        } else {
            if(currentChildNodeIterator != null) {
                if(currentChildNodeIterator.hasNext()) {
                    return true;
                }
            }

            if(childrenIterator.hasNext()) {
                currentChildNodeIterator = childrenIterator.next().iterator();

                return currentChildNodeIterator.hasNext();
            }

            return false;
        }
    }

    @Override
    public TreeNode<T> next() {
        if(!nodeReturned) {
            nodeReturned = true;

            return node;
        } else {
            return currentChildNodeIterator.next();
        }
    }

}
