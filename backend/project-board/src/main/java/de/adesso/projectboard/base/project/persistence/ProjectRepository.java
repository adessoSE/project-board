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

    /**
     * A normal user can see all escalated and open projects only when the user's
     * LoB is the same as the project's one. Projects where no LoB is
     * set {@code lob = null} are included as well.
     *
     * @param lob
     *          The LoB of the user to get the projects for.
     *
     * @param page
     *          The pageable for pagination.
     *
     * @return
     *          The {@link Page} with all {@link Project}s.
     *
     * @see #findAllForUser(String, Sort)
     */
    @Query("SELECT p " +
            "FROM Project AS p " +
            "WHERE (LOWER(p.status) = 'eskaliert') " +
            "OR (LOWER(p.status) = 'open' AND (LOWER(p.lob) = LOWER(:lob) OR p.lob IS NULL))")
    Page<Project> findAllForUserPageable(@Param("lob") String lob, Pageable page);

    /**
     *
     * @param lob
     *          The LoB of the user to get the projects for.
     *
     * @param sort
     *          The sort to apply.
     *
     * @return
     *          The content of the returned page of {@link #findAllForUserPageable(String, Pageable)}
     *          with the given {@code lob}, {@code sort} and a {@link PageRequest}
     *          with no page size limit as the arguments.
     *
     * @see #findAllForUserPageable(String, Pageable)
     */
    default List<Project> findAllForUser(String lob, Sort sort) {
        return findAllForUserPageable(lob, PageRequest.of(0, Integer.MAX_VALUE, sort)).getContent();
    }

    /**
     * Managers can see all open and escalated {@link Project}s.
     *
     * @param page
     *          The pageable for pagination.
     *
     * @return
     *          The {@link Page} with all {@link Project}s.
     */
    @Query("SELECT p " +
            "FROM Project AS p " +
            "WHERE (LOWER(p.status) = 'eskaliert') " +
            "OR (LOWER(p.status) = 'open')")
    Page<Project> findAllForManagerPageable(Pageable page);

    /**
     *
     * @param sort
     *          The sort to apply.
     *
     * @return
     *          The content of the returned page of {@link #findAllForManagerPageable(Pageable)}
     *          with the given {@code sort} and a {@link PageRequest}
     *          with no page size limit as the arguments.
     *
     * @see #findAllForManagerPageable(Pageable)
     */
    default List<Project> findAllForManager(Sort sort) {
        return findAllForManagerPageable(PageRequest.of(0, Integer.MAX_VALUE, sort)).getContent();
    }

    /**
     *
     * @param lob
     *          The LoB of the user to get the projects for.
     *
     * @param keyword
     *          The keyword to search for.
     *
     * @param page
     *          The pageable for pagination.
     *
     * @return
     *          A {@link Page} with all {@link Project}.
     */
    @Query("SELECT p " +
            "FROM Project AS p " +
            "WHERE ((LOWER(p.status) = 'eskaliert') OR (LOWER(p.status) = 'open'))" +
            "AND (LOWER(p.title) like LOWER(CONCAT('%', :keyword, '%')) " +
                "OR LOWER(p.skills) like LOWER(CONCAT('%', :keyword, '%')) " +
                "OR LOWER(p.job) like LOWER(CONCAT('%', :keyword, '%')) " +
                "OR LOWER(p.description) like LOWER(CONCAT('%', :keyword, '%'))" +
            ")" +
            "AND (LOWER(p.lob) = LOWER(:lob) OR p.lob IS NULL)")
    Page<Project> findAllForUserByKeywordPageable(@Param("lob") String lob, @Param("keyword") String keyword, Pageable page);

    /**
     *
     * @param lob
     *          The LoB of the user to get the projects for.
     *
     * @param keyword
     *          The keyword to search for.
     *
     * @param sort
     *          The sort to apply.
     *
     * @return
     *        The content of the returned page of {@link #findAllForUserByKeywordPageable(String, String, Pageable)}
     *        with the given {@code lob}, {@code keyword}, {@code sort} and a {@link PageRequest}
     *        with no page size limit as the arguments.
     *
     * @see #findAllForUserByKeywordPageable(String, String, Pageable)
     */
    default List<Project> findAllForUserByKeyword(String lob, String keyword, Sort sort) {
        return findAllForUserByKeywordPageable(lob, keyword, PageRequest.of(0, Integer.MAX_VALUE, sort)).getContent();
    }

    /**
     *
     * @param keyword
     *          The keyword to search for.
     *
     * @param page
     *          The pageable for pagination.
     *
     * @return
     *          The {@link Page} with all {@link Project}s.
     */
    @Query("SELECT p " +
            "FROM Project p " +
            "WHERE ((LOWER(p.status) = 'eskaliert') OR (LOWER(p.status) = 'open')) " +
            "AND (LOWER(p.title) like LOWER(CONCAT('%', :keyword, '%')) " +
                "OR LOWER(p.skills) like LOWER(CONCAT('%', :keyword, '%')) " +
                "OR LOWER(p.job) like LOWER(CONCAT('%', :keyword, '%')) " +
                "OR LOWER(p.description) like LOWER(CONCAT('%', :keyword, '%'))" +
            ")")
    Page<Project> findAllForManagerByKeywordPageable(@Param("keyword") String keyword, Pageable page);

    /**
     *
     * @param keyword
     *          The keyword to search for.
     *
     * @param sort
     *          The sort to apply.
     *
     * @return
     *          The content of the returned page of {@link #findAllForManagerByKeywordPageable(String, Pageable)}
     *          with the given {@code keyword}, {@code sort} and a {@link PageRequest}
     *          with no page size limit as the arguments.
     *
     * @see #findAllForManagerByKeywordPageable(String, Pageable)
     */
    default List<Project> findAllForManagerByKeyword(String keyword, Sort sort) {
        return findAllForManagerByKeywordPageable(keyword, PageRequest.of(0, Integer.MAX_VALUE, sort)).getContent();
    }

}
