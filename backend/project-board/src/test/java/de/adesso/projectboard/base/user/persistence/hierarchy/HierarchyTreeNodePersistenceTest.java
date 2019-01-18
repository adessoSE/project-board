package de.adesso.projectboard.base.user.persistence.hierarchy;

import de.adesso.projectboard.base.user.persistence.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
@TestPropertySource("classpath:application-persistence-test.properties")
public class HierarchyTreeNodePersistenceTest {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private HierarchyTreeNodeRepository hierarchyTreeNodeRepo;

    @Test
    @Sql("classpath:de/adesso/projectboard/persistence/Users.sql")
    public void save() {
        // given
        var user = userRepo.findById("User1").orElseThrow();
        var directStaff = userRepo.findById("User2").orElseThrow();
        var indirectStaff = userRepo.findById("User3").orElseThrow();

        var rootNode = new HierarchyTreeNode(user);
        var directChildNode = new HierarchyTreeNode(directStaff);
        var indirectChildNode = new HierarchyTreeNode(indirectStaff);

        rootNode.addDirectStaffMember(directChildNode);
        directChildNode.addDirectStaffMember(indirectChildNode);

        // when
        var savedNode = hierarchyTreeNodeRepo.save(rootNode);
        var actualRootNode = hierarchyTreeNodeRepo.findById(savedNode.id).orElseThrow();

        // then
        rootNode.id = actualRootNode.id;
        assertThat(actualRootNode).isEqualTo(rootNode);
    }

    @Test
    @Sql({
            "classpath:de/adesso/projectboard/persistence/Users.sql",
            "classpath:de/adesso/projectboard/persistence/HierarchyTreeNode.sql"
    })
    public void findByUserReturnsHierarchyNodeWhenPresent() {
        // given
        var user = userRepo.findById("User1").orElseThrow();
        var expectedNode = hierarchyTreeNodeRepo.findById(1L).orElseThrow();

        // when
        var actualNodeOptional = hierarchyTreeNodeRepo.findByUser(user);

        // then
        assertThat(actualNodeOptional)
                .isPresent()
                .contains(expectedNode);
    }

    @Test
    @Sql({
            "classpath:de/adesso/projectboard/persistence/Users.sql",
            "classpath:de/adesso/projectboard/persistence/HierarchyTreeNode.sql"
    })
    public void findByUserDoesNotReturnHierarchyNodeWhenNotPresent() {
        // given
        var user = userRepo.findById("User5").orElseThrow();

        // when
        var actualNodeOptional = hierarchyTreeNodeRepo.findByUser(user);

        // then
        assertThat(actualNodeOptional)
                .isEmpty();
    }

    @Test
    @Sql({
            "classpath:de/adesso/projectboard/persistence/Users.sql",
            "classpath:de/adesso/projectboard/persistence/HierarchyTreeNode.sql"
    })
    public void findByUserInReturnsHierarchyNodeForUserWhenPresent() {
        // given
        var userWithNodePresent = userRepo.findById("User1").orElseThrow();
        var userWithNoNodePresent = userRepo.findById("User5").orElseThrow();

        var expectedNode = hierarchyTreeNodeRepo.findById(1L).orElseThrow();

        // when
        var actualNodes = hierarchyTreeNodeRepo.findByUserIn(List.of(userWithNodePresent, userWithNoNodePresent));

        // then
        assertThat(actualNodes).containsExactly(expectedNode);
    }

    @Test
    @Sql({
            "classpath:de/adesso/projectboard/persistence/Users.sql",
            "classpath:de/adesso/projectboard/persistence/HierarchyTreeNode.sql"
    })
    public void existsByUserAndManagingUserTrueReturnsTrueWhenPresent() {
        // given / when / then
        compareExistsByUserAndManagingUserTrueWithExpectedExists("User1", true);
    }

    @Test
    @Sql({
            "classpath:de/adesso/projectboard/persistence/Users.sql",
            "classpath:de/adesso/projectboard/persistence/HierarchyTreeNode.sql"
    })
    public void existsByUserAndManagingUserTrueReturnsFalseWhenNotPresent() {
        // given / when / then
        compareExistsByUserAndManagingUserTrueWithExpectedExists("User3", false);
    }

    @Test
    @Sql({
            "classpath:de/adesso/projectboard/persistence/Users.sql",
            "classpath:de/adesso/projectboard/persistence/HierarchyTreeNode.sql"
    })
    public void existsByUserAndStaffContainingReturnsTrueWhenPresent() {
        // given / when / then
        compareExistsByUserAndStaffContainingWithExpectedExists("User2",4, true);
    }

    @Test
    @Sql({
            "classpath:de/adesso/projectboard/persistence/Users.sql",
            "classpath:de/adesso/projectboard/persistence/HierarchyTreeNode.sql"
    })
    public void existsByUserAndStaffContainingReturnsFalseWhenNotPresent() {
        // given / when / then
        compareExistsByUserAndStaffContainingWithExpectedExists("User1",4, true);
    }

    private void compareExistsByUserAndManagingUserTrueWithExpectedExists(String userId, boolean expectedExists) {
        // given
        var user = userRepo.findById(userId).orElseThrow();

        // when
        var actualExists = hierarchyTreeNodeRepo.existsByUserAndManagingUserTrue(user);

        // then
        assertThat(actualExists).isEqualTo(expectedExists);
    }

    private void compareExistsByUserAndStaffContainingWithExpectedExists(String userId, long nodeId, boolean expectedExists) {
        // given
        var user = userRepo.findById(userId).orElseThrow();

        var staffHierarchyNode = hierarchyTreeNodeRepo.findById(nodeId).orElseThrow();

        // when
        var actualExists = hierarchyTreeNodeRepo.existsByUserAndStaffContaining(user, staffHierarchyNode);

        // then
        assertThat(actualExists).isEqualTo(expectedExists);
    }

}