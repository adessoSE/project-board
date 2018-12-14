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
     * A normal user can see all escalated ({@code status = 'eskaliert'}). Open projects ({@code status = 'open'})
     * can only be seen when at least one of the following conditions is fulfilled:
     *
     * <ul>
     *     <li>the {@link Project#lob LoB} of the project is set to {@code null} ({@code lob IS NULL})</li>
     *
     *     <li>the given {@code lob} is like the {@link Project project's LoB} with wildcards appended
     *     to the beginning and the end ({@code LOWER(lob) LIKE CONCAT('%', LOWER(project.lob), '%')})</li>
     * </ul>
     *
     * <p/>
     *
     * This is necessary because the {@link Project project's LoB} differs from a
     * {@link de.adesso.projectboard.base.user.persistence.data.UserData#lob user's LoB}
     * even when they should be equal because they are from the same LoB.
     * <p/>
     * Example:
     * <pre>
     *     LOB CROSS INDUSTRIES (CI) &ne; LOB Cross Industries
     *       (User LoB)                      (Project LoB)
     * </pre>
     *
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
            "OR (LOWER(p.status) = 'open' AND (p.lob IS NULL OR LOWER(:lob) LIKE CONCAT('%', LOWER(p.lob), '%')))")
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
     * Managers can see all open and escalated projects ({@code status = 'eskaliert' OR status = 'open'}).
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
     * Searches projects for a user from a given {@code lob} with a given {@code keyword}. Searches in
     * the following fields:
     * <ul>
     *     <li>{@link Project#title Title}</li>
     *     <li>{@link Project#skills Skills}</li>
     *     <li>{@link Project#job Job}</li>
     *     <li>{@link Project#description Description}</li>
     * </ul>
     *
     * <b>Note</b>: The same access restrictions as described in {@link #findAllForUser(String, Sort)}
     * are applied.
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
            "WHERE (" +
                "LOWER(p.status) = 'eskaliert'" +
                "OR (LOWER(p.status) = 'open' AND (p.lob IS NULL OR LOWER(:lob) LIKE CONCAT('%', LOWER(p.lob), '%')))" +
            ")" +
            "AND (LOWER(p.title) like LOWER(CONCAT('%', :keyword, '%')) " +
                "OR LOWER(p.skills) like LOWER(CONCAT('%', :keyword, '%')) " +
                "OR LOWER(p.job) like LOWER(CONCAT('%', :keyword, '%')) " +
                "OR LOWER(p.description) like LOWER(CONCAT('%', :keyword, '%'))" +
            ")")
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
     * Searches projects for a manager with a given {@code keyword}. Searches in
     * the following fields:
     * <ul>
     *     <li>{@link Project#title Title}</li>
     *     <li>{@link Project#skills Skills}</li>
     *     <li>{@link Project#job Job}</li>
     *     <li>{@link Project#description Description}</li>
     * </ul>
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
