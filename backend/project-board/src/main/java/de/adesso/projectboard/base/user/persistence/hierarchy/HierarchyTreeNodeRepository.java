package de.adesso.projectboard.base.user.persistence.hierarchy;

import de.adesso.projectboard.base.user.persistence.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * {@link JpaRepository} to persist {@link HierarchyTreeNode} entities.
 */
public interface HierarchyTreeNodeRepository extends JpaRepository<HierarchyTreeNode, Long> {

    Optional<HierarchyTreeNode> findByUser(User user);

    List<HierarchyTreeNode> findByUserIn(Iterable<User> users);

    boolean existsByUserAndManagingUserTrue(User user);

    boolean existsByUserAndStaffContaining(User user, HierarchyTreeNode staff);

}
