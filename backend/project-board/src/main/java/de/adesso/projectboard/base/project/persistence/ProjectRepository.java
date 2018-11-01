package de.adesso.projectboard.base.project.persistence;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * {@link JpaRepository} for persisting {@link Project}s.
 */
public interface ProjectRepository extends JpaRepository<Project, String> {

    // a normal user can see escalated projects and open ones when the user's
    // LOB is the same as the project's one
    // projects where no LOB is set (lob = null) are included as well
    @Query("SELECT p " +
            "FROM Project AS p " +
            "WHERE (LOWER(p.status) = 'eskaliert') " +
            "OR (LOWER(p.status) = 'offen' AND (LOWER(p.lob) = LOWER(:lob) OR p.lob IS NULL))")
    List<Project> findAllByStatusEscalatedOrOpenOrSameLob(@Param("lob") String lob, Sort sort);

    // super users can see all escalated and open projects
    @Query("SELECT p " +
            "FROM Project AS p " +
            "WHERE (LOWER(p.status) = 'eskaliert') " +
            "OR (LOWER(p.status) = 'offen')")
    List<Project> findAllByStatusEscalatedOrOpen(Sort sort);

    @Query("SELECT p " +
            "FROM Project AS p " +
            "WHERE ((LOWER(p.status) = 'eskaliert') OR (LOWER(p.status) = 'offen'))" +
            "AND (LOWER(p.title) like LOWER(CONCAT('%', :keyword, '%')) " +
                "OR LOWER(p.skills) like LOWER(CONCAT('%', :keyword, '%')) " +
                "OR LOWER(p.job) like LOWER(CONCAT('%', :keyword, '%')) " +
                "OR LOWER(p.description) like LOWER(CONCAT('%', :keyword, '%'))" +
            ")" +
            "AND (LOWER(p.lob) = LOWER(:lob) OR p.lob IS NULL)")
    List<Project> findAllByStatusEscalatedOrOpenOrSameLobContainsKeyword(@Param("lob") String lob, @Param("keyword") String keyword, Sort sort);

    @Query("SELECT p " +
            "FROM Project p " +
            "WHERE ((LOWER(p.status) = 'eskaliert') OR (LOWER(p.status) = 'offen')) " +
            "AND (LOWER(p.title) like LOWER(CONCAT('%', :keyword, '%')) " +
                "OR LOWER(p.skills) like LOWER(CONCAT('%', :keyword, '%')) " +
                "OR LOWER(p.job) like LOWER(CONCAT('%', :keyword, '%')) " +
                "OR LOWER(p.description) like LOWER(CONCAT('%', :keyword, '%'))" +
            ")")
    List<Project> findAllByStatusEscalatedOrOpenContainsKeyword(@Param("keyword") String keyword, Sort sort);

}
