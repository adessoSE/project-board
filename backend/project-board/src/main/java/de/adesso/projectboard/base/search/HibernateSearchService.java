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
import java.util.*;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@Slf4j
public class HibernateSearchService {

    private static final int MAX_CLAUSE_COUNT = 4096;

    /*
     * A transactional entity manager to use when searching. Required because an extended
     * persistence context entity manager will return entities that differ from the entities returned by
     * spring repositories, which causes the equals() and hashCode() methods to return
     * unexpected results, mainly due to the weird implementation of Hibernate's PersistentBag.
     */
    @PersistenceContext
    EntityManager entityManager;

    final Map<Class<?>, List<String>> classIndexedFieldMap;

    final SimpleQueryEnhancer simpleQueryEnhancer;

    public HibernateSearchService(SimpleQueryEnhancer simpleQueryEnhancer) {
        // increase the max clause count to allow searching for
        // staff members of users with more than 1024 staff members
        BooleanQuery.setMaxClauseCount(MAX_CLAUSE_COUNT);

        this.classIndexedFieldMap = new HashMap<>();
        this.simpleQueryEnhancer = simpleQueryEnhancer;
    }

    Query getProjectBaseQuery(@NonNull String simpleQueryString, @NonNull Set<String> status) {
        var baseQuery = getQuerySearchingForAllIndexedFields(Project.class, simpleQueryString);

        if(status.isEmpty()) {
            return baseQuery;
        }

        var queryBuilder = getQueryBuilder(Project.class);
        var statusQuery = queryBuilder.simpleQueryString()
                .onField("status")
                .matching(createLuceneDisjunction(status))
                .createQuery();
        return queryBuilder.bool()
                .must(statusQuery)
                .must(baseQuery)
                .createQuery();
    }

    @SuppressWarnings("unchecked")
    public List<Project> searchProjects(@NonNull String simpleQueryString, @NonNull Set<String> status) {
        var query = getProjectBaseQuery(simpleQueryString, status);

        return getFullTextEntityManager().createFullTextQuery(query, Project.class)
                .getResultList();
    }

    @SuppressWarnings("unchecked")
    public Page<Project> searchProjects(@NonNull String simpleQueryString, @NonNull Set<String> status, @NonNull Pageable pageable) {
        var query = getProjectBaseQuery(simpleQueryString, status);

        var firstIndex = pageable.getPageNumber() * pageable.getPageSize();

        var jpaQuery = getFullTextEntityManager().createFullTextQuery(query, Project.class)
                .setFirstResult(firstIndex)
                .setMaxResults(pageable.getPageSize());
        var resultSize = jpaQuery.getResultSize();
        var resultContent = jpaQuery.getResultList();

        return new PageImpl<>(resultContent, pageable, resultSize);
    }

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
                .matching(createLuceneDisjunction(userIds))
                .createQuery();

        var boolQuery = queryBuilder.bool()
                .must(idDisjunctionQuery)
                .must(baseQuery)
                .createQuery();

        return getFullTextEntityManager().createFullTextQuery(boolQuery, UserData.class)
                .getResultList();
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

        var queryBuilder = getQueryBuilder(entityType);
        return queryBuilder.simpleQueryString()
                .onFields(annotatedStringFields.get(0), annotatedStringFields.subList(1, annotatedStringFields.size()).toArray(String[]::new))
                .withAndAsDefaultOperator()
                .matching(simpleQueryString)
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
    void initialize(EntityManager initEntityManager) {
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

    /**
     *
     * @param values
     *          The values to create the matching string for, not null
     *          and not empty.
     *
     * @return
     *          A lucene simple query string.
     */
    String createLuceneDisjunction(Set<String> values) {
        var valueArr = values.toArray(String[]::new);
        var fieldMatchStringBuilder = new StringBuilder(valueArr[0]);

        for(var valueIndex = 1; valueIndex < valueArr.length; valueIndex++) {
            fieldMatchStringBuilder
                    .append(" | ")
                    .append(valueArr[valueIndex]);
        }

        return fieldMatchStringBuilder.toString();
    }

}
