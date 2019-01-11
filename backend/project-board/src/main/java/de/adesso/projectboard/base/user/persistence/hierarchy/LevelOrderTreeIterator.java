package de.adesso.projectboard.base.user.persistence.hierarchy;

import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;

/**
 * {@link Iterator} implementation to iterate a (sub-)tree constructed by a {@link HierarchyTreeNode}
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
 */
public class LevelOrderTreeIterator implements Iterator<HierarchyTreeNode> {

    /**
     * A deque to queue the further elements returned
     * by this iterator.
     */
    private final Deque<HierarchyTreeNode> nextDeque;

    /**
     * A deque to save the already iterated manager nodes.
     * A node gets removed after it's child nodes were added to the
     * {@code nextDeque} to minimize forward loading of tree nodes.
     */
    private final Deque<HierarchyTreeNode> iteratedManagersDeque;

    /**
     * Constructs a new instance.
     *
     * @param node
     *          The node this iterator belongs to.
     */
    LevelOrderTreeIterator(HierarchyTreeNode node) {
        this.nextDeque = new LinkedList<>();
        this.iteratedManagersDeque = new LinkedList<>();

        this.nextDeque.add(node);
    }

    /**
     *
     * @return
     *          {@code true}, iff at least one of the deques
     *          is not {@code empty}.
     */
    @Override
    public boolean hasNext() {
        return !nextDeque.isEmpty() || !iteratedManagersDeque.isEmpty();
    }

    @Override
    public HierarchyTreeNode next() {
        if(!hasNext()) {
            throw new NoSuchElementException("No further node present!");
        }

        if(nextDeque.isEmpty()) {
            addFirstManagersEmployeesToNextDeque();
        }

        var next = nextDeque.removeFirst();
        return addNodeToIteratedManagersDequeIfManager(next);
    }

    /**
     * Adds the first node's employee nodes of the {@link #iteratedManagersDeque}
     * at the end of the {@link #nextDeque}.
     */
    void addFirstManagersEmployeesToNextDeque() {
         var firstIterated = iteratedManagersDeque.removeFirst();
         nextDeque.addAll(firstIterated.getStaff());
    }

    /**
     * Adds the given {@code node} to the {@link #iteratedManagersDeque}
     * in case the node's {@code user} is a manager.
     *
     * @param node
     *          The node to add to the {@link #iteratedManagersDeque}.
     *
     * @return
     *          The given {@code node}.
     */
    HierarchyTreeNode addNodeToIteratedManagersDequeIfManager(HierarchyTreeNode node) {
        if(!node.isManagingUser()) {
            iteratedManagersDeque.add(node);
        }

        return node;
    }

}
