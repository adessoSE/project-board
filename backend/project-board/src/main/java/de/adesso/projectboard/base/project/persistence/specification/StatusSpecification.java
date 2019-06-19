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
public class StatusSpecification implements Specification<Project> {

    /**
     * The name of the field to match the status against.
     */
    private static final String STATUS_FIELD_NAME = "status";

    /**
     * The name of the field to match the lob of the user against.
     */
    private static final String LOB_FIELD_NAME = "lob";

    /**
     * The allowed status values for projects that do not require the
     * LoB of the project be equal to the LoB of the user.
     */
    private final Set<String> lobIndependentStatus;

    /**
     * The allowed status values for projects that do require the LoB
     * of the project to be equal to the LoB of the user.
     */
    private final Set<String> lobDependentStatus;

    /**
     * The LoB of the user to get the projects for.
     */
    private final String userLob;

    /**
     * Constructs a new instance. All values contained inside the
     * given {@code status} collection are transformed to lower
     * case values.
     *
     * @param lobIndependentStatus
     *          The allowed status values for projects that do not require the
     *          LoB of the project be equal to the {@code userLob}, not null.
     *
     * @param lobDependentStatus
     *          The allowed status values for projects that do require the LoB
     *          of the project to be equal to the {@code userLob}, not null.
     *
     * @param userLob
     *          The LoB of the user to find projects for, may be null.
     *
     */
    public StatusSpecification(@NonNull Collection<String> lobIndependentStatus, @NonNull Collection<String> lobDependentStatus, String userLob) {
        this.lobIndependentStatus = allToLowerCase(lobIndependentStatus);
        this.lobDependentStatus = allToLowerCase(lobDependentStatus);
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
     *          A {@code Predicate} that matches any project if at least one of the
     *          following conditions is met:
     *          <ul>
     *              <li>the LoB of the project IS {@code null} AND the status is present in the {@code lobIndependentStatus} set</li>
     *              <li>the LoB of the project is NOT {@code null} AND equal to the {@code userLob} AND the status
     *              IS present in the {@code lobDependentStatus} set</li>
     *          </ul>
     *          All matching is case-insensitive.
     */
    @Override
    public Predicate toPredicate(Root<Project> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        var lowerCaseStatusExpression = criteriaBuilder.lower(root.get(STATUS_FIELD_NAME));
        var lowerCaseLobExpression = criteriaBuilder.lower(root.get(LOB_FIELD_NAME));

        if(lobDependentStatus.isEmpty() && lobIndependentStatus.isEmpty()) {
            return criteriaBuilder.or(); // a single or does not match any project
        }

        var statusMatchesPredicate = buildStatusMatchesPredicate(criteriaBuilder, lowerCaseStatusExpression, lowerCaseLobExpression);
        var lobAndStatusMatchesPredicate = buildLobAndStatusMatchesPredicate(criteriaBuilder, lowerCaseStatusExpression, lowerCaseLobExpression);

        return criteriaBuilder.or(statusMatchesPredicate, lobAndStatusMatchesPredicate);
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
     *          A predicate that matches if the project LoB is {@code null} {@code (A)} AND the status is
     *          present in the {@code lobDependentStatus} set {@code (B)} OR the status is present in the
     *          {@code lobIndependentStatus} set {@code (C)}. {@code ((A & B) | C) }
     */
    private Predicate buildStatusMatchesPredicate(CriteriaBuilder criteriaBuilder, Expression<String> lowerCaseStatusExpr, Expression<String> lowerCaseLobExpr) {
        var lobIndependentStatusPredicate = buildStatusMatchesPredicate(criteriaBuilder, lowerCaseStatusExpr, lobIndependentStatus);
        var lobDependentStatusPredicate = buildStatusMatchesPredicate(criteriaBuilder, lowerCaseStatusExpr, lobDependentStatus);
        var lobNullPredicate = criteriaBuilder.isNull(lowerCaseLobExpr);
        var lobNullAndLobDependentStatusPredicate = criteriaBuilder.and(lobNullPredicate, lobDependentStatusPredicate);

        return criteriaBuilder.or(lobIndependentStatusPredicate, lobNullAndLobDependentStatusPredicate);
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
     *          A predicate that matches if the lob of the project is equal to the {@code userLob}
     *          AND the status of the project is present in the {@code lobDependentStatus} set.
     *
     */
    private Predicate buildLobAndStatusMatchesPredicate(CriteriaBuilder criteriaBuilder, Expression<String> lowerCaseStatusExpr, Expression<String> lowerCaseLobExpression) {
        if(userLob == null) {
           return criteriaBuilder.or(); // a single or does not matches any project
        }

        var statusMatchesPredicate = buildStatusMatchesPredicate(criteriaBuilder, lowerCaseStatusExpr, lobDependentStatus);
        var lobMatchesPredicate = criteriaBuilder.equal(lowerCaseLobExpression, userLob);

        return criteriaBuilder.and(lobMatchesPredicate, statusMatchesPredicate);
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
