package de.adesso.projectboard.core.base.rest.project.persistence;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * {@link CrudRepository} for persisting {@link Project}s.
 */
public interface ProjectRepository extends CrudRepository<Project, String> {

    // a normal user can see escalated projects and open ones when the user's
    // LOB is the same as the project's one
    // projects where no LOB is set (lob = null) are included as well
    @Query("SELECT p " +
            "FROM Project AS p " +
            "WHERE (LOWER(p.status) = LOWER('eskaliert')) " +
            "OR (LOWER(p.status) = LOWER('offen') AND (LOWER(p.lob) = LOWER(:lob) OR p.lob IS NULL))" +
            "ORDER BY p.updated DESC")
    List<Project> findAllByStatusEscalatedOrOpenOrSameLob(@Param("lob") String lob);

    // super users can see all escalated and open projects
    @Query("SELECT p " +
            "FROM Project AS p " +
            "WHERE (LOWER(p.status) = LOWER('eskaliert')) " +
            "OR (LOWER(p.status) = LOWER('offen'))" +
            "ORDER BY p.updated DESC")
    List<Project> findAllByStatusEscalatedOrOpen();

    @Query("SELECT p " +
            "FROM Project AS p " +
            "WHERE ((LOWER(p.status) = LOWER('eskaliert')) OR (LOWER(p.status) = LOWER('offen')))" +
            "AND (LOWER(p.title) like LOWER(CONCAT('%', :keyword, '%')) " +
                "OR LOWER(p.skills) like LOWER(CONCAT('%', :keyword, '%')) " +
                "OR LOWER(p.job) like LOWER(CONCAT('%', :keyword, '%')) " +
                "OR LOWER(p.description) like LOWER(CONCAT('%', :keyword, '%'))" +
            ")" +
            "AND (LOWER(p.lob) = LOWER(:lob) OR p.lob IS NULL)" +
            "ORDER BY p.updated DESC")
    List<Project> findAllByStatusEscalatedOrOpenOrSameLobContainsKeyword(@Param("lob") String lob, @Param("keyword") String keyword);

    @Query("SELECT p " +
            "FROM Project p " +
            "WHERE ((LOWER(p.status) = LOWER('eskaliert')) OR (LOWER(p.status) = LOWER('offen'))) " +
            "AND (LOWER(p.title) like LOWER(CONCAT('%', :keyword, '%')) " +
                "OR LOWER(p.skills) like LOWER(CONCAT('%', :keyword, '%')) " +
                "OR LOWER(p.job) like LOWER(CONCAT('%', :keyword, '%')) " +
                "OR LOWER(p.description) like LOWER(CONCAT('%', :keyword, '%'))" +
            ") ORDER BY p.updated DESC")
    List<Project> findAllByStatusEscalatedOrOpenContainsKeyword(@Param("keyword") String keyword);



}
