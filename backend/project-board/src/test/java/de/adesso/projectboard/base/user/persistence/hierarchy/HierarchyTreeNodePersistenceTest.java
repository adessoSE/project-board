package de.adesso.projectboard.base.user.persistence.hierarchy;

import de.adesso.projectboard.base.user.persistence.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;

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



}