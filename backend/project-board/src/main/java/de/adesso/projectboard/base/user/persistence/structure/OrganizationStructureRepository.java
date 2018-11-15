package de.adesso.projectboard.base.user.persistence.structure;

import de.adesso.projectboard.base.user.persistence.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * {@link JpaRepository} to persist {@link OrganizationStructure} instances.
 */
public interface OrganizationStructureRepository extends JpaRepository<OrganizationStructure, Long> {

    Optional<OrganizationStructure> findByUser(User user);

    boolean existsByUser(User user);

    @Query("SELECT COUNT(o) " +
            "FROM OrganizationStructure AS o " +
            "WHERE o.user = :user " +
            "AND o.staffMembers IS NOT EMPTY")
   boolean existsByUserAndStaffMembersNotEmpty(@Param("user") User user);

}
