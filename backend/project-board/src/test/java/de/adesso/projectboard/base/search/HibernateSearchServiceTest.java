package de.adesso.projectboard.base.search;

import de.adesso.projectboard.base.project.persistence.Project;
import org.apache.lucene.search.Query;
import org.hibernate.search.MassIndexer;
import org.hibernate.search.SearchFactory;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.query.dsl.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class HibernateSearchServiceTest {

    @Mock
    private FullTextEntityManager fullTextEntityManagerMock;

    @Mock
    private QueryBuilder queryBuilderMock;

    @Mock
    private SimpleQueryStringContext simpleQueryStringContextMock;

    @Mock
    private BooleanJunction<BooleanJunction> booleanJunctionMock;

    @Mock
    private MustJunction mustJunctionMock;

    private HibernateSearchService hibernateSearchService;

    @Before
    public void setUp() {
        this.hibernateSearchService = new HibernateSearchService();
        this.hibernateSearchService.entityManager = fullTextEntityManagerMock;
    }

    @Test
    public void getProjectsBaseQueryReturnsBaseQueryWhenStatusIsEmpty() {
        // given
        var expectedQuery = mock(Query.class);

        var givenQuery = "java";
        var expectedFirstField = "status";
        var expectedOtherFields = new String[] { "title", "job", "skills", "description", "lob",
                "customer", "location", "operationStart", "operationEnd", "effort", "other" };

        var matchingContextMock = createSimpleQueryStringMatchingContext(givenQuery, expectedQuery);
        configureQueryBuilderSimpleQueryString(expectedFirstField, expectedOtherFields, matchingContextMock);
        configureGetQueryBuilderToReturnBuilderForType(Project.class);

        // when
        var actualQuery = hibernateSearchService.getProjectBaseQuery(givenQuery, Set.of());

        // then
        assertThat(actualQuery).isEqualTo(expectedQuery);
    }

    @Test
    public void getProjectBaseQueryReturnsExpectedQuery() {
        // given
        var expectedSimpleQueryStringQuery = mock(Query.class);
        var expectedStatusQuery = mock(Query.class);
        var expectedResultQuery = mock(Query.class);

        var givenSimpleQuery = "java";
        var givenStatusSet = Set.of("open");
        var expectedSimpleStatusQuery = "open";
        var expectedFirstField = "status";
        var expectedOtherFields = new String[] { "title", "job", "skills", "description", "lob",
                "customer", "location", "operationStart", "operationEnd", "effort", "other" };

        var givenQueryMatchingContext = createSimpleQueryStringMatchingContext(givenSimpleQuery, expectedSimpleQueryStringQuery);
        var statusQueryMatchingContext = createSimpleQueryStringMatchingContext(expectedSimpleStatusQuery, expectedStatusQuery);
        configureQueryBuilderSimpleQueryString(expectedFirstField, expectedOtherFields, givenQueryMatchingContext);
        configureQueryBuilderSimpleQueryString("status", statusQueryMatchingContext);
        configureGetQueryBuilderToReturnBuilderForType(Project.class);

        given(queryBuilderMock.bool()).willReturn(booleanJunctionMock);
        given(booleanJunctionMock.must(expectedStatusQuery)).willReturn(mustJunctionMock);
        given(mustJunctionMock.must(expectedSimpleQueryStringQuery)).willReturn(mustJunctionMock);
        given(mustJunctionMock.createQuery()).willReturn(expectedResultQuery);

        // when
        var actualQuery = hibernateSearchService.getProjectBaseQuery(givenSimpleQuery, givenStatusSet);

        // then
        assertThat(actualQuery).isEqualTo(expectedResultQuery);
    }

    @Test
    public void searchUserDataReturnsEmptyListWhenNoUsersGiven() {
        // given / when / then
        assertThat(hibernateSearchService.searchUserData(List.of(), ""))
                .isEmpty();
    }

    @Test
    public void getQuerySearchingForAllIndexedFieldsThrowsExceptionWhenNotAnEntity() {
        // given / when / then
        assertThatThrownBy(() -> hibernateSearchService.getQuerySearchingForAllIndexedFields(String.class, ""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Given type is not an entity or is not indexed!");
    }

    @Test
    public void getQuerySearchingForAllIndexedFieldsThrowsExceptionWhenEntityNotIndexed() {
        // given / when / then
        assertThatThrownBy(() -> hibernateSearchService.getQuerySearchingForAllIndexedFields(NonIndexedEntity.class, ""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Given type is not an entity or is not indexed!");
    }

    @Test
    public void getQuerySearchingForAllIndexedFieldsThrowsExceptionWhenNoStringFieldsAnnotated() {
        // given / when / then
        assertThatThrownBy(() -> hibernateSearchService.getQuerySearchingForAllIndexedFields(IndexedEntityWithoutFields.class, ""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("No field of type String annotated with @Field!");
    }

    @Test
    public void getQuerySearchingForAllFieldsReturnsExpectedQuery() {
        // given
        var expectedQuery = mock(Query.class);

        var givenType = IndexedEntity.class;
        var givenQuery = "param1 | param2";
        var expectedFirstField = "renamed_field";
        var expectedOtherFields = new String[] { "secondField" };

        var matchingContextMock = createSimpleQueryStringMatchingContext(givenQuery, expectedQuery);
        configureQueryBuilderSimpleQueryString(expectedFirstField, expectedOtherFields, matchingContextMock);
        configureGetQueryBuilderToReturnBuilderForType(givenType);

        // when
        var actualQuery = hibernateSearchService.getQuerySearchingForAllIndexedFields(givenType, givenQuery);

        // then
        assertThat(actualQuery).isEqualTo(expectedQuery);
    }

    @Test
    public void isIndexedEntityReturnsFalseWhenNotAnEntity() {
        // given / when / then
        assertThat(hibernateSearchService.isIndexedEntity(String.class)).isFalse();
    }

    @Test
    public void isIndexedEntityReturnsFalseWhenEntityNotIndexed() {
        // given / when / then
        assertThat(hibernateSearchService.isIndexedEntity(NonIndexedEntity.class)).isFalse();
    }

    @Test
    public void isIndexedEntityReturnsTrueWhenEntityIsIndexed() {
        // given / when / then
        assertThat(hibernateSearchService.isIndexedEntity(IndexedEntity.class)).isTrue();
    }

    @Test
    public void getNamesOfAnnotatedStringFields() {
        // given
        var expectedFieldNames = Set.of("renamed_field", "secondField");

        // when
        var actualFieldNames = hibernateSearchService.getNamesOfAnnotatedStringFields(IndexedEntity.class);

        // then
        assertThat(actualFieldNames).containsExactlyInAnyOrderElementsOf(expectedFieldNames);
    }

    @Test
    public void initializeIndexesExistingEntities() throws InterruptedException {
        // given
        var massIndexerMock = mock(MassIndexer.class);

        given(fullTextEntityManagerMock.createIndexer()).willReturn(massIndexerMock);

        // when
        hibernateSearchService.initialize(fullTextEntityManagerMock);

        // then
        verify(massIndexerMock).startAndWait();
    }

    @Test
    public void getFullTextEntityManager() {
        // given / when
        var actualFullTextEntityManager = hibernateSearchService.getFullTextEntityManager();

        // then
        assertThat(actualFullTextEntityManager).isEqualTo(fullTextEntityManagerMock);
    }

    @Test
    public void getQueryBuilderReturnsExpectedQueryBuilderForType() {
        // given
        var givenType = IndexedEntity.class;
        configureGetQueryBuilderToReturnBuilderForType(givenType);

        // when
        var actualQueryBuilder = hibernateSearchService.getQueryBuilder(givenType);

        // then
        assertThat(actualQueryBuilder).isEqualTo(queryBuilderMock);
    }

    @Test
    public void createLuceneDisjunctionReturnsElementWhenOnlyOneIsPresent() {
        // given
        var expectedDisjunction = "param1";
        var givenValues = Set.of(expectedDisjunction);

        // when
        var actualDisjunction = hibernateSearchService.createLuceneDisjunction(givenValues);

        // then
        assertThat(actualDisjunction).isEqualTo(expectedDisjunction);
    }

    @Test
    public void createLuceneDisjunctionReturnsExpectedDisjunction() {
        // given
        var expectedDisjunction = "param1 | param2 | param3";
        var givenValues = new LinkedHashSet<>(List.of("param1", "param2", "param3"));

        // when
        var actualDisjunction = hibernateSearchService.createLuceneDisjunction(givenValues);

        // then
        assertThat(actualDisjunction).isEqualTo(expectedDisjunction);
    }

    private void configureGetQueryBuilderToReturnBuilderForType(Class<?> entityType) {
        // given
        var searchFactoryMock = mock(SearchFactory.class);
        var queryContextBuilderMock = mock(QueryContextBuilder.class);
        var entityContextMock = mock(EntityContext.class);

        given(fullTextEntityManagerMock.getSearchFactory()).willReturn(searchFactoryMock);
        given(searchFactoryMock.buildQueryBuilder()).willReturn(queryContextBuilderMock);
        given(queryContextBuilderMock.forEntity(entityType)).willReturn(entityContextMock);
        given(entityContextMock.get()).willReturn(queryBuilderMock);
    }

    private void configureQueryBuilderSimpleQueryString(String field, SimpleQueryStringMatchingContext simpleQueryStringMatchingContext, SimpleQueryStringMatchingContext... simpleQueryStringMatchingContexts) {
        // given
        given(queryBuilderMock.simpleQueryString()).willReturn(simpleQueryStringContextMock);
        given(simpleQueryStringContextMock.onField(field)).willReturn(simpleQueryStringMatchingContext, simpleQueryStringMatchingContexts);
    }

    private void configureQueryBuilderSimpleQueryString(String firstField, String[] otherFields, SimpleQueryStringMatchingContext simpleQueryStringMatchingContext, SimpleQueryStringMatchingContext... simpleQueryStringMatchingContexts) {
        // given
        given(queryBuilderMock.simpleQueryString()).willReturn(simpleQueryStringContextMock);
        given(simpleQueryStringContextMock.onFields(firstField, otherFields)).willReturn(simpleQueryStringMatchingContext, simpleQueryStringMatchingContexts);
    }

    private SimpleQueryStringMatchingContext createSimpleQueryStringMatchingContext(String simpleQuery, Query query) {
        var simpleQueryStringMatchingContextMock = mock(SimpleQueryStringMatchingContext.class);
        var simpleQueryStringTerminationMock = mock(SimpleQueryStringTermination.class);

        given(simpleQueryStringMatchingContextMock.withAndAsDefaultOperator()).willReturn(simpleQueryStringMatchingContextMock);
        given(simpleQueryStringMatchingContextMock.matching(simpleQuery)).willReturn(simpleQueryStringTerminationMock);
        given(simpleQueryStringTerminationMock.createQuery()).willReturn(query);

        return simpleQueryStringMatchingContextMock;
    }

}
