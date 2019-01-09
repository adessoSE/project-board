package de.adesso.projectboard.base.user.persistence.structure.tree;

import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;

/**
 * {@link Iterator} implementation to iterate a (sub-)tree constructed by a {@link TreeNode}
 * in a level order.
 * <br><br>
 * <p>
 *
 *     <b>Example Iteration</b> (<i>A</i> is the node this iterator originated from):
 *     <pre>
 *
 *            A
 *          / | \
 *        /   |  \
 *       B    E   F
 *      / \       |
 *     C   D      G
 *
 *     </pre>
 *
 *     Iteration order: <i>A - B - E - F - C - D - G</i>.
 * </p>
 *
 * @param <T>
 *          The type of the node's {@link TreeNode#content content}.
 */
public class LevelOrderTreeIterator<T> implements Iterator<T> {

    /**
     * A deque to queue the further elements returned
     * by this iterator.
     */
    private final Deque<TreeNode<T>> furtherNodeDeque;

    /**
     * A deque to save the already iterated nodes that have child nodes.
     * A node gets removed after it's child nodes were added to the
     * {@code furtherNodeDeque} to minimize forward loading of tree nodes.
     */
    private final Deque<TreeNode<T>> iteratedNodeDeque;

    /**
     * Constructs a new instance.
     *
     * @param node
     *          The node this iterator belongs to.
     */
    LevelOrderTreeIterator(TreeNode<T> node) {
        this.furtherNodeDeque = new LinkedList<>();
        this.iteratedNodeDeque = new LinkedList<>();

        this.furtherNodeDeque.add(node);
    }

    @Override
    public boolean hasNext() {
        return !furtherNodeDeque.isEmpty() || !iteratedNodeDeque.isEmpty();
    }

    @Override
    public T next() {
        if(!hasNext()) {
            throw new NoSuchElementException("No further element present!");
        }

        // fill up the deque with the children in case it
        // is empty
        if(furtherNodeDeque.isEmpty()) {
            var lastNode = iteratedNodeDeque.removeFirst();

            furtherNodeDeque.addAll(lastNode.getChildren());
        }

        // get the next node returned by the iterator and add
        // it to the iteratedNodeDeque iff the node has child nodes
        var currentNode = furtherNodeDeque.removeFirst();

        if(!currentNode.isLeaf()) {
            iteratedNodeDeque.add(currentNode);
        }

        return currentNode.getContent();
    }

}
