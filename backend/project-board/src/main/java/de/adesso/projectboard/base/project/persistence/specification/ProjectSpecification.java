package de.adesso.projectboard.base.project.persistence.specification;

import de.adesso.projectboard.base.project.persistence.Project;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A {@link Specification} implementation to match {@link Project}s with
 * a given status and LoB. The predicate returned is described in detail
 * at {@link #toPredicate(Root, CriteriaQuery, CriteriaBuilder)}.
 */
@EqualsAndHashCode
public class ProjectSpecification implements Specification<Project> {

    /**
     * The name of the field to match the status against.
     */
    private static final String STATUS_FIELD_NAME = "status";

    /**
     * The name of the field to match the lob of the user against.
     */
    private static final String LOB_FIELD_NAME = "lob";

    /**
     * The allowed status values for projects that require the LoB
     * of the project to be equal to the LoB of the user or the LoB of the
     * project to be null.
     */
    private final Set<String> lobDependentStatus;

    /**
     * The status values of projects that should not be matched by
     * this specification.
     */
    private final Set<String> excludedStatus;

    /**
     * The lower case LoB of the user to get the projects for.
     */
    private final String userLob;

    /**
     * Constructs a new instance. All values contained inside the
     * given {@code status} collection are transformed to lower
     * case values.
     *
     * @param excludedStatus
     *          The status values of projects that should not be matched by this
     *          specification, not {@code null}.
     *
     * @param lobDependentStatus
     *          The allowed status values for projects that do require the LoB
     *          of the project to be equal to the {@code userLob}, not null.
     *
     * @param userLob
     *          The LoB of the user to find projects for, may be null.
     *
     */
    public ProjectSpecification(@NonNull Collection<String> excludedStatus, @NonNull Collection<String> lobDependentStatus, String userLob) {
        this.lobDependentStatus = allToLowerCase(lobDependentStatus);
        this.excludedStatus = allToLowerCase(excludedStatus);
        this.userLob = userLob == null ? null : userLob.toLowerCase();
    }

    /**
     *
     * @param root
     *          The root type in the from clause, not null.
     *
     * @param query
     *          The {@code CriteriaQuery}, may be null.
     *
     * @param criteriaBuilder
     *          The {@code CriteriaBuilder} to build the query with, not null.
     *
     * @return
     *          A {@code Predicate} matching all projects when the {@code lobDependentStatus} and
     *          {@code excludedStatus} are empty OR a {@code Predicate} matching projects which meet
     *          the following conditions:
     *          <ul>
     *              <li>the status of the project is not contained in the {@code excludedStatus} set</li>
     *              <li>(the status is contained in the {@code lobDependentStatus} set AND the {@code userLob}
     *              is equal to the project LoB) OR the project LoB is {@code null}</li>
     *          </ul>
     */
    @Override
    public Predicate toPredicate(Root<Project> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        var lowerCaseStatusExpression = criteriaBuilder.lower(root.get(STATUS_FIELD_NAME));
        var lowerCaseLobExpression = criteriaBuilder.lower(root.get(LOB_FIELD_NAME));

        if(lobDependentStatus.isEmpty() && excludedStatus.isEmpty()) {
            return criteriaBuilder.and(); // a single or matches all projects
        }

        var statusNotExcludedPredicate = buildStatusNotExcludedPredicate(criteriaBuilder, lowerCaseStatusExpression);
        var lobNullOrEqualToUserLobPredicate = buildLobNullOrEqualToUserLobPredicate(criteriaBuilder,
                lowerCaseStatusExpression, lowerCaseLobExpression);

        return criteriaBuilder.and(statusNotExcludedPredicate, lobNullOrEqualToUserLobPredicate);
    }

    /**
     *
     * @param criteriaBuilder
     *          The criteria builder, not {@code null}.
     *
     * @param lowerCaseStatusExpr
     *          The expression to match the lob dependent status against, not {@code null}.
     *
     * @return
     *          A predicate that matches if the project's status is not contained in the
     *          {@code excludedStatus} set.
     */
    private Predicate buildStatusNotExcludedPredicate(CriteriaBuilder criteriaBuilder, Expression<String> lowerCaseStatusExpr) {
        if(excludedStatus.isEmpty()) {
            // an empty "in" expression causes errors, so just return an
            // single "and" expression matching every project
            return criteriaBuilder.and();
        }

        return criteriaBuilder.not(lowerCaseStatusExpr.in(excludedStatus.toArray()));
    }

    /**
     *
     * @param criteriaBuilder
     *          The criteria builder, not {@code null}.
     *
     * @param lowerCaseStatusExpr
     *          The expression to match the lob dependent status against, not {@code null}.
     *
     * @param lowerCaseLobExpression
     *          The expression to match the user lob against, not {@code null}.
     *
     * @return
     *         A predicate that matches if the status of the project is not contained in the {@code lobDependentStatus}
     *         set ({@code A}) OR the LoB of the project is {@code null} ({@code B}) OR the {@code userLob}
     *         is not {@code null} ({@code C}) AND the project's LoB is equal to the {@code userLob} ({@code D})
     *         ({@code A | B | (C & D)}).
     */
    private Predicate buildLobNullOrEqualToUserLobPredicate(CriteriaBuilder criteriaBuilder, Expression<String> lowerCaseStatusExpr, Expression<String> lowerCaseLobExpression) {
        if(lobDependentStatus.isEmpty()) {
            // an empty "in" expression causes errors, so just return an
            // single "and" expression matching every project since every project
            // is matched either way
            return criteriaBuilder.and();
        }

        var lobIndependentStatusPredicate = criteriaBuilder.not(lowerCaseStatusExpr.in(lobDependentStatus.toArray()));

        var lobNullPredicate = criteriaBuilder.isNull(lowerCaseLobExpression);
        var lobEqualsPredicate = criteriaBuilder.or();

        if(userLob != null) {
            lobEqualsPredicate = criteriaBuilder.equal(lowerCaseLobExpression, userLob);
        }

        return criteriaBuilder.or(lobIndependentStatusPredicate, lobNullPredicate, lobEqualsPredicate);
    }

    /**
     *
     * @param criteriaBuilder
     *          The criteria builder, not {@code null}.
     *
     * @param lowerCaseStatusExpr
     *          The expression to match the status against, not {@code null}.
     *
     * @param status
     *          The status the projects status are matched againsted, not {@code null}.
     *
     * @return
     *          A predicate consisting of a disjunction of all status predicates.
     */
    private Predicate buildStatusMatchesPredicate(CriteriaBuilder criteriaBuilder, Expression<String> lowerCaseStatusExpr, Collection<String> status) {
        var statusMatchesPredicates = status.stream()
                .map(stat -> criteriaBuilder.equal(lowerCaseStatusExpr, stat))
                .collect(Collectors.toList());

        return criteriaBuilder.or(statusMatchesPredicates.toArray(Predicate[]::new));
    }

    /**
     *
     * @param status
     *          The collection containing all allowed status
     *          values, not null.
     *
     * @return
     *          A set containg all status values transformed to
     *          lower case.
     */
    Set<String> allToLowerCase(Collection<String> status) {
        return status.stream()
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
    }

}
