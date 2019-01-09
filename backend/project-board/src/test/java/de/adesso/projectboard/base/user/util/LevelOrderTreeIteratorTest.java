package de.adesso.projectboard.base.user.util;

import de.adesso.projectboard.base.user.persistence.structure.tree.LevelOrderTreeIterator;
import de.adesso.projectboard.base.user.persistence.structure.tree.TreeNode;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class LevelOrderTreeIteratorTest {

    private final int NODE_COUNT = 5;

    private TreeNode<String> rootNode;

    @Before
    public void setUp() {
        this.rootNode = new TreeNode<String>("Root");

        var firstLevelNode1 = new TreeNode<String>("First Level 1");
        var firstLevelNode2 = new TreeNode<String>("First Level 2");
        var secondLevelNode1 = new TreeNode<String>("Second Level 1");
        var secondLevelNode2 = new TreeNode<String>("Second Level 2");
        var thirdLevelNode = new TreeNode<String>("Third Level");

        rootNode.children.addAll(Arrays.asList(firstLevelNode1, firstLevelNode2));
        firstLevelNode1.children.add(secondLevelNode1);
        firstLevelNode2.children.add(secondLevelNode2);
        secondLevelNode1.children.add(thirdLevelNode);
    }

    @Test
    public void nextReturnsExpectedIterationOrder() {
        // given
        var iterator = new LevelOrderTreeIterator<String>(rootNode);

        var expectedNodeIteration = Arrays.asList("Root", "First Level 1", "First Level 2",
                "Second Level 1", "Second Level 2", "Third Level");

        // when
        var actualNodeIteration = new ArrayList<String>();
        for(var i = 0; i <= NODE_COUNT; i++) {
            actualNodeIteration.add(iterator.next());
        }

        // then
        assertThat(actualNodeIteration).isEqualTo(expectedNodeIteration);
    }

    @Test
    public void nextThrowsNoSuchElementExceptionWhenNoElementIsPresent() {
        // given
        var iterator = new LevelOrderTreeIterator<>(rootNode);

        // when
        for(var i = 0; i <= NODE_COUNT; i++) {
            iterator.next();
        }

        assertThatThrownBy(iterator::next)
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("No further element present!");
    }

    @Test
    public void hasNextReturnsTrueWhenFurtherElementsArePresent() {
        // given
        var iterator = new LevelOrderTreeIterator<>(rootNode);

        // when
        for(var i = 0; i <= NODE_COUNT - 1; i++) {
            iterator.next();
        }

        // then
        assertThat(iterator.hasNext()).isTrue();
    }

    @Test
    public void hasNextReturnsFalseWhenNoFurtherElementsArePresent() {
        // given
        var iterator = new LevelOrderTreeIterator<>(rootNode);

        // when
        for(var i = 0; i <= NODE_COUNT; i++) {
            iterator.next();
        }

        // then
        assertThat(iterator.hasNext()).isFalse();
    }

}