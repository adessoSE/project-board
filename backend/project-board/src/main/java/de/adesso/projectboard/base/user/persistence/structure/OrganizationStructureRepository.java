package de.adesso.projectboard.base.user.persistence.structure;

import de.adesso.projectboard.base.user.persistence.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * {@link JpaRepository} to persist {@link OrganizationStructure} instances.
 */
public interface OrganizationStructureRepository extends JpaRepository<OrganizationStructure, Long> {

    Optional<OrganizationStructure> findByUser(User user);

    List<OrganizationStructure> findAllByUserIn(Collection<User> users);

    boolean existsByUser(User user);

    boolean existsByUserAndUserIsManager(User user, boolean manager);

    boolean existsByUserAndStaffMembersContaining(User user, User staffMember);

}
