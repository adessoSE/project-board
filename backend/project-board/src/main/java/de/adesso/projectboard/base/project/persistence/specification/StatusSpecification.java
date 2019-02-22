package de.adesso.projectboard.base.project.persistence.specification;

import de.adesso.projectboard.base.project.persistence.Project;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A {@link Specification} implementation to match {@link Project}s with
 * a given status.
 */
@EqualsAndHashCode
public class StatusSpecification implements Specification<Project> {

    /**
     * The name of the field to match against.
     */
    private static final String STATUS_FIELD_NAME = "status";

    /**
     * The allowed status values.
     */
    private final Set<String> status;

    /**
     * Constructs a new instance. All values contained inside the
     * given {@code status} collection are transformed to lower
     * case values.
     *
     * @param status
     *          The allowed status values of the project, not null.
     */
    public StatusSpecification(@NonNull Collection<String> status) {
        this.status = allToLowerCase(status);
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
     *          A {@code Predicate} that matches any project which's status
     *          is equal to one of the status values contained in the {@code status}
     *          collection or any when no status is given.
     */
    @Override
    public Predicate toPredicate(Root<Project> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        var lowerCaseStatusExpression = criteriaBuilder.lower(root.get(STATUS_FIELD_NAME));
        var predicates = new LinkedHashSet<Predicate>();

        if(status.isEmpty()) {
            return criteriaBuilder.and();
        }

        for(var stat : status) {
            var statusEqualsPredicate = criteriaBuilder.equal(lowerCaseStatusExpression, stat);

            predicates.add(statusEqualsPredicate);
        }

        return criteriaBuilder.or(predicates.toArray(Predicate[]::new));
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
