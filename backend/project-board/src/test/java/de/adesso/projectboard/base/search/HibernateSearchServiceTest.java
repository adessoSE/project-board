package de.adesso.projectboard.base.search;

import de.adesso.projectboard.base.project.persistence.Project;
import de.adesso.projectboard.base.user.persistence.User;
import de.adesso.projectboard.base.user.persistence.data.UserData;
import helper.search.IndexedEntity;
import helper.search.IndexedEntityWithoutFields;
import helper.search.NonIndexedEntity;
import org.apache.lucene.search.Query;
import org.hibernate.search.MassIndexer;
import org.hibernate.search.SearchFactory;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.FullTextQuery;
import org.hibernate.search.query.dsl.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

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
    private FullTextQuery fullTextQueryMock;

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
    public void searchProjectsNonPageableReturnsExpectedResult() {
        // given
        var expectedSimpleQueryStringQuery = mock(Query.class);
        var expectedStatusQuery = mock(Query.class);
        var expectedResultQuery = mock(Query.class);
        var expectedResult = List.of(mock(Project.class));

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

        given(fullTextEntityManagerMock.createFullTextQuery(expectedResultQuery, Project.class)).willReturn(fullTextQueryMock);
        given(fullTextQueryMock.getResultList()).willReturn(expectedResult);

        // when
        var actualResult = hibernateSearchService.searchProjects(givenSimpleQuery, givenStatusSet);

        // then
        assertThat(actualResult).isEqualTo(expectedResult);
    }

    @Test
    public void searchProjectsPageableReturnsExpectedResult() {
        var totalResults = 1;
        var pageSize = 20;
        var pageCount = 1;
        var givenPageable = PageRequest.of(pageCount, pageSize);
        var givenSimpleQuery = "java";
        var givenStatusSet = Set.of("open");
        var expectedSimpleStatusQuery = "open";
        var expectedFirstField = "status";
        var expectedOtherFields = new String[] { "title", "job", "skills", "description", "lob",
                "customer", "location", "operationStart", "operationEnd", "effort", "other" };

        var expectedSimpleQueryStringQuery = mock(Query.class);
        var expectedStatusQuery = mock(Query.class);
        var expectedResultQuery = mock(Query.class);
        var expectedProjects = List.of(mock(Project.class));
        var expectedResultPage = new PageImpl<>(expectedProjects, givenPageable, totalResults);

        var givenQueryMatchingContext = createSimpleQueryStringMatchingContext(givenSimpleQuery, expectedSimpleQueryStringQuery);
        var statusQueryMatchingContext = createSimpleQueryStringMatchingContext(expectedSimpleStatusQuery, expectedStatusQuery);
        configureQueryBuilderSimpleQueryString(expectedFirstField, expectedOtherFields, givenQueryMatchingContext);
        configureQueryBuilderSimpleQueryString("status", statusQueryMatchingContext);
        configureGetQueryBuilderToReturnBuilderForType(Project.class);

        given(queryBuilderMock.bool()).willReturn(booleanJunctionMock);
        given(booleanJunctionMock.must(expectedStatusQuery)).willReturn(mustJunctionMock);
        given(mustJunctionMock.must(expectedSimpleQueryStringQuery)).willReturn(mustJunctionMock);
        given(mustJunctionMock.createQuery()).willReturn(expectedResultQuery);

        given(fullTextEntityManagerMock.createFullTextQuery(expectedResultQuery, Project.class)).willReturn(fullTextQueryMock);
        given(fullTextQueryMock.setFirstResult(pageCount * pageSize)).willReturn(fullTextQueryMock);
        given(fullTextQueryMock.setMaxResults(pageSize)).willReturn(fullTextQueryMock);
        given(fullTextQueryMock.getResultSize()).willReturn(totalResults);
        given(fullTextQueryMock.getResultList()).willReturn(expectedProjects);

        // when
        var actualResultPage = hibernateSearchService.searchProjects(givenSimpleQuery, givenStatusSet, givenPageable);

        // then
        assertThat(actualResultPage).isEqualTo(expectedResultPage);
    }

    @Test
    public void searchUserDataReturnsEmptyListWhenNoUsersGiven() {
        // given / when / then
        assertThat(hibernateSearchService.searchUserData(List.of(), ""))
                .isEmpty();
    }

    @Test
    public void searchUserDataReturnsExpectedResult() {
        // given
        var simpleQuery = "Jane | Doe";

        var expectedUserIdQuery = mock(Query.class);
        var expectedFieldQuery = mock(Query.class);
        var expectedCompleteQuery = mock(Query.class);

        var expectedSearchResult = List.of(mock(UserData.class));
        var firstUserId = "jane";
        var secondUserId = "peter";
        var userIdSimpleQuery = secondUserId + " | " + firstUserId;
        var firstUser = mock(User.class);
        var secondUser = mock(User.class);
        given(firstUser.getId()).willReturn(firstUserId);
        given(secondUser.getId()).willReturn(secondUserId);

        var firstField = "firstName";
        var otherFields = new String[] { "lastName" };

        var userIdMatchingContext = createSimpleQueryStringMatchingContext(userIdSimpleQuery, expectedUserIdQuery);
        var fieldMatchingContext = createSimpleQueryStringMatchingContext(simpleQuery, expectedFieldQuery);
        configureGetQueryBuilderToReturnBuilderForType(UserData.class);
        configureQueryBuilderSimpleQueryString("user_id", userIdMatchingContext);
        configureQueryBuilderSimpleQueryString(firstField, otherFields, fieldMatchingContext);

        given(queryBuilderMock.bool()).willReturn(booleanJunctionMock);
        given(booleanJunctionMock.must(expectedUserIdQuery)).willReturn(mustJunctionMock);
        given(mustJunctionMock.must(expectedFieldQuery)).willReturn(mustJunctionMock);
        given(mustJunctionMock.createQuery()).willReturn(expectedCompleteQuery);

        given(fullTextEntityManagerMock.createFullTextQuery(expectedCompleteQuery, UserData.class)).willReturn(fullTextQueryMock);
        given(fullTextQueryMock.getResultList()).willReturn(expectedSearchResult);

        // when
        var actualSearchResult = hibernateSearchService.searchUserData(List.of(firstUser, secondUser), simpleQuery);

        // then
        assertThat(actualSearchResult).isEqualTo(expectedSearchResult);
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
