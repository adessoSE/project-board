package de.adesso.projectboard.core.base.rest.project.persistence;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * {@link CrudRepository} for persisting {@link Project}s.
 */
public interface ProjectRepository extends CrudRepository<Project, String> {

    List<Project> findAllByStatusOrStatusIgnoreCaseOrderByCreatedDesc(String status, String otherStatus);

    // a normal user can see escalated projects and open ones when the user's
    // LOB is the same as the project's one
    // projects where no LOB is set (lob = null) are included as well
    @Query("SELECT p " +
            "FROM Project AS p " +
            "WHERE (lower(p.status) = lower('eskaliert')) " +
            "OR (lower(p.status) = lower('offen') AND (lower(p.lob) = lower(:lob) OR p.lob IS NULL))" +
            "ORDER BY p.updated DESC")
    List<Project> getAllForUserOfLob(String lob);

    // super users can see all escalated and open projects
    @Query("SELECT p " +
            "FROM Project AS p " +
            "WHERE (lower(p.status) = lower('eskaliert')) " +
            "OR (lower(p.status) = lower('offen'))" +
            "ORDER BY p.updated DESC")
    List<Project> getAllForSuperUser();

}
