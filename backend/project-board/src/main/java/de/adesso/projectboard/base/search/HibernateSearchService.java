package de.adesso.projectboard.base.search;

import de.adesso.projectboard.base.project.persistence.Project;
import de.adesso.projectboard.base.user.persistence.User;
import de.adesso.projectboard.base.user.persistence.data.UserData;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.hibernate.search.query.dsl.QueryBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@Slf4j
public class HibernateSearchService {

    private static final int MAX_CLAUSE_COUNT = 4096;

    private static final String LOB_FIELD_NAME = "lob";

    private static final String STATUS_FIELD_NAME = "status";

    /*
     * A transactional entity manager to use when searching. Required because an extended
     * persistence context entity manager will return entities that differ from the entities returned by
     * spring repositories, which causes the equals() and hashCode() methods to return
     * unexpected results, mainly due to the weird implementation of Hibernate's PersistentBag.
     */
    @PersistenceContext
    EntityManager entityManager;

    /**
     * A map to store the names of all indexed fields of a specific
     * class.
     */
    final Map<Class<?>, List<String>> classIndexedFieldMap;

    /**
     * A set of the values of the status field that add
     * additional constraints to the lob field.
     */
    private final Set<String> statusWithLobConstraint;

    /**
     * A set of the values of the status field of projects that should
     * not be included.
     */
    private final Set<String> excludedStatus;

    /**
     *
     * @param statusWithLobConstraint
     *          A collection of the values of the {@value STATUS_FIELD_NAME} field
     *          that add additional constraints to the lob field, not {@code null}.
     *
     * @param excludedStatus
     *          A collection of the values of the {@value STATUS_FIELD_NAME} field
     *          of projects that should not be included in any result.
     */
    public HibernateSearchService(@NotNull Collection<String> statusWithLobConstraint, @NotNull Collection<String> excludedStatus) {
        // increase the max clause count to allow searching for
        // staff members of users with more than 1024 staff members
        BooleanQuery.setMaxClauseCount(MAX_CLAUSE_COUNT);

        this.classIndexedFieldMap = new HashMap<>();
        this.statusWithLobConstraint = allToLowerCase(statusWithLobConstraint);
        this.excludedStatus =  allToLowerCase(excludedStatus);
    }

    /**
     *
     * @param simpleQueryString
     *          The query to evaluate, not {@code null}.
     *
     * @param lob
     *          The lob of the projects which's status indicates a constrained to
     *          the lob to search for, may be {@code null}.
     *
     * @return
     *          A list of all found projects.
     */
    @SuppressWarnings("unchecked")
    public List<Project> searchProjects(@NonNull String simpleQueryString, String lob) {
        var query = getProjectBaseQuery(simpleQueryString, lob);

        return getFullTextEntityManager().createFullTextQuery(query, Project.class)
                .getResultList();
    }

    /**
     *
     * @param simpleQueryString
     *          The query to evaluate, not {@code null}.
     *
     * @param pageable
     *          The pageable to get the paging information from, not {@code null}.
     *
     * @param lob
     *          The lob of the projects which's status indicates a constrained to
     *          the lob to search for, may be {@code null}.
     *
     * @return
     *          A page of all found projects.
     */
    @SuppressWarnings("unchecked")
    public Page<Project> searchProjects(@NonNull String simpleQueryString, @NonNull Pageable pageable, String lob) {
        var query = getProjectBaseQuery(simpleQueryString, lob);

        var firstIndex = pageable.getPageNumber() * pageable.getPageSize();

        var jpaQuery = getFullTextEntityManager().createFullTextQuery(query, Project.class)
                .setFirstResult(firstIndex)
                .setMaxResults(pageable.getPageSize());
        var resultSize = jpaQuery.getResultSize();
        var resultContent = jpaQuery.getResultList();

        return new PageImpl<>(resultContent, pageable, resultSize);
    }

    /**
     *
     * @param users
     *          The list of users to search the user data for, not {@code null}.
     *
     * @param simpleQueryString
     *          The simple query to evaluate, not {@code null}.
     *
     * @return
     *          A list of all user data instances matching the given {@code simpleQueryString}.
     */
    @SuppressWarnings("unchecked")
    public List<UserData> searchUserData(@NonNull List<User> users, @NonNull String simpleQueryString) {
        if(users.isEmpty()) {
            return Collections.emptyList();
        }

        var baseQuery = getQuerySearchingForAllIndexedFields(UserData.class, simpleQueryString);
        var queryBuilder = getQueryBuilder(UserData.class);

        var userIds = users.stream()
                .map(User::getId)
                .collect(Collectors.toSet());
        var idDisjunctionQuery = queryBuilder.simpleQueryString()
                .onField("user_id")
                .matching(HibernateSimpleQueryUtils.createLuceneQueryString(userIds, "|"))
                .createQuery();

        var boolQuery = queryBuilder.bool()
                .must(idDisjunctionQuery)
                .must(baseQuery)
                .createQuery();

        return getFullTextEntityManager().createFullTextQuery(boolQuery, UserData.class)
                .getResultList();
    }

    private Query getProjectBaseQuery(String simpleQueryString, String lob) {
        var queryBuilder = getQueryBuilder(Project.class);

        var baseQuery = getQuerySearchingForAllIndexedFields(Project.class, simpleQueryString);
        var excludeStatusQuery = buildNotInQuery(queryBuilder, STATUS_FIELD_NAME, excludedStatus);
        var lobIndependentOrLobNullOrEqualQuery = buildLobIndependentOrLobNullOrEqualQuery(queryBuilder, lob);

        return queryBuilder.bool()
                .must(baseQuery)
                .must(excludeStatusQuery)
                .must(lobIndependentOrLobNullOrEqualQuery)
                .createQuery();
    }

    /**
     * Builds a query that matches all projects
     * <ul>
     *     <li>whose status is not included in the {@code excludedStatus} set</li>
     *     <li>whose status is not included in the {@code statusWithLobConstraint} set</li>
     *     <li>whose LoB is {@code null}</li>
     *     <li>whose status is included in the {@code statusWithLobConstraint} and whose LoB is equal
     *     to the given {@code lob}</li>
     * </ul>
     *
     * @param queryBuilder
     *          The query builder to use.
     *
     * @param lob
     *          The lob of the projects to include in the result as described above.
     *
     * @return
     *          A query as described above, or {@code null} in case the
     *          {@code statusWithLobConstrained} set is empty.
     */
    private Query buildLobIndependentOrLobNullOrEqualQuery(QueryBuilder queryBuilder, String lob) {
        if(statusWithLobConstraint.isEmpty()) {
            return null;
        }

        var lobIndependentOrLobNullOrEqualsQuery = queryBuilder
                .bool();

        var lobIndependentQuery = buildNotInQuery(queryBuilder, STATUS_FIELD_NAME, statusWithLobConstraint);
        lobIndependentOrLobNullOrEqualsQuery.should(lobIndependentQuery);

        var lobNullQuery = queryBuilder
                .keyword()
                .onField(LOB_FIELD_NAME)
                .matching("_null_")
                .createQuery();
        lobIndependentOrLobNullOrEqualsQuery.should(lobNullQuery);

        if(lob != null) {
            var lowerCaseLob = lob.toLowerCase();
            var lobDependentQuery = buildInQuery(queryBuilder, STATUS_FIELD_NAME, statusWithLobConstraint);
            var lobEqualsQuery = queryBuilder
                    .phrase()
                    .onField(LOB_FIELD_NAME)
                    .sentence(lowerCaseLob)
                    .createQuery();

            var q = queryBuilder.bool()
                    .must(lobDependentQuery)
                    .must(lobEqualsQuery)
                    .createQuery();

            lobIndependentOrLobNullOrEqualsQuery.should(q);
        }

        return lobIndependentOrLobNullOrEqualsQuery.createQuery();
    }

    /**
     *
     * @param queryBuilder
     *          The query builder to use.
     *
     * @param fieldName
     *          The field name to use.
     *
     * @param unwantedValues
     *          A collection of all unwanted values, may be empty.
     *
     * @return
     *      A query matching all entities whose value of the field specified by the given {@code fieldName}
     *      is not included in the given {@code unwantedValues} collection, or {@code null} in case
     *      the given {@code unwantedValues} collection is empty.
     */
    private Query buildNotInQuery(QueryBuilder queryBuilder, String fieldName, Collection<String> unwantedValues) {
        if(unwantedValues.isEmpty()) {
            return null;
        }

        var inQuery = buildInQuery(queryBuilder, fieldName, unwantedValues);

        return queryBuilder
                .bool()
                .must(inQuery).not()
                .createQuery();
    }

    /**
     *
     * @param queryBuilder
     *          The query builder to use.
     *
     * @param fieldName
     *          The field name to use.
     *
     * @param wantedValues
     *          A collection of all wanted values, may be empty.
     *
     * @return
     *      A query matching all entities whose value of the field specified by the given {@code fieldName}
     *      is included in the given {@code unwantedValues} collection, or {@code null} in case
     *      the given {@code unwantedValues} collection is empty.
     */
    private Query buildInQuery(QueryBuilder queryBuilder, String fieldName, Collection<String> wantedValues) {
        if(wantedValues.isEmpty()) {
            return null;
        }

        var inQuery = queryBuilder.bool();

        wantedValues.stream()
                .map(wantedValue -> buildFieldValueEqualsQuery(queryBuilder, fieldName, wantedValue))
                .forEach(inQuery::should);

        return inQuery.createQuery();
    }

    private Query buildFieldValueEqualsQuery(QueryBuilder queryBuilder, String fieldName, String value) {
        if(value.contains(" ")) {
            return queryBuilder.phrase()
                    .onField(fieldName)
                    .sentence(value)
                    .createQuery();
        } else {
            return queryBuilder.keyword()
                    .onField(fieldName)
                    .matching(value)
                    .createQuery();
        }
    }

    /**
     *
     * @param entityType
     *          The type to search for, not null. Must be annotated with {@link Entity} and
     *          must have at least one {@code String} field annotated with
     *          {@link org.hibernate.search.annotations.Field}.
     *
     * @param simpleQueryString
     *          The simple query string to create the query from, not null.
     *
     * @return
     *          A query searching for the given {@code entityType} in all {@code String} fields
     *          annotated with {@link org.hibernate.search.annotations.Field} weighted equally.
     */
    Query getQuerySearchingForAllIndexedFields(Class<?> entityType, String simpleQueryString) {
        if(!isIndexedEntity(entityType)) {
            throw new IllegalArgumentException("Given type is not an entity or is not indexed!");
        }

        var annotatedStringFields = getNamesOfAnnotatedStringFields(entityType);
        if(annotatedStringFields.isEmpty()) {
            throw new IllegalArgumentException("No field of type String annotated with @Field!");
        }

        var fuzzyAndPrefixQuery = HibernateSimpleQueryUtils.makeQueryPrefixAndFuzzy(simpleQueryString);

        var queryBuilder = getQueryBuilder(entityType);
        return queryBuilder.simpleQueryString()
                .onFields(annotatedStringFields.get(0), annotatedStringFields.subList(1, annotatedStringFields.size()).toArray(String[]::new))
                .withAndAsDefaultOperator()
                .matching(fuzzyAndPrefixQuery)
                .createQuery();
    }

    /**
     *
     * @param entityType
     *          The entity type to get the annotated field names of, not null.
     *
     * @return
     *          The names of all String type fields annotated with
     *          {@link Field} or the value of {@link Field#name()} in case
     *          it is not empty.
     */
    List<String> getNamesOfAnnotatedStringFields(Class<?> entityType) {
        if(classIndexedFieldMap.containsKey(entityType)) {
            return classIndexedFieldMap.get(entityType);
        }

        var indexedStringFields = Arrays.stream(entityType.getDeclaredFields())
                .filter(field -> String.class.equals(field.getType()) && Objects.nonNull(field.getAnnotation(Field.class)))
                .map(field -> {
                    var fieldAnnotation = field.getAnnotation(Field.class);

                    if(!fieldAnnotation.name().isEmpty()) {
                        return fieldAnnotation.name();
                    }

                    return field.getName();
                })
                .collect(Collectors.toList());
        classIndexedFieldMap.put(entityType, indexedStringFields);

        return indexedStringFields;
    }

    /**
     *
     * @param entityType
     *          The entity type to check, not null.
     *
     * @return
     *          {@code true}, iff the given {@code entityType} is annotated with
     *          {@link Entity} and {@link Indexed}.
     */
    boolean isIndexedEntity(Class<?> entityType) {
        return Objects.nonNull(entityType.getAnnotation(Entity.class)) &&
                Objects.nonNull(entityType.getAnnotation(Indexed.class));
    }

    /**
     *
     * @param initEntityManager
     *          The entity manager to create the lucene index
     *          for already existing entities with.
     */
    void indexExistingEntities(EntityManager initEntityManager) {
        try {
            Search.getFullTextEntityManager(initEntityManager)
                    .createIndexer()
                    .startAndWait();

            log.info("Successfully built lucene index!");
        } catch (InterruptedException ex) {
            log.error("Error building lucene index!", ex);
        }
    }

    /**
     *
     * @return
     *          A {@link FullTextEntityManager} backed by the
     *          injected transactional {@code entityManager}.
     */
    FullTextEntityManager getFullTextEntityManager() {
        return Search.getFullTextEntityManager(entityManager);
    }

    /**
     *
     * @param type
     *          The indexed entity type to get a query builder for, not null.
     *
     * @return
     *          A hibernate search query builder for the given {@code type}.
     */
    QueryBuilder getQueryBuilder(Class<?> type) {
        return getFullTextEntityManager().getSearchFactory()
                .buildQueryBuilder()
                .forEntity(type)
                .get();
    }

    private Set<String> allToLowerCase(Collection<String> collection) {
        return collection.stream()
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
    }

}
