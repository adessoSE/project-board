package de.adesso.projectboard.base.project.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * {@link JpaRepository} for persisting {@link Project}s.
 */
public interface ProjectRepository extends JpaRepository<Project, String> {


    @Query("SELECT p FROM Project AS p " +
            "WHERE LOWER(p.status) = 'open' OR LOWER(p.status) = 'offen' " +
            "OR LOWER(p.status) = 'escalated' OR LOWER(p.status) = 'eskaliert'")
    Page<Project> findAllByStatusEscalatedOrOpenPageable(Pageable pageable);

    default List<Project> findAllByStatusEscalatedOrOpen(Sort sort) {
        return findAllByStatusEscalatedOrOpenPageable(PageRequest.of(0, Integer.MAX_VALUE, sort))
                .getContent();
    }

    @Query("SELECT p FROM Project AS p " +
            "WHERE (LOWER(p.status) = 'open' OR LOWER(p.status) = 'offen' " +
            "OR LOWER(p.status) = 'escalated' OR LOWER(p.status) = 'eskaliert') " +
            "AND (LOWER(p.title) like LOWER(CONCAT('%', :keyword, '%')) " +
                "OR LOWER(p.skills) like LOWER(CONCAT('%', :keyword, '%')) " +
                "OR LOWER(p.job) like LOWER(CONCAT('%', :keyword, '%')) " +
                "OR LOWER(p.description) like LOWER(CONCAT('%', :keyword, '%'))" +
            ")")
    Page<Project> findAllByStatusEscalatedOrOpenAndKeywordPageable(@Param("keyword") String keyword, Pageable pageable);

    default List<Project> findAllByStatusEscalatedOrOpenAndKeyword(String keyword, Sort sort) {
        return findAllByStatusEscalatedOrOpenAndKeywordPageable(keyword, PageRequest.of(0, Integer.MAX_VALUE, sort))
                .getContent();
    }

}
