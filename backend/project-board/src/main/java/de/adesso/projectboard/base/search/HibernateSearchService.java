package de.adesso.projectboard.base.search;

import de.adesso.projectboard.base.project.persistence.Project;
import de.adesso.projectboard.base.user.persistence.User;
import de.adesso.projectboard.base.user.persistence.data.UserData;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.search.Query;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

// TODO: add method for project pagination

@Transactional(readOnly = true)
@Slf4j
public class HibernateSearchService {

    /**
     * A transactional entity manager to use when searching.
     * Required because an extended entity manager will return
     * entities that differ from the entities returned by
     * spring repositories, which causes the {@code equals()} and
     * {@code hashCode()} methods to return unexpected results.
     */
    @PersistenceContext
    private EntityManager entityManager;

    @SuppressWarnings("unchecked")
    public List<Project> searchProjects(@NonNull String simpleQueryString, String... status) {

        var baseQuery = getBaseQuery(Project.class, simpleQueryString);
        var queryBuilder = getFullTextEntityManager().getSearchFactory()
                .buildQueryBuilder()
                .forEntity(Project.class)
                .get();
        var statusQuery = queryBuilder.simpleQueryString()
                .onField("status")
                .matching("offen | open | eskaliert | escalated")
                .createQuery();
        var fullQuery = queryBuilder.bool()
                .must(statusQuery)
                .must(baseQuery)
                .createQuery();

        return getFullTextEntityManager().createFullTextQuery(fullQuery, Project.class)
                .getResultList();
    }

    @SuppressWarnings("unchecked")
    public List<UserData> searchUserData(@NonNull List<User> users, @NonNull String simpleQueryString) {
        var baseQuery = getBaseQuery(UserData.class, simpleQueryString);
        var queryBuilder = getFullTextEntityManager().getSearchFactory()
                .buildQueryBuilder()
                .forEntity(UserData.class)
                .get();

        var boolQueryJunction = queryBuilder.bool().minimumShouldMatchNumber(1);
        users.forEach(user -> boolQueryJunction.should(queryBuilder.keyword().onField("user_id").matching(user.getId()).createQuery()));
        var fullQuery = boolQueryJunction.must(baseQuery).createQuery();

        return getFullTextEntityManager().createFullTextQuery(fullQuery, UserData.class)
                .getResultList();
    }

    Query getBaseQuery(Class<?> entityType, String simpleQueryString) {
        if(!isIndexedEntity(entityType)) {
            throw new IllegalArgumentException("Given type is not an entity or is not indexed!");
        }

        var annotatedStringFields = getNamesOfAnnotatedStringFields(entityType);
        if(annotatedStringFields.isEmpty()) {
            throw new IllegalArgumentException("No field of type String annotated with @Field!");
        }

        var queryBuilder = getFullTextEntityManager().getSearchFactory()
                .buildQueryBuilder()
                .forEntity(entityType)
                .get();

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
     *          {@link org.hibernate.search.annotations.Field}.
     */
    List<String> getNamesOfAnnotatedStringFields(Class<?> entityType) {
        return Arrays.stream(entityType.getDeclaredFields())
                .filter(field -> String.class.equals(field.getType()) && Objects.nonNull(field.getAnnotation(org.hibernate.search.annotations.Field.class)))
                .map(Field::getName)
                .collect(Collectors.toList());
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

            log.debug("Successfully built lucene Index!");
        } catch (InterruptedException ex) {
            log.error("Error building lucene index!", ex);
        }
    }

    /**
     *
     * @return
     *          A {@link FullTextEntityManager} backed by the
     *          injected {@code entityManager}.
     */
    FullTextEntityManager getFullTextEntityManager() {
        return Search.getFullTextEntityManager(entityManager);
    }

}
